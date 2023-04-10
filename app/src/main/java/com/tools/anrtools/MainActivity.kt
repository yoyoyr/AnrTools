package com.tools.anrtools

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.bigAnr).setOnClickListener {

        }

        findViewById<Button>(R.id.manyAnr).setOnClickListener {

        }

        findViewById<Button>(R.id.saveMsg).setOnClickListener {

        }
    }
}