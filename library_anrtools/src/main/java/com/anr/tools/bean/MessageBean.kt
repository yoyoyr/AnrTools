package com.anr.tools.bean

import java.io.Serializable


/**
 * 单个Message信息
 */
class MessageBean : Serializable {
    lateinit var handleName: String
        private set

    /**
     * 内存地址
     */
    lateinit var handlerAddress: String
        private set
    lateinit var callbackName: String
        private set
    var messageWhat = 0
        private set
    var msgId: Long = 0

    constructor()

    constructor(
        handleName: String,
        callbackName: String,
        messageWhat: Int,
        handlerAddress: String
    ) {
        this.handleName = handleName
        this.callbackName = callbackName
        this.messageWhat = messageWhat
        this.handlerAddress = handlerAddress
    }

    override fun toString(): String {
        return "MessageBean{" +
                "handleName='" + handleName + '\'' +
                ", handlerAddress='" + handlerAddress + '\'' +
                ", callbackName='" + callbackName + '\'' +
                ", messageWhat=" + messageWhat +
                ", msgId=" + msgId +
                '}'
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}