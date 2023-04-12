package com.anr.tools.bean

import java.io.Serializable


class ScheduledBean(overTime: Long, monitorMsgId: Long, start: Boolean) : Serializable {

    //与预测时间的偏差，如预设时间是300ms，实际时间为305ms，则dealt值为5ms
    var overTime = NO_DEALT

    var monitorMsgId = 0L


    //当前调度是否处理完成，如果处理完成说明主线程很久都没有处理对应的回调
    var isDone = false

    companion object {
        const val NO_DEALT: Long = -1
        private const val serialVersionUID = 1L
    }

    init {
        this.overTime = overTime
        isDone = start
        this.monitorMsgId = monitorMsgId
    }

    override fun toString(): String {
        return "ScheduledInfo(dealt=$overTime, monitorMsgId=$monitorMsgId, isStart=$isDone)"
    }

}
