@file:Suppress("UNNECESSARY_SAFE_CALL")

package com.anr.tools.util

import android.os.Debug
import com.anr.tools.BaseApplication
import com.anr.tools.bean.MessageBean
import com.tencent.matrix.AppActiveMatrixDelegate
import com.tencent.matrix.report.Issue
import com.tencent.matrix.trace.config.SharePluginInfo
import com.tencent.matrix.util.DeviceUtil
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat


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

fun currentTime() = SimpleDateFormat("MM-dd HH:mm:ss:SSS").format(System.currentTimeMillis())

fun simpleCurrentTime() = SimpleDateFormat("HH:mm:ss:SSS").format(System.currentTimeMillis())

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
            get(SharePluginInfo.ANR_SHORT_MSG)?.run {
                fileWriter.write("$this")
                fileWriter.write(System.lineSeparator())
            }
            get(SharePluginInfo.ANR_LONG_MSG)?.run {
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

fun MessageBean.saveFile() {

    var fileWriter: FileWriter? = null
    try {
        fileWriter = FileWriter("${BaseApplication.cachePath}/warn_message_${currentTime()}.txt")
        fileWriter.write(JsonUtil.jsonFromObject(this))
        fileWriter.write(System.lineSeparator())
        fileWriter.write(System.lineSeparator())
        fileWriter.write("页面历史 ： ${AppActiveMatrixDelegate.INSTANCE.getVisibleScene()}")
        fileWriter.write(System.lineSeparator())

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

fun Long.toKB() = this / 1024

fun Long.toMB() =
    BigDecimal(this.toDouble()).divide(BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP).toFloat()


private val MEM_TOTAL_REGEX = "MemTotal:\\s*(\\d+)\\s*kB".toRegex()
private val MEM_FREE_REGEX = "MemFree:\\s*(\\d+)\\s*kB".toRegex()
private val MEM_AVA_REGEX = "MemAvailable:\\s*(\\d+)\\s*kB".toRegex()


fun getMemoryInfo(): String {
    val memInfo = StringBuilder()
    File("/proc/meminfo").useLines {
        it.forEach { line ->
            when {
                line.startsWith("MemTotal") -> memInfo.append(
                    "系统总内存 : ${
                        MEM_TOTAL_REGEX.matchValue(
                            line
                        )
                    }KB, "
                )
                line.startsWith("MemAvailable") -> memInfo.append(
                    "可用内存 : ${
                        MEM_AVA_REGEX.matchValue(
                            line
                        )
                    }KB, "
                )
            }
        }
    }
    memInfo.append(
        ", java总内存 : ${Runtime.getRuntime().maxMemory().toKB()}KB, java以用内存 : ${
            (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()).toKB() 
        }KB."
    )
    return memInfo.toString()
}

private fun Regex.matchValue(s: String) = matchEntire(s.trim())
    ?.groupValues?.getOrNull(1)?.toLong() ?: 0L