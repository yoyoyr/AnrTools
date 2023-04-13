package com.anr.tools

import android.os.SystemClock
import java.io.Serializable

class MessageLruCache<V> : Serializable {

    //默认5分钟
    private var offsetTime = MESSAGE_CACHE_TIME

    private var lastPutTime: Long = 0

    /**xs
     * 存储下最后一个元素，方便快速获取
     */
    private var lastValue: V? = null

    //按插入顺序保存 依次移除时间最早的
    private val linkedHashMap = MessageLinkedHasMhMap(0, 0.75f, false)

    fun put(value: V) {
        put(SystemClock.elapsedRealtime(), value)
    }

    fun put(baseTime: Long, value: V) {
        linkedHashMap[baseTime] = value
        lastPutTime = baseTime
        lastValue = value
    }


    fun getAll(): List<V> {
        val list: MutableList<V> = ArrayList()

        for (entry in linkedHashMap.entries) { //
            list.add(entry.value)
        }
        return list
    }

    /**
     * 存储下最后一个元素，方便快速获取
     */
    fun getLastValue(): V? {
        return lastValue
    }


    private inner class MessageLinkedHasMhMap(
        initialCapacity: Int,
        loadFactor: Float,
        accessOrder: Boolean
    ) :
        LinkedHashMap<Long, V>(initialCapacity, loadFactor, accessOrder) {
        override fun removeEldestEntry(eldest: Map.Entry<Long, V>): Boolean {
            val iterator = linkedHashMap.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (entry === eldest) {
                    var temp: MutableMap.MutableEntry<Long, V>? = null
                    if (iterator.hasNext()) {
                        temp = iterator.next()
                    }
                    //在去除第一个的时候，剩下的数据也大于指定时间
                    return temp != null && lastPutTime - temp.key > offsetTime
                }
            }
            return false
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
