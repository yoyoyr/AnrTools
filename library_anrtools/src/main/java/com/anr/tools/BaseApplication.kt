package com.anr.tools

import android.app.ActivityManager
import android.app.ActivityManager.ProcessErrorStateInfo
import android.app.Application
import android.content.Context
import android.os.Process
import android.os.SystemClock
import com.anr.tools.util.LoggerUtils
import com.tencent.matrix.AppActiveMatrixDelegate
import com.tencent.matrix.Matrix
import com.tencent.matrix.plugin.Plugin
import com.tencent.matrix.plugin.PluginListener
import com.tencent.matrix.report.Issue
import com.tencent.matrix.trace.TracePlugin
import com.tencent.matrix.trace.config.SharePluginInfo
import com.tencent.matrix.trace.config.TraceConfig
import com.tencent.matrix.trace.constants.Constants
import org.json.JSONException
import java.util.logging.Logger

open class BaseApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        context = base
    }

    override fun onCreate() {
        super.onCreate()


        val config = TraceConfig.Builder()
            .anrTracePath(cacheDir.path + "/trace.txt")
            .printTracePath(cacheDir.path + "/print_trace.txt")
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
            try {
                if (issue.content
                        .get(SharePluginInfo.ISSUE_STACK_TYPE) == Constants.Type.SIGNAL_ANR
                ) {
                    val procs =
                        (context.getSystemService(ACTIVITY_SERVICE) as ActivityManager).processesInErrorState
                    for (proc in procs) {
                        if (proc.uid != Process.myUid()
                            && proc.condition == ProcessErrorStateInfo.NOT_RESPONDING
                        ) {
                            LoggerUtils.LOGV("maybe received other apps ANR signal")
                            break
                        }
                        if (proc.pid != Process.myPid()) continue
                        if (proc.condition != ProcessErrorStateInfo.NOT_RESPONDING) {
                            continue
                        }
                        LoggerUtils.LOGV("error sate longMsg = %s", proc.longMsg)
                    }
                    val scene = AppActiveMatrixDelegate.INSTANCE.getVisibleScene()
                    LoggerUtils.LOGV("sence = %s", scene)
//                            LoggerUtils.LOGV("start long =" + (SystemClock.elapsedRealtime() - com.tencent.matrix.test.memoryhook.App.startTime)

                }
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
        }
    }

    companion object {
        lateinit var context: Context
    }
}