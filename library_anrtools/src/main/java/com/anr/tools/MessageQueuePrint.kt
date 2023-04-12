package com.anr.tools

import android.util.Printer

//记录后续message内容
class MessageQueuePrint : Printer {

    override fun println(msg: String) {
        MessageCache.getInstance().onMessageQueueSample(msg)
    }
}