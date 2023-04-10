package com.anr.tools

import java.io.File

object FileUtils {
    fun deleteFile(file: File?): Boolean {
        if (file != null && file.exists()) {
            if (file.isDirectory) {
                val files = file.listFiles()
                if (files != null) {
                    for (value in files) {
                        return deleteFile(value)
                    }
                }
            }
            return file.delete()
        }
        return false
    }

    fun closeStream(closeable: AutoCloseable?) {
        if (closeable == null) {
            return
        }
        try {
            closeable.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
