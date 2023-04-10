package com.anr.tools

import android.app.Application
import android.content.Context

class BaseApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        context = base
    }

    companion object{
        lateinit var context:Context
    }
}