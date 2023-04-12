package com.anr.tools.bean


import com.anr.tools.MessageLruCache
import java.io.Serializable


/**
 * 存储所有信息
 */
class PolMessageBean : Serializable {
    var messageSamplerCache: MessageLruCache<MessageListBean> = MessageLruCache()
    var scheduledSamplerCache: MessageLruCache<ScheduledBean> = MessageLruCache()
    var messageQueueSample = StringBuilder()
    var markTime: String = ""



    companion object {
        private const val serialVersionUID = 1L
    }

    override fun toString(): String {
        return "PolMessageBean(messageSamplerCache=${messageSamplerCache.getAll()}, scheduledSamplerCache=${scheduledSamplerCache.getAll()}, messageQueueSample=$messageQueueSample, markTime='$markTime')"
    }
}