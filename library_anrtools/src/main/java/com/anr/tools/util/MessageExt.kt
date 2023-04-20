@file:Suppress("UNNECESSARY_SAFE_CALL")

package com.anr.tools.util

import android.bluetooth.BluetoothClass.Device
import android.os.SystemClock
import com.anr.tools.BaseApplication
import com.anr.tools.BaseApplication.Companion.START_TIME
import com.anr.tools.bean.MessageBean
import com.tencent.matrix.report.Issue
import com.tencent.matrix.trace.config.SharePluginInfo
import com.tencent.matrix.util.DeviceUtil
import java.io.File
import java.io.FileWriter
import java.io.IOException


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

fun Issue.saveFile() {

    var fileWriter: FileWriter? = null
    try {
        fileWriter = FileWriter(BaseApplication.anrInfoPath)

        fileWriter.write("应用使用时长 : ${BaseApplication.USE_TIME}s")
        fileWriter.write(System.lineSeparator())

        content?.run {
            get(SharePluginInfo.ISSUE_PROCESS_FOREGROUND)?.run {
                fileWriter.write("应用是否处于前台 : $this")
                fileWriter.write(System.lineSeparator())
            }
            get(DeviceUtil.DEVICE_MACHINE)?.run {
                fileWriter.write("机器等级 : $this")
                fileWriter.write(System.lineSeparator())
            }
            fileWriter.write("java内存使用情况 : ${DeviceUtil.getDalvikHeap()}")
            fileWriter.write(System.lineSeparator())
            fileWriter.write("native内存使用情况 : ${DeviceUtil.getDalvikHeap()}")
            fileWriter.write(System.lineSeparator())
            get(SharePluginInfo.ISSUE_SCENE)?.run {
                fileWriter.write("用户的操作路径  :   $this")
                fileWriter.write(System.lineSeparator())
            }
            get(SharePluginInfo.ANR_MESSAGE)?.run {
                fileWriter.write("发生anr的消息  :   $this")
                fileWriter.write(System.lineSeparator())

            }
            get(SharePluginInfo.ISSUE_THREAD_STACK)?.run {
                fileWriter.write("触发anr的堆栈  :   $this")
                fileWriter.write(System.lineSeparator())
            }
            get(SharePluginInfo.PROCESS_ERROR_STATE_INFO)?.run {
                fileWriter.write("$this")
                fileWriter.write(System.lineSeparator())
            }
        }

    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            fileWriter?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}