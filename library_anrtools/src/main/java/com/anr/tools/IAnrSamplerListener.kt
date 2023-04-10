package com.anr.tools

interface IAnrSamplerListener {

    /**
     * 收集消息队列中未处理的消息
     */
    fun onMessageQueueSample(baseTime: Long, msgId: String, msg: String)

    fun onCpuSample(baseTime: Long, msgId: String, msg: String)

    fun onMemorySample(baseTime: Long, msgId: String, msg: String)

    fun onMainThreadStackSample(baseTime: Long, msgId: String, msg: String)


    /**
     * 当前主线程的调度能力
     *
     * @param start true 发起本次调度  false结束
     */
    fun onScheduledSample(start: Boolean, baseTime: Long, msgId: String, dealt: Long)

    /**
     * 采集消息队列每次处理的消息  当消息类型是anr的时候，调用者不是主线程
     */
    fun onMsgSample(baseTime: Long, msgId: String, msg: MessageInfo)

    fun onJankSample(msgId: String, msg: MessageInfo)

    /**
     * 消息队列中发生anr的消息已经处理完毕
     */
    fun messageQueueDispatchAnrFinish()


    fun onSampleAnrMsg()
}