package com.anr.tools

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Printer
import android.view.Choreographer

class MessageMonitor : Printer {

    private val start = false
    private val applicationContext: Context? = null

    /**
     * 每一帧的时间
     */
    private val mFrameIntervalNanos = 16f
    private val noInit: Long = -1
    private val startTime = noInit
    private var msgStartTime: Long = noInit
    private var lastMsgEnd: Long = noInit
    private val cpuStartTime = noInit
    private var lastCpuEnd: Long = noInit

    /**
     * 超过这个时间 就发生anr
     */
    private val monitorAnrTime = noInit

    /**
     * 超过这个时间 就发生anr
     */
    private var monitorMsgId: Long = 0


    /**
     * 每次消息处理完成后需要置空
     */
    private val messageInfo: MessageInfo? = null
    private val currentMsg: BoxMessage? = null

    private val config: BlockBoxConfig? = null

//    /**
//     * 正常采集
//     * */
//    private SamplerListenerChain sampleListener = new SamplerListenerChain();

    //    /**
    //     * 正常采集
    //     * */
    //    private SamplerListenerChain sampleListener = new SamplerListenerChain();
    private val mainHandler = Handler(Looper.getMainLooper())
    private val checkId: Long = -1

    override fun println(x: String?) {

    }
}