package com.anr.tools

import java.io.Serializable


/**
 * 分发的Message  相关信息
 */
class BoxMessage : Serializable {
    var handleName: String? = null
        private set

    /**
     * 内存地址
     */
    var handlerAddress: String? = null
        private set
    var callbackName: String? = null
        private set
    var messageWhat = 0
        private set
    var msgId: Long = 0

    constructor() {}
    constructor(
        handleName: String?,
        callbackName: String?,
        messageWhat: Int,
        handlerAddress: String?
    ) {
        this.handleName = handleName
        this.callbackName = callbackName
        this.messageWhat = messageWhat
        this.handlerAddress = handlerAddress
    }

    override fun toString(): String {
        return "BoxMessage{" +
                "handleName='" + handleName + '\'' +
                ", handlerAddress='" + handlerAddress + '\'' +
                ", callbackName='" + callbackName + '\'' +
                ", messageWhat=" + messageWhat +
                ", msgId=" + msgId +
                '}'
    }

    companion object {
        const val SEPARATOR = "\r\n"
        private const val serialVersionUID = 1L
    }
}
