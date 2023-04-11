package com.anr.tools.bean

import android.os.SystemClock
import androidx.annotation.IntDef
import java.io.Serializable

//聚合消息类
class PolMessageBean : Serializable {
    @IntDef(
        MSG_TYPE_NONE,
        MSG_TYPE_INFO,
        MSG_TYPE_WARN,
        MSG_TYPE_ANR,
        MSG_TYPE_JANK,
        MSG_TYPE_GAP,
        MSG_TYPE_ACTIVITY_THREAD_H
    )
    private annotation class MsgType

    @MsgType
    var msgType = MSG_TYPE_INFO

    /**
     * 至少有一条消息
     */
    var count = 0

    /**
     * 消息分发耗时
     */
    var wallTime: Long = 0

    /**
     * SystemClock.currentThreadTimeMillis()  是当前线程方法的执行时间，不包含线程休眠 或者锁竞争等待
     * cpu 时间是函数正真执行时间
     */
    var cpuTime: Long = 0
    var boxMessages = mutableListOf<MessageBean>()

    /**
     * 消息被创建的时间
     */
    var messageCreateTime = SystemClock.elapsedRealtime()

    override fun toString(): String {
        return "\nMessageInfo{\n" +
                "   msgType=${msgTypeToString(msgType)}, count=$count, " +
                "\n   wallTime=$wallTime, cpuTime=$cpuTime, " +
                "\n   boxMessages=$boxMessages" +
                "      \n}\n"
    }

    companion object {
        private const val serialVersionUID = 1L
        const val MSG_TYPE_NONE = 0x00
        const val MSG_TYPE_INFO = 0x01
        const val MSG_TYPE_WARN = 0x02
        const val MSG_TYPE_ANR = 0x04

        /**
         * 掉帧
         */
        const val MSG_TYPE_JANK = 0x08

        /**
         * 连续两个消息之间的间隙
         */
        const val MSG_TYPE_GAP = 0x10

        /**
         * 通过ActivityThread$H handle 发送的消息
         */
        const val MSG_TYPE_ACTIVITY_THREAD_H = 0x20
        fun msgTypeToString(@MsgType msgType: Int): String {
            when (msgType) {
                MSG_TYPE_NONE -> return "NONE"
                MSG_TYPE_INFO -> return "INFO"
                MSG_TYPE_WARN -> return "WARN"
                MSG_TYPE_ANR -> return "ANR"
                MSG_TYPE_JANK -> return "ANK"
                MSG_TYPE_GAP -> return "GAP"
                MSG_TYPE_ACTIVITY_THREAD_H -> return "ACTIVITY_THREAD_H"
            }
            return ""
        }
    }
}
