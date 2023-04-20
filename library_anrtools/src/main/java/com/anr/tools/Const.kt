package com.anr.tools

import com.anr.tools.bean.PolMessageBean
import java.util.concurrent.Executors


//保存5分钟前的消息
const val MESSAGE_CACHE_TIME = 3 * 60 * 1000L

//当某条消息处理时间超过WARN_TIME时，弹窗提示耗时且记录消息
const val WARN_TIME = 1000L


/**
 * 超过这个时间输出警告 超过这个时间消息单独列出来
 * 合并消息包的阈值。例如16个消息耗时达到300ms，则打包成一个消息包。因为有很多的短消息
 */
const val warnTime: Long = 300

//两个消息之间间隔50ms，判定为gap消息
const val gapTime: Long = 50

//anr判定时间
const val anrTime: Long = 3000

//任务执行器
val IO_EXECUTOR = Executors.newFixedThreadPool(3)

//当前分析的polMessage对象
var MSG_INFO: PolMessageBean? = null

//指定目录下最多能够存储多少个文件
const val maxSize = 20