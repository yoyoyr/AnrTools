package com.anr.tools.util

import android.util.Log
import android.util.Printer
import java.util.*


object LoggerUtils : Printer {


    var LOGGING_ENABLED = true//BuildConfig.DEBUG
    var THREAD_INFO_ENABLED = true//BuildConfig.DEBUG
    private const val LOG_PREFIX = "u_"
    private const val LOG_PREFIX_LENGTH = LOG_PREFIX.length
    private const val MAX_LOG_TAG_LENGTH = 100

    override fun println(x: String) {
        LOGD(x)
    }

    fun makeLogTag(str: String): String {
        return if (str.length > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            LOG_PREFIX + str.substring(
                0,
                MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1
            )
        } else LOG_PREFIX + str
    }

    private val callerStackTraceElement: StackTraceElement
        private get() = Thread.currentThread().stackTrace[4]

    private fun makeLogTag(caller: StackTraceElement): String {
        var className = caller.className
        className = className.substring(className.lastIndexOf(".") + 1)
        return if (THREAD_INFO_ENABLED) {
            String.format(
                Locale.CHINA,
                "$className.${caller.methodName}(L:${caller.lineNumber},T:${Thread.currentThread().name})"
            )
        } else {
            String.format(
                Locale.CHINA, "$className.${caller.methodName}(L:${caller.lineNumber})"
            )
        }
    }

    /**
     * Don't use this when obfuscating class names!
     */
    @JvmStatic
    fun makeLogTag(cls: Class<*>): String {
        return makeLogTag(cls.simpleName)
    }


    @JvmStatic
    fun LOGD(tag: String, message: String) {
        if (LOGGING_ENABLED) {
            if (Log.isLoggable(tag, Log.DEBUG)) {
                Log.d(tag, message)
            }
        }
    }

    @JvmStatic
    fun LOGD(
        tag: String,
        message: String,
        cause: Throwable
    ) {
        if (LOGGING_ENABLED) {
            if (Log.isLoggable(tag, Log.DEBUG)) {
                Log.d(tag, message, cause)

            }
        }
    }

    @JvmStatic
    fun LOGV(tag: String, message: String) {
        if (LOGGING_ENABLED) {
            if (Log.isLoggable(tag, Log.VERBOSE)) {
                Log.v(tag, message)
            }
        }
    }

    @JvmStatic
    fun LOGV(
        tag: String,
        message: String,
        cause: Throwable
    ) {
        if (LOGGING_ENABLED) {
            if (Log.isLoggable(tag, Log.VERBOSE)) {
                Log.v(tag, message, cause)

            }
        }
    }

    @JvmStatic
    fun LOGI(tag: String, message: String) {
        if (LOGGING_ENABLED) {
            Log.i(tag, message!!)

        }
    }

    @JvmStatic
    fun LOGI(
        tag: String,
        message: String,
        cause: Throwable
    ) {
        if (LOGGING_ENABLED) {
            Log.i(tag, message, cause)

        }
    }

    @JvmStatic
    fun LOGW(tag: String, message: String) {
        Log.w(tag, message)

    }

    @JvmStatic
    fun LOGW(
        tag: String,
        message: String,
        cause: Throwable
    ) {
        Log.w(tag, message, cause)

    }

    @JvmStatic
    fun LOGE(tag: String, message: String) {
        Log.e(tag, message)

    }

    @JvmStatic
    fun LOGE(message: String, cause: Throwable) {
        val tag =
            makeLogTag(callerStackTraceElement)
        Log.e(tag, message, cause)

    }

    @JvmStatic
    fun LOGE(cause: Throwable) {
        val tag =
            makeLogTag(callerStackTraceElement)
        Log.e(tag, "", cause)

    }

    @JvmStatic
    fun LOGW(cause: Throwable) {
        val tag =
            makeLogTag(callerStackTraceElement)
        Log.w(tag, "", cause)

    }

    @JvmStatic
    fun LOGD(message: String) {
        if (LOGGING_ENABLED) {
            val tag =
                makeLogTag(callerStackTraceElement)
            Log.d(tag, message)

        }
    }

    @JvmStatic
    fun LOGV(message: String) {
        if (LOGGING_ENABLED) {
            var msg = message
            val tag =
                makeLogTag(callerStackTraceElement)
            //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
            //  把4*1024的MAX字节打印长度改为2001字符数
            val max_str_length = 1900
            //大于4000时
            //大于4000时
            while (msg.length > max_str_length) {
                Log.v("=", msg.substring(0, max_str_length))
                msg = msg.substring(max_str_length)
            }
            Log.v(tag, msg)

        }
    }

    @JvmStatic
    fun LOGI(message: String) {
        if (LOGGING_ENABLED) {
            val tag =
                makeLogTag(callerStackTraceElement)
            Log.i(tag, message)

        }
    }

    @JvmStatic
    fun LOGE(message: String) {
        val tag =
            makeLogTag(callerStackTraceElement)
        Log.e(tag, message)

    }

    @JvmStatic
    fun LOGW(message: String) {
        val tag =
            makeLogTag(callerStackTraceElement)
        Log.w(tag, message)

    }
}