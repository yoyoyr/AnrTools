package com.anr.tools

import com.anr.tools.bean.InfoBean
import java.util.concurrent.Executors


/**
 * 超过这个时间输出警告 超过这个时间消息单独罗列出来
 * 合并消息包的阈值。例如16个消息耗时达到300ms，则打包成一个消息包。因为有很多的短消息
 */
const val warnTime: Long = 300

//这个值暂定50ms
const val gapTime: Long = 50

//anr判定时间
const val anrTime: Long = 3000

/**
 * 指定目录下最多能够存储多少个文件
 */
const val maxSize = 20

//任务执行器
val IO_EXECUTOR = Executors.newFixedThreadPool(3)

//当前分析的polMessage对象
var ANR_INFO: InfoBean? = null