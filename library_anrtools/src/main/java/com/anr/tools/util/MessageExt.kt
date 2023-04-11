package com.anr.tools

import com.anr.tools.bean.MessageBean
import com.google.gson.GsonBuilder
import java.io.File


/**
 * 处理Looper 发出的消息  消息样例： >>>>> Dispatching to " + msg.target + " " +
 * msg.callback + ": " + msg.what
 * >>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler) {3346d43} com.example.test.MainActivity$1@7250fab: 0
 */
fun String.parseLooperStart(): MessageBean {
    var msg = this
    var boxMessage: MessageBean
    try {
        msg = msg.trim { it <= ' ' }
        var msgA = msg.split(":".toRegex()).toTypedArray()
        val what = msgA[1].trim { it <= ' ' }.toInt()
        //>>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler) {3346d43} com.example.test.MainActivity$1@7250fab
        msgA = msgA[0].split("\\{.*\\}".toRegex()).toTypedArray()
        val callback = msgA[1]
        //>>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler)
        msgA = msgA[0].split("\\(".toRegex()).toTypedArray()
        msgA = msgA[1].split("\\)".toRegex()).toTypedArray()
        val handler = msgA[0]
        msgA = msg.split("\\{".toRegex()).toTypedArray()
        msgA = msgA[1].split("\\}".toRegex()).toTypedArray()
        boxMessage = MessageBean(handler, callback, what, msgA[0])
    } catch (e: Exception) {
        e.printStackTrace()
        boxMessage = MessageBean()
    }
    return boxMessage
}

fun Any.tojJson(): String {
    return if (this == null) {
        "{}"
    } else try {
        GsonBuilder().create().toJson(this)
    } catch (e: Throwable) {
        e.printStackTrace()
        "{}"
    }
}


/**
 * 判断某条消息是不是在更新ui
 */
fun MessageBean.isBoxMessageDoFrame() =
    "android.view.Choreographer\$FrameHandler" == handleName && callbackName.contains("android.view.Choreographer\$FrameDisplayEventReceiver")


fun MessageBean.isBoxMessageActivityThread() = "android.app.ActivityThread\$H" == handleName


fun File.deleteFile(): Boolean {
    if (exists()) {
        if (isDirectory) {
            val files = listFiles()
            for (value in files) {
                return value.deleteFile()
            }
        }
        return delete()
    }
    return false
}

fun AutoCloseable.closeStream() {
    try {
        close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}