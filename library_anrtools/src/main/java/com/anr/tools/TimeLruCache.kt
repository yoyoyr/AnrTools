package com.anr.tools

import android.os.SystemClock
import java.io.Serializable


/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：依据时间作为偏差，存储指定时间范围内的节点
 */
class TimeLruCache<V> : Serializable {
    /**
     * 默认30s  单位是ms
     */
    private var offsetTime = (30 * 1000).toLong()
    private var lastPutTime: Long = 0

    /**xs
     * 存储下最后一个元素，方便快速获取
     */
    private var lastValue: V? = null

    //按插入顺序保存 依次移除时间最早的
    private val linkedHashMap = TimeLinkedHashMap(0, 0.75f, false)

    constructor() {}

    /**
     * 当最后一个和第一个时间偏差超过该值的时候，会将LinkedHashMap 中链表表头的元素移除
     */
    constructor(offsetTime: Long) {
        this.offsetTime = offsetTime
    }

    fun put(value: V) {
        put(SystemClock.elapsedRealtime(), value)
    }

    fun put(baseTime: Long, value: V) {
        linkedHashMap.put(baseTime, value)
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


    private inner class TimeLinkedHashMap(
        initialCapacity: Int,
        loadFactor: Float,
        accessOrder: Boolean
    ) :
        LinkedHashMap<Long, V>(initialCapacity, loadFactor, accessOrder) {
        override fun removeEldestEntry(eldest: Map.Entry<Long, V>): Boolean {
            //这样会不会导致存储的数据不够 offsetTime ？
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
