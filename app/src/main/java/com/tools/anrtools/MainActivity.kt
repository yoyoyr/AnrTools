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

class MainActivity : AppCompatActivity() {

    var mainHandler = Handler(Looper.getMainLooper())

    var clickTime = SystemClock.elapsedRealtime()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.bigAnr).setOnClickListener {
            mainHandler.post(Runnable {
                SystemClock.sleep(1000)
                LoggerUtils.LOGV("send message...")
            })
        }

        findViewById<Button>(R.id.manyAnr).setOnClickListener {

            //发送多个不是非常严重耗时消息，模拟消息队列繁忙
            for (i in 1..10) {
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
            MainLooperMonitor.getInstance().saveMessage()
            clickTime = SystemClock.elapsedRealtime()
        }
    }

}