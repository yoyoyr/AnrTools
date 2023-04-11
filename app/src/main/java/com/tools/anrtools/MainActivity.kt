package com.tools.anrtools

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anr.tools.MessageMonitor
import com.anr.tools.util.LoggerUtils

class MainActivity : AppCompatActivity() {

    var mainHandler = Handler(Looper.getMainLooper())

    var clickTime = SystemClock.elapsedRealtime()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        XXPermissions.with(this)
//            .permission(Permission.WRITE_EXTERNAL_STORAGE)
//            .permission(Permission.READ_EXTERNAL_STORAGE)
//            .request(object : OnPermissionCallback {
//                override fun onGranted(permissions: List<String?>?, all: Boolean) {
//                    if (!all) {
//                        Toast.makeText(this@MainActivity, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//
//                override fun onDenied(permissions: List<String?>?, never: Boolean) {
//                    if (never) {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "被永久拒绝授权，请手动授予文件读写权限权限",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        // 如果是被永久拒绝就跳转到应用权限系统设置页
//                    } else {
//                        Toast.makeText(this@MainActivity, "获取文件读写权限失败", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            })

        findViewById<Button>(R.id.bigAnr).setOnClickListener {

        }

        findViewById<Button>(R.id.manyAnr).setOnClickListener {

            //发送多个不是非常严重耗时消息，模拟消息队列繁忙
            for (i in 1..5) {
                mainHandler.post(Runnable { //500ms
                    SystemClock.sleep(500)
                    LoggerUtils.LOGV("send message...")
                })
            }
        }

        findViewById<Button>(R.id.saveMsg).setOnClickListener {
            if (SystemClock.elapsedRealtime() - clickTime < 100) {
                return@setOnClickListener
            }
            Toast.makeText(it.context, "保存成功", Toast.LENGTH_LONG).show()
            MessageMonitor.getInstance().saveMessage()
            clickTime = SystemClock.elapsedRealtime()
        }
    }
}