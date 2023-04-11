package com.anr.tools


import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.text.TextUtils
import android.util.Log
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Synchronized


class FileSample private constructor() : IAnrSamplerListener {
    private var anrInfo: AnrInfo = AnrInfo()
    override fun onMessageQueueSample(baseTime: Long, msgId: String, msg: String) {
        anrInfo.messageQueueSample.append(msg)
    }

    override fun onCpuSample(baseTime: Long, msgId: String, msg: String) {}
    override fun onMemorySample(baseTime: Long, msgId: String, msg: String) {}
    override fun onMainThreadStackSample(baseTime: Long, msgId: String, msg: String) {
        anrInfo.mainThreadStack = msg
    }

    override fun onSampleAnrMsg() {

    }

    override fun onScheduledSample(start: Boolean, baseTime: Long, msgId: String, dealt: Long) {
        synchronized(this) {
            anrInfo.scheduledSamplerCache.put(
                baseTime,
                ScheduledInfo(dealt, msgId, start)
            )
        }
    }

    override fun onMsgSample(baseTime: Long, msgId: String, msg: MessageInfo) {
        synchronized(this) {
            if (msg.msgType === MessageInfo.MSG_TYPE_GAP && anrInfo.messageSamplerCache.getLastValue()?.msgType === MessageInfo.MSG_TYPE_GAP) {
//                Log.e(TAG,"error continuous gap");
            }
            anrInfo.messageSamplerCache.put(baseTime, msg)
        }
    }

    @Synchronized
    fun saveMessage() {
        val temp: AnrInfo = anrInfo
        val path = FileCache.sFormat.format(Date())
        if (TextUtils.isEmpty(temp.markTime)) {
            temp.markTime = path
        }
        AppExecutors.getInstance().diskIO().execute(Runnable {
            Log.d(
                TAG,
                "cacheData schedule size " + temp.scheduledSamplerCache.getAll().size
                    .toString() + "  file name : " + temp.markTime
            )
            fileCache.cacheData(temp.markTime, temp)
            //通知可以展示ui
        })
    }


    override fun onJankSample(msgId: String, msg: MessageInfo) {
        val builder = StringBuilder()
        builder.append("onJankSample")
            .append(" msgId : ")
            .append(msgId)
            .append("  msg : ")
            .append(msg)
        Log.d(TAG, String(builder))
    }

    override fun messageQueueDispatchAnrFinish() {
        val temp: AnrInfo = anrInfo
        anrInfo = AnrInfo()
        val path = FileCache.sFormat.format(Date())
        if (TextUtils.isEmpty(temp.markTime)) {
            temp.markTime = path
        }
        AppExecutors.getInstance().diskIO().execute(Runnable {
            Log.d(
                TAG,
                "messageQueueDispatchAnrFinish cacheData schedule size " + temp.scheduledSamplerCache.getAll()
                    .size.toString() + "  file name : " + temp.markTime
            )
            fileCache.cacheData(temp.markTime, temp)
        })
    }

    class FileCache<T : Serializable?> {
        private val LAST_VERSION = "last_version"
        private var diskCacheDir: File? = null

        /**
         * 指定目录下最多能够存储多少个文件
         */
        private var maxSize = 20
        private var currentSize = 0
        private fun getDiskCacheDir(context: Context, uniqueName: String): File {
            val externalStorageAvailable =
                Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
            val cachePath: String
            cachePath = if (externalStorageAvailable) {
                context.externalCacheDir!!.path
            } else {
                context.cacheDir.path
            }
            return File(cachePath + File.separator + uniqueName)
        }

        fun init(context: Context, rootDir: String, maxSize: Int, appVersion: String) {
            if (maxSize > 2) {
                this.maxSize = maxSize
            }
            val sp = context.getSharedPreferences("moonlight_anr", Context.MODE_PRIVATE)
            val lastVersion = sp.getString(LAST_VERSION, "")
            sp.edit().putString(LAST_VERSION, appVersion)
                .apply()
            diskCacheDir = getDiskCacheDir(context, rootDir)
            if (diskCacheDir!!.exists() && appVersion != lastVersion) { //版本不一致删除
                FileUtils.deleteFile(diskCacheDir)
            }
            if (!diskCacheDir!!.exists()) {
                diskCacheDir!!.mkdirs()
            }
            Log.d(TAG, "cache path : " + diskCacheDir!!.path)
            val files = diskCacheDir!!.listFiles()
            currentSize = files?.size ?: 0
            val m = getUsableSpace(diskCacheDir)
        }

        @Synchronized
        fun cacheData(path: String?, serializable: T) {
//如果文件不存在就创建文件
            val file = File(diskCacheDir!!.path + File.separator + path)
            //file.createNewFile();
            //获取输出流
            //这里如果文件不存在会创建文件，  如果文件存在，新写会覆盖以前的内容吗？
            var fos: ObjectOutputStream? = null
            try {
                fos = ObjectOutputStream(FileOutputStream(file))
                fos.writeObject(serializable)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                FileUtils.closeStream(fos)
            }
            currentSize =
                if (diskCacheDir!!.listFiles() == null) 0 else diskCacheDir!!.listFiles().size
            if (currentSize > maxSize) {
                removeLastFile()
            }
        }

        @Synchronized
        private fun removeLastFile() {
            val files = diskCacheDir!!.listFiles()
            Arrays.sort(
                files
            ) { o1, o2 -> o1.name.compareTo(o2.name) }
            //把最小的移除掉
            Log.d(TAG, "removeLastFile file name: " + files[0].name)
            FileUtils.deleteFile(files[0])
        }

        @Synchronized
        fun restoreData(): List<T> {
            val result: MutableList<T> = ArrayList()
            val files = diskCacheDir!!.listFiles() ?: return result
            for (file in files) {
                var ois: ObjectInputStream? = null
                try {
                    //获取输入流
                    ois = ObjectInputStream(FileInputStream(file))
                    //获取文件中的数据
                    val data = ois.readObject() as T
                    result.add(data)
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

        private fun getUsableSpace(path: File?): Long {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                return path!!.usableSpace
            }
            val statFs = StatFs(path!!.path)
            return statFs.blockSizeLong + statFs.availableBlocksLong
        }

        companion object {
            private val TAG: String = "FileCache"
            val sFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
        }
    }

    companion object {
        private val TAG: String = "FileSample"
        val fileCache: FileCache<AnrInfo> = FileCache<AnrInfo>()
        private val fileSample = FileSample()
        fun getInstance() = fileSample
    }

    init {
        fileCache.init(
            BaseApplication.context,
            "block_anr",
            10,
            "1.0.0"
        )
    }
}