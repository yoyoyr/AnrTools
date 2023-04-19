package com.anr.tools

import android.app.Application
import android.content.Context
import android.os.Environment
import android.os.SystemClock
import com.anr.tools.util.LoggerUtils
import com.anr.tools.util.saveFile
import com.tencent.matrix.Matrix
import com.tencent.matrix.plugin.Plugin
import com.tencent.matrix.plugin.PluginListener
import com.tencent.matrix.report.Issue
import com.tencent.matrix.trace.TracePlugin
import com.tencent.matrix.trace.config.SharePluginInfo
import com.tencent.matrix.trace.config.TraceConfig
import com.tencent.matrix.trace.constants.Constants

open class BaseApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        context = base

        val externalStorageAvailable =
            Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        cachePath = if (externalStorageAvailable) {
            context.externalCacheDir?.path ?: context.cacheDir.path
        } else {
            context.cacheDir.path
        }
    }

    override fun onCreate() {
        super.onCreate()


        val config = TraceConfig.Builder()
            .anrTracePath("$cachePath/trace.txt")
            .enableSignalAnrTrace(true)
            .build()
        val tracePlugin = TracePlugin(config)

        val matrix = Matrix.Builder(this)
            .plugin(tracePlugin)
            .pluginListener(pluginListener)
            .build()
        Matrix.init(matrix)

        tracePlugin.start()
    }

    private val pluginListener = object : PluginListener {
        override fun onInit(plugin: Plugin) {}
        override fun onStart(plugin: Plugin) {}
        override fun onStop(plugin: Plugin) {}
        override fun onDestroy(plugin: Plugin) {}
        override fun onReportIssue(issue: Issue) {
            LoggerUtils.LOGV("issue  = $issue")
            val type = issue.content.get(SharePluginInfo.ISSUE_STACK_TYPE)
            if (type == Constants.Type.SIGNAL_ANR
                || type == Constants.Type.SIGNAL_ANR_NATIVE_BACKTRACE //__SIGRTMIN + 3 信号对应的值
//                || type == Constants.Type.ANR
            ) {
                USE_TIME = (SystemClock.elapsedRealtime() - START_TIME) / 1000
                MainLooperMonitor.getInstance().saveMessage()
                issue.saveFile("$cachePath/anr_info.txt")
                LoggerUtils.LOGV("消息队列信息 : ${cachePath}/block_anr")
                LoggerUtils.LOGV("trace文件 : $cachePath/trace.txt")
                LoggerUtils.LOGV("anr_info文件 : $cachePath/anr_info.txt")
            }
        }
    }

    companion object {
        lateinit var context: Context

        lateinit var cachePath: String

        val START_TIME = SystemClock.elapsedRealtime()

        var USE_TIME = -1L
    }
}