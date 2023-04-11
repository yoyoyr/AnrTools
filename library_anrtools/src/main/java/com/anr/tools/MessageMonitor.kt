package com.anr.tools

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.util.Printer
import com.anr.tools.ui.DisplayUtils
import java.util.concurrent.atomic.AtomicBoolean

open class MessageMonitor private constructor() : Printer {
    private val TAG: String = "MessageMonitor"

    @Volatile
    private var start = false

    /**
     * 每一帧的时间
     */
    private val noInit: Long = -1
    private var startTime = noInit
    private var msgStartTime: Long = noInit
    private var lastMsgEnd: Long = noInit
    private var cpuStartTime = noInit
    private var lastCpuEnd: Long = noInit

    /**
     * 超过这个时间 就发生anr
     */
    private var monitorAnrTime = noInit

    /**
     * 超过这个时间 就发生anr
     */
    private var monitorMsgId: Long = 0


    /**
     * 每次消息处理完成后需要置空
     */
    private var messageInfo: MessageInfo? = null
    private lateinit var currentMsg: BoxMessage

    private lateinit var config: BlockBoxConfig

    private val mainHandler = Handler(Looper.getMainLooper())
    private var checkId: Long = -1

    //调用println 是奇数次还是偶数  默认false 偶数  true 奇数
    private val odd = AtomicBoolean(false)

    override fun println(x: String) {
        if (x.contains("<<<<< Finished to") && !odd.get()) {
            return
        }
        //原来是偶数次，那么这次进来就是奇数
        if (!odd.get()) {
            msgStart(x)
        } else {
            msgEnd()
        }
        odd.set(!odd.get())
    }

    private fun msgStart(msg: String) {
        msgStartTime = SystemClock.elapsedRealtime()
        monitorAnrTime = msgStartTime + config.getAnrTime()
        currentMsg = BoxMessageUtils.parseLooperStart(msg)
        currentMsg.msgId = monitorMsgId
        cpuStartTime = SystemClock.currentThreadTimeMillis()
        //两次消息的时间间隔较大，如前一条消息处理完了以后等待了300ms才来下一条消息
        //单独处理消息且增加一个gap消息 不应该存在两个连续的gap消息
        if (msgStartTime - lastMsgEnd > config.getGapTime() && lastMsgEnd != noInit) {
            handleMsg()
            messageInfo = MessageInfo().apply {
                msgType = MessageInfo.MSG_TYPE_GAP
                wallTime = msgStartTime - lastMsgEnd
                cpuTime = cpuStartTime - lastCpuEnd
            }

            startTime = msgStartTime
        }
        if (messageInfo == null) {
            messageInfo = MessageInfo()
            startTime = SystemClock.elapsedRealtime()
            msgStartTime = startTime
            cpuStartTime = SystemClock.currentThreadTimeMillis()
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
            lastMsgEnd = SystemClock.elapsedRealtime()
            lastCpuEnd = SystemClock.currentThreadTimeMillis()
            val msgDealtTime: Long = lastMsgEnd - msgStartTime
            //判断是否是ActivityThread.H的消息
            val msgActivityThread: Boolean = BoxMessageUtils.isBoxMessageActivityThread(currentMsg)
            if (msgDealtTime > config.getWarnTime() //单条消息时间达到警告条件的
                || msgActivityThread
            ) { //ActivityThread的消息
                if (messageInfo!!.count > 1) { //先处理原来的信息包
                    messageInfo!!.msgType = MessageInfo.MSG_TYPE_INFO
                    handleMsg()
                }
                messageInfo = MessageInfo().apply {
                    wallTime = lastMsgEnd - msgStartTime
                    cpuTime = lastCpuEnd - cpuStartTime
                    boxMessages.add(currentMsg)
                    msgType = MessageInfo.MSG_TYPE_WARN
                    if (msgDealtTime > config.getAnrTime()) {
                        msgType = MessageInfo.MSG_TYPE_ANR
                    } else if (msgActivityThread) {
                        msgType = MessageInfo.MSG_TYPE_ACTIVITY_THREAD_H
                    }
                }

                handleMsg()
            } else {
                //统计每一次消息分发耗时 他们的叠加就是总耗时
                messageInfo?.run {
                    wallTime += lastMsgEnd - startTime
                    //生成消息的时候，当前线程总的执行时间
                    cpuTime += lastCpuEnd - cpuStartTime
                    boxMessages.add(currentMsg)
                    count++
                    if (wallTime > config.getWarnTime()) {
                        handleMsg()
                    }
                }
            }
            monitorMsgId++
        }
    }


    @Synchronized
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
    @Synchronized
    fun stopMonitor(): MessageMonitor {
        Looper.getMainLooper().setMessageLogging(null)
        mainHandler.removeCallbacksAndMessages(null)
        start = false
        checkId = -1
        return this
    }

    //保存msg信息
    private fun handleMsg() {
        if (messageInfo != null) {
            val temp = messageInfo!!
            messageInfo = null
            var msgId = 0L
            if (temp.boxMessages.size !== 0) {
                msgId = temp.boxMessages[0].msgId
            }

            LoggerUtils.LOGV(
                "add msg wallTime other wallTime : " + temp.wallTime + "  cpuTime " + temp.cpuTime + "   MSG_TYPE : " + MessageInfo.msgTypeToString(
                    temp.msgType
                ) + "  msgId " + msgId
            )

            FileSample.getInstance().onMsgSample(
                SystemClock.elapsedRealtimeNanos(),
                monitorMsgId.toString() + "",
                temp
            )
        }
    }

    companion object {
        fun getInstance(config: BlockBoxConfig) = MessageMonitor().apply {
            this.config = config
            DisplayUtils.showAnalyzeActivityInLauncher(
                BaseApplication.context,
                config.isUseAnalyze()
            )
        }

    }

}