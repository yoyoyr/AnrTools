package com.anr.tools

import android.util.Printer
import com.anr.tools.util.FileUtil
import com.anr.tools.util.LoggerUtils

//记录后续message内容
class MessageQueuePrint : Printer {

    override fun println(msg: String) {
        LoggerUtils.LOGV("sdfsdfsdf $msg")
        FileUtil.getInstance().onMessageQueueSample(msg)
    }
}