package com.tools.anrtools

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.anr.tools.util.LoggerUtils

class SecondService : Service() {

    override fun onCreate() {
        super.onCreate()
        LoggerUtils.LOGV("service onCreate...")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}