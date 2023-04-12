package com.anr.tools

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Printer
import com.anr.tools.bean.MessageBean
import com.anr.tools.bean.MessageListBean
import com.anr.tools.util.LoggerUtils
import com.anr.tools.util.isBoxMessageActivityThread
import com.anr.tools.util.parseLooperStart
import java.util.concurrent.atomic.AtomicBoolean

class MainLooperMonitor private constructor() : Printer {

    @Volatile
    private var start = false

    //每一帧的时间
    private val noInit = -1L

    //聚合消息的开始时间
    private var polMessageStartTime = noInit

    //单挑消息的开始时间
    private var messageStartTime = noInit

    //记录单条消息的cpu时间片开始时间
    private var messageCpuStartTime = noInit

    //上一条消息的结束时间
    private var lastMessageEndTime = noInit

    //上一条消息的cpu时间片结束时间
    private var lastMessageCpuEndTime = noInit

    private var monitorMsgId = 0L

    private var polMessage: MessageListBean? = null

    private lateinit var currentMessage: MessageBean

    //调用println 是奇数次还是偶数  默认false 偶数  true 奇数
    private val printCountFlg = AtomicBoolean(false)

    private var checkId: Long = -1
    private val mainHandler = Handler(Looper.getMainLooper())
    private var checkMainRunnable = object : Runnable {
        var dealtTime = SystemClock.elapsedRealtime()
        override fun run() {
            //时间偏差 差值越大说明调度能力越差
            //就采集一次。
            val offset = SystemClock.elapsedRealtime() - dealtTime - warnTime
            if (checkId > -1) {
                //需要注意的是，这个只能反映发生anr前的调度能力，会存在发生anr前最后一次调度检测没有收集到。
                MessageCache.getInstance().onScheduledSample(true, dealtTime, monitorMsgId, offset)
            }
            if (start) {
                checkId++
                dealtTime = SystemClock.elapsedRealtime()
                //                sampleListener.onScheduledSample(true, dealtTime,""+checkId,offset );
                mainHandler.postDelayed(this, warnTime)
            }
        }
    }

    override fun println(x: String) {
        if (x.contains("<<<<< Finished to") && !printCountFlg.get()) {
            return
        }
        //原来是偶数次，那么这次进来就是奇数
        if (!printCountFlg.get()) {
            msgStart(x)
        } else {
            msgEnd()
        }
        printCountFlg.set(!printCountFlg.get())
    }

    /**
     * checkMain 是否记录线程的调度能力
     */
    fun startMonitor(checkMain: Boolean = true) {
        if (start) {
            LoggerUtils.LOGW("already start")
        }
        start = true
        Looper.getMainLooper().setMessageLogging(this)
        if (checkMain) {
            mainHandler.post(checkMainRunnable)
        }

    }

    /**
     * 停止监控
     */
    fun stopMonitor() {
        Looper.getMainLooper().setMessageLogging(null)
        start = false
    }

    //将这段时间的消息保存到文件中
    fun saveMessage() {
        //获取当前消息队列的情况
        Looper.getMainLooper().dump(
            MessageQueuePrint(),
            "\n        "
        )
        MessageCache.getInstance().saveMessage()
    }

    private fun msgStart(msg: String) {
        messageStartTime = SystemClock.elapsedRealtime()
        currentMessage = msg.parseLooperStart()
        currentMessage.monitorMsgId = monitorMsgId
        messageCpuStartTime = SystemClock.currentThreadTimeMillis()
        //两次消息的时间间隔较大，如前一条消息处理完了以后等待了300ms才来下一条消息
        //单独处理消息且增加一个gap消息 不应该存在两个连续的gap消息
        if (messageStartTime - lastMessageEndTime > gapTime && lastMessageEndTime != noInit) {
            polMessage?.run {
                count++
            }
            handleMsg()
            polMessage = MessageListBean().apply {
                msgType = MessageListBean.MSG_TYPE_GAP
                wallTime = messageStartTime - lastMessageEndTime
                cpuTime = messageCpuStartTime - lastMessageCpuEndTime
            }

            polMessageStartTime = messageStartTime
        }
        if (polMessage == null) {
            polMessage = MessageListBean()
            polMessageStartTime = SystemClock.elapsedRealtime()
            messageStartTime = polMessageStartTime
            messageCpuStartTime = SystemClock.currentThreadTimeMillis()
        }
    }

    /**
     * 打包消息的条件 ：
     * 1.单条消息耗时达到warnTime，这时候需要之前的消息打成一个包，本消息打成一个包
     * 2.累计消息消耗达到warnTime
     * 3.ActivityThread的消息
     * 4.两次消息的时间间隔较大，如前一条消息处理完了以后等待了300ms才来下一条消息
     *
     */
    private fun msgEnd() {
        lastMessageEndTime = SystemClock.elapsedRealtime()
        lastMessageCpuEndTime = SystemClock.currentThreadTimeMillis()
        val msgDealtTime: Long = lastMessageEndTime - messageStartTime
        //判断是否是ActivityThread.H的消息
        val msgActivityThread = currentMessage.isBoxMessageActivityThread()
        if (msgDealtTime > warnTime //单条消息时间达到警告条件的
            || msgActivityThread//ActivityThread的消息
        ) {
            polMessage?.run {
                if (count > 1) { //先处理原来的信息包
                    msgType = MessageListBean.MSG_TYPE_INFO
                    handleMsg()
                }
            }

            polMessage = MessageListBean().apply {
                wallTime = lastMessageEndTime - messageStartTime
                cpuTime = lastMessageCpuEndTime - messageCpuStartTime
                boxMessages.add(currentMessage)
                msgType = MessageListBean.MSG_TYPE_WARN
                if (msgDealtTime > anrTime) {
                    msgType = MessageListBean.MSG_TYPE_ANR
                } else if (msgActivityThread) {
                    msgType = MessageListBean.MSG_TYPE_ACTIVITY_THREAD_H
                }
            }

            handleMsg()
        } else {
            //统计每一次消息分发耗时 他们的叠加就是总耗时
            polMessage?.run {
                wallTime += lastMessageEndTime - polMessageStartTime
                //生成消息的时候，当前线程总的执行时间
                cpuTime += lastMessageCpuEndTime - messageCpuStartTime
                boxMessages.add(currentMessage)
                count++
                if (wallTime > warnTime) {
                    handleMsg()
                }
            }
        }

        if (monitorMsgId > 1000000) {//100万条从0开始计数
            monitorMsgId = 0
        } else {
            monitorMsgId++
        }

    }

    //保存msg信息
    private fun handleMsg() {
        polMessage?.run {
            MessageCache.getInstance().onMsgSample(
                SystemClock.elapsedRealtimeNanos(),
                this
            )
            polMessage = null
        }
    }


    companion object {
        private val messageMonitor = MainLooperMonitor()

        fun getInstance() = messageMonitor
    }
}