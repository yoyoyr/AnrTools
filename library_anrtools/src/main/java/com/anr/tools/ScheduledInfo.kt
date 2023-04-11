package com.anr.tools

import java.io.Serializable


class ScheduledInfo(dealt: Long, msgId: String, start: Boolean) : Serializable {
    var dealt = NO_DEALT
    val msgId: String

    /**
     * 当前调度是否接收到了结束的信息，如果没有接收到说明主线程很久都没有处理对应的回调
     */
    var isStart = true

    companion object {
        const val NO_DEALT: Long = -1
        private const val serialVersionUID = 1L
    }

    init {
        this.dealt = dealt
        this.msgId = msgId
        isStart = start
    }
}
