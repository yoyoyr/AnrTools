package com.tools.anrtools


import android.app.ActivityManager
import android.app.Application
import android.os.Process
import android.util.Log
import com.anr.tools.BaseApplication
import com.anr.tools.BlockBoxConfig
import com.anr.tools.LoggerUtils
import com.anr.tools.MessageMonitor


class TestApplication : BaseApplication() {
    private val TAG = "TestApplication"
    override fun onCreate() {
        super.onCreate()
        val pid = Process.myPid()
        var processName = ""
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (process in manager.runningAppProcesses) {
            if (process.pid == pid) {
                processName = process.processName
            }
        }
        if (processName == packageName) {
            LoggerUtils.LOGV("init BlockMonitor")
            MessageMonitor.getInstance(
                BlockBoxConfig.Builder()
                    .useAnalyze(true)
                    .build()
            )
                .startMonitor()
        }

    }
}