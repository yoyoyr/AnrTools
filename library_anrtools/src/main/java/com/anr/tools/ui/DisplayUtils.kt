package com.anr.tools.ui


import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.anr.tools.AppExecutors
import com.anr.tools.FileSample


object DisplayUtils {
    fun showAnalyzeActivityInLauncher(context: Context, show: Boolean) {
        FileSample.getInstance().saveMessage()
        val component = ComponentName(context, DisplayActivity::class.java)
        val packageManager = context.packageManager
        val newState =
            if (show) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        // Blocks on IPC.
        AppExecutors.getInstance().diskIO().execute(Runnable {
            packageManager.setComponentEnabledSetting(
                component,
                newState,
                PackageManager.DONT_KILL_APP
            )
        })
    }
}
