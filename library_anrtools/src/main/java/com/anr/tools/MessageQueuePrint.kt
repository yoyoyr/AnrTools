package com.anr.tools

import android.util.Printer
import com.anr.tools.util.FileUtil

//记录后续message内容
class MessageQueuePrint : Printer {

    override fun println(msg: String) {
        FileUtil.getInstance().onMessageQueueSample(msg)
    }
}