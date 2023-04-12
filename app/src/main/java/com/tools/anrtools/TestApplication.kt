package com.tools.anrtools


import android.app.ActivityManager
import android.os.Process
import com.anr.tools.BaseApplication
import com.anr.tools.util.LoggerUtils
import com.anr.tools.MainLooperMonitor


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
            MainLooperMonitor.getInstance().startMonitor()
        }

    }
}
