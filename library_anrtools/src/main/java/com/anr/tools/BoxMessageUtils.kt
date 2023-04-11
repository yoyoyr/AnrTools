package com.anr.tools


object BoxMessageUtils {
    /**
     * 处理Looper 发出的消息  消息样例： >>>>> Dispatching to " + msg.target + " " +
     * msg.callback + ": " + msg.what
     * >>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler) {3346d43} com.example.test.MainActivity$1@7250fab: 0
     */
    fun parseLooperStart(msg: String): BoxMessage {
        var msg = msg
        var boxMessage: BoxMessage
        try {
            msg = msg.trim { it <= ' ' }
            var msgA = msg.split(":".toRegex()).toTypedArray()
            val what = msgA[1].trim { it <= ' ' }.toInt()
            //>>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler) {3346d43} com.example.test.MainActivity$1@7250fab
            msgA = msgA[0].split("\\{.*\\}".toRegex()).toTypedArray()
            val callback = msgA[1]
            //>>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler)
            msgA = msgA[0].split("\\(".toRegex()).toTypedArray()
            msgA = msgA[1].split("\\)".toRegex()).toTypedArray()
            val handler = msgA[0]
            msgA = msg.split("\\{".toRegex()).toTypedArray()
            msgA = msgA[1].split("\\}".toRegex()).toTypedArray()
            boxMessage = BoxMessage(handler, callback, what, msgA[0])
        } catch (e: Exception) {
            e.printStackTrace()
            boxMessage = BoxMessage()
        }
        return boxMessage
    }

    /**
     * 判断某条消息是不是在更新ui
     */
    fun isBoxMessageDoFrame(message: BoxMessage?): Boolean {
        return message != null && "android.view.Choreographer\$FrameHandler" == message.handleName && message.callbackName!!.contains("android.view.Choreographer\$FrameDisplayEventReceiver")
    }

    fun isBoxMessageActivityThread(message: BoxMessage?): Boolean {
        return message != null && "android.app.ActivityThread\$H" == message.handleName
    }
}
