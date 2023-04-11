package com.anr.tools

import android.os.Looper
import android.os.SystemClock
import android.util.Printer
import com.anr.tools.bean.MessageBean
import com.anr.tools.bean.PolMessageBean
import com.anr.tools.util.FileUtil
import com.anr.tools.util.LoggerUtils
import java.util.concurrent.atomic.AtomicBoolean

open class MessageMonitor private constructor() : Printer {

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

    //监控开始到目前，记录的消息个数
    private var monitorMessageCount = 0L

    private var polMessage: PolMessageBean? = null

    private lateinit var currentMessage: MessageBean

    //调用println 是奇数次还是偶数  默认false 偶数  true 奇数
    private val printCountFlg = AtomicBoolean(false)

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

    fun startMonitor() {
        if (start) {
            LoggerUtils.LOGW("already start")
        }
        start = true
        Looper.getMainLooper().setMessageLogging(this)
    }

    /**
     * 停止监控
     */
    fun stopMonitor() {
        Looper.getMainLooper().setMessageLogging(null)
        start = false
        monitorMessageCount = 0L
    }

    fun saveMessage(){
        FileUtil.getInstance().saveMessage()
    }

    private fun msgStart(msg: String) {
        messageStartTime = SystemClock.elapsedRealtime()
        currentMessage = msg.parseLooperStart()
        currentMessage.msgId = monitorMessageCount
        messageCpuStartTime = SystemClock.currentThreadTimeMillis()
        //两次消息的时间间隔较大，如前一条消息处理完了以后等待了300ms才来下一条消息
        //单独处理消息且增加一个gap消息 不应该存在两个连续的gap消息
        if (messageStartTime - lastMessageEndTime > gapTime && lastMessageEndTime != noInit) {
            handleMsg()
            polMessage = PolMessageBean().apply {
                msgType = PolMessageBean.MSG_TYPE_GAP
                wallTime = messageStartTime - lastMessageEndTime
                cpuTime = messageCpuStartTime - lastMessageCpuEndTime
            }

            polMessageStartTime = messageStartTime
        }
        if (polMessage == null) {
            polMessage = PolMessageBean()
            polMessageStartTime = SystemClock.elapsedRealtime()
            messageStartTime = polMessageStartTime
            messageCpuStartTime = SystemClock.currentThreadTimeMillis()
        }
    }

    /**
     * 打包消息的三种情况 ：
     * 1.单条消息耗时达到config.getWarnTime()，这时候需要之前的消息打成一个包，本消息打成一个包
     * 2.累计消息消耗达到config.getWarnTime()
     * 3.ActivityThread的消息
     *
     */
    private fun msgEnd() {
        synchronized(MessageMonitor::class.java) {
            lastMessageEndTime = SystemClock.elapsedRealtime()
            lastMessageCpuEndTime = SystemClock.currentThreadTimeMillis()
            val msgDealtTime: Long = lastMessageEndTime - messageStartTime
            //判断是否是ActivityThread.H的消息
            val msgActivityThread = currentMessage.isBoxMessageActivityThread()
            if (msgDealtTime > warnTime //单条消息时间达到警告条件的
                || msgActivityThread
            ) { //ActivityThread的消息
                polMessage?.run {
                    if (count > 1) { //先处理原来的信息包
                        msgType = PolMessageBean.MSG_TYPE_INFO
                        handleMsg()
                    }
                }

                polMessage = PolMessageBean().apply {
                    wallTime = lastMessageEndTime - messageStartTime
                    cpuTime = lastMessageCpuEndTime - messageCpuStartTime
                    boxMessages.add(currentMessage)
                    msgType = PolMessageBean.MSG_TYPE_WARN
                    if (msgDealtTime > anrTime) {
                        msgType = PolMessageBean.MSG_TYPE_ANR
                    } else if (msgActivityThread) {
                        msgType = PolMessageBean.MSG_TYPE_ACTIVITY_THREAD_H
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
            monitorMessageCount++
        }
    }

    //保存msg信息
    private fun handleMsg() {
        polMessage?.run {
            var msgId = 0L
            if (boxMessages.isNotEmpty()) {
                msgId = boxMessages[0].msgId
            }

            LoggerUtils.LOGV(
                "add msg wallTime other wallTime : $wallTime  cpuTime $cpuTime   MSG_TYPE : " + PolMessageBean.msgTypeToString(
                    msgType
                ) + "  msgId " + msgId
            )

            FileUtil.getInstance().onMsgSample(
                SystemClock.elapsedRealtimeNanos(),
                monitorMessageCount.toString(),
                this
            )
            polMessage = null
        }
    }


    companion object {
        private val messageMonitor = MessageMonitor()

        fun getInstance() = messageMonitor
    }
}