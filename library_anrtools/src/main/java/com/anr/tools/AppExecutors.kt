package com.anr.tools

import android.os.Handler
import android.os.Looper
import androidx.annotation.VisibleForTesting
import java.util.concurrent.Executor
import java.util.concurrent.Executors


/**
 * Global executor pools for the whole application.
 *
 *
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
class AppExecutors internal constructor(
    private val diskIO: Executor,
    private val networkIO: Executor,
    private val mainThread: MainThreadExecutor
) {

    constructor() : this(
        DiskIOThreadExecutor(), Executors.newFixedThreadPool(THREAD_COUNT),
        MainThreadExecutor()
    ) {
    }

    fun diskIO(): Executor {
        return diskIO
    }

    fun networkIO(): Executor {
        return networkIO
    }

    fun mainThread(): MainThreadExecutor {
        return mainThread
    }

    class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }

        fun executeDelay(command: Runnable, ms: Long) {
            mainThreadHandler.postDelayed(command, ms)
        }
    }

    companion object {
        private const val THREAD_COUNT = 3
        private val appExecutors = AppExecutors()

        fun getInstance() = appExecutors
    }
}
