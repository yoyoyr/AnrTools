package com.anr.tools

import com.anr.tools.bean.PolMessageBean
import com.anr.tools.bean.MessageListBean
import com.anr.tools.bean.ScheduledBean
import com.anr.tools.util.closeStream
import com.anr.tools.util.currentTime
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class MessageCache private constructor() {

    private var anrInfo = PolMessageBean()
    private val hFormat = SimpleDateFormat("HH:mm:ss:SSS")

    fun onMsgSample(baseTime: Long, msg: MessageListBean) {
        anrInfo.messageSamplerCache.put(baseTime, msg)
    }

    fun onScheduledSample(isDone: Boolean, baseTime: Long, monitorMsgId: Long, overTime: Long) {
        anrInfo.scheduledSamplerCache.put(
            baseTime,
            ScheduledBean(overTime, monitorMsgId, isDone)
        )
    }


    fun onMessageQueueSample(msg: String) {
        anrInfo.messageQueueSample.append(msg)
    }


    fun saveMessage(anrStr: String, memoryStr: String): String {
        val temp = anrInfo
        val path = currentTime()
        temp.markTime = path
        temp.anrTime = anrStr
        temp.memory = memoryStr
        IO_EXECUTOR.execute {
            cacheData(temp)
        }

        return anrInfo.messageQueueSample.toString()
    }


    private fun cacheData(polMessageBean: PolMessageBean) {

        //如果文件不存在就创建文件

        val file = File("${BaseApplication.listMessagePath}message_list_${polMessageBean.markTime}.txt")
        if (!file.exists()) {
            file.createNewFile()
        }
        //获取输出流
        //这里如果文件不存在会创建文件，  如果文件存在，新写会覆盖以前的内容吗？
        var fos: ObjectOutputStream? = null
        try {
            fos = ObjectOutputStream(FileOutputStream(file))
            fos.writeObject(polMessageBean)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fos?.closeStream()
        }
    }

    fun restoreData(): List<PolMessageBean> {
        val result: MutableList<PolMessageBean> = ArrayList()
        val files = File(BaseApplication.listMessagePath)
        files.listFiles().forEach { file ->
            var ois: ObjectInputStream? = null
            try {
                //获取输入流
                ois = ObjectInputStream(FileInputStream(file))
                ois.run {
                    readObject().run {
                        if (this is PolMessageBean) {
                            result.add(this)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    ois?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        return result
    }


    companion object {
        private val fileSample = MessageCache()
        fun getInstance() = fileSample
    }

}

