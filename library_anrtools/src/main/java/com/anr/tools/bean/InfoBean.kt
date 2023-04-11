package com.anr.tools.bean


import com.anr.tools.ScheduledInfo
import com.anr.tools.MessageLruCache
import java.io.Serializable


/**
 * 存储所有信息
 */
class InfoBean : Serializable {
    var messageSamplerCache: MessageLruCache<PolMessageBean> = MessageLruCache(30L * 1000 * 1000000)
    var scheduledSamplerCache: MessageLruCache<ScheduledInfo> = MessageLruCache()
    var messageQueueSample = StringBuilder()
    var mainThreadStack: String? = null
    var markTime: String = ""

    companion object {
        private const val serialVersionUID = 1L
    }
}