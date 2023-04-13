package com.anr.tools

import android.os.Environment
import com.anr.tools.BaseApplication.Companion.context
import com.anr.tools.bean.PolMessageBean
import com.anr.tools.bean.MessageListBean
import com.anr.tools.bean.ScheduledBean
import com.anr.tools.util.LoggerUtils
import com.anr.tools.util.closeStream
import com.anr.tools.util.deleteFile
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class MessageCache private constructor() {

    private var anrInfo = PolMessageBean()
    private val sFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
    private val hFormat = SimpleDateFormat("HH:mm:ss")
    private val diskCacheDir: File

    private var currentSize = 0

    init {
        val externalStorageAvailable =
            Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        val cachePath = if (externalStorageAvailable) {
            context.externalCacheDir?.path ?: context.cacheDir.path
        } else {
            context.cacheDir.path
        }
        diskCacheDir = File(cachePath + File.separator + "block_anr")

    }

    fun onMsgSample(baseTime: Long, msg: MessageListBean) {
        msg.messageCreateTime = hFormat.format(System.currentTimeMillis())
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


    fun saveMessage() {
        val temp = anrInfo
        val path = sFormat.format(System.currentTimeMillis())
        temp.markTime = path
        IO_EXECUTOR.execute {
            cacheData(temp.markTime, temp)
        }
    }

    private fun cacheData(path: String, polMessageBean: PolMessageBean) {
        //如果文件不存在就创建文件
        val file = File(diskCacheDir.path + File.separator + path)
        //file.createNewFile();
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
        currentSize =
            if (diskCacheDir.listFiles() == null) 0 else diskCacheDir.listFiles().size
        if (currentSize > maxSize) {
            val files = diskCacheDir.listFiles()
            Arrays.sort(files) { o1, o2 -> o1.name.compareTo(o2.name) }
            //把最小的移除掉
            LoggerUtils.LOGV("removeLastFile file name: " + files[0].name)
            files[0].deleteFile()
        }
    }

    fun restoreData(): List<PolMessageBean> {
        val result: MutableList<PolMessageBean> = ArrayList()
        val files = diskCacheDir.listFiles() ?: return result
        for (file in files) {
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

