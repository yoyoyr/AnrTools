package com.tools.anrtools

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anr.tools.MainLooperMonitor
import com.anr.tools.util.LoggerUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

class SecondActivity : AppCompatActivity() {

    var mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }

}