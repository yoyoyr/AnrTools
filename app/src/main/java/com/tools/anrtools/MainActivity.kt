package com.tools.anrtools

import android.app.Service
import android.content.Intent
import android.os.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anr.tools.MainLooperMonitor
import com.anr.tools.util.LoggerUtils
import com.anr.tools.util.getMemoryInfo
import com.anr.tools.util.toMB
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

class MainActivity : AppCompatActivity() {

    val activity = this

    var mainHandler = Handler(Looper.getMainLooper())

    var clickTime = SystemClock.elapsedRealtime()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        XXPermissions.with(this)
            .permission(Permission.WRITE_EXTERNAL_STORAGE)
            .permission(Permission.READ_MEDIA_AUDIO)
            .permission(Permission.READ_MEDIA_IMAGES)
            .permission(Permission.READ_MEDIA_VIDEO)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: List<String>, all: Boolean) {
                    if (!all) {
                        Toast.makeText(this@MainActivity, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onDenied(permissions: List<String>, never: Boolean) {
                    if (never) {
                        Toast.makeText(
                            this@MainActivity,
                            "被永久拒绝授权，请手动授予文件读写权限权限",
                            Toast.LENGTH_SHORT
                        ).show()
                        // 如果是被永久拒绝就跳转到应用权限系统设置页
                    } else {
                        Toast.makeText(this@MainActivity, "获取文件读写权限失败", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        findViewById<Button>(R.id.bigAnr).setOnClickListener {
            activity.startService(Intent(activity, SecondService::class.java))
            LoggerUtils.LOGV("start service...")
            mainHandler.post(Runnable {
                SystemClock.sleep(10000)
                LoggerUtils.LOGV("send message...")
            })
        }

        findViewById<Button>(R.id.manyAnr).setOnClickListener {
//            activity.startService(Intent(activity, SecondService::class.java))
//            LoggerUtils.LOGV("start service...")

            //发送多个不是非常严重耗时消息，模拟消息队列繁忙
            for (i in 1..50) {
                mainHandler.post(Runnable { //500ms
                    Thread.sleep(500)
                    LoggerUtils.LOGV("send message...")
                })
            }

        }

        findViewById<Button>(R.id.warnMsg).setOnClickListener {
            for (i in 1..5) {
                mainHandler.post(Runnable { //500ms
                    if (i == 3) {
                        SystemClock.sleep(1000)
                        LoggerUtils.LOGV("send message...")
                    } else {
                        SystemClock.sleep(50)
                        LoggerUtils.LOGV("send message...")
                    }

                })
            }
        }

        findViewById<Button>(R.id.activity).setOnClickListener {

            LoggerUtils.LOGV(getMemoryInfo())
            //            mainHandler.postDelayed(kotlinx.coroutines.Runnable {
//                activity.startActivity(Intent(activity, SecondActivity::class.java))
//            }, 500)
        }
    }

}