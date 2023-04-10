package com.anr.tools


import java.io.Serializable


/**
 * 存储所有Anr发生时产生的信息
 */
class AnrInfo : Serializable {
    var messageSamplerCache: TimeLruCache<MessageInfo> = TimeLruCache(30L * 1000 * 1000000)
    var scheduledSamplerCache: TimeLruCache<ScheduledInfo> = TimeLruCache()
    var messageQueueSample = StringBuilder()
    var mainThreadStack: String? = null
    var markTime: String? = null

    companion object {
        private const val serialVersionUID = 1L
        private const val OFFSET_TIME = (60 * 1000 //1分钟
                ).toLong()
    }
}