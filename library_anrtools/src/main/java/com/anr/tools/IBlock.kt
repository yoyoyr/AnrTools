package com.anr.tools

import android.content.Context


interface IBlock  {

    fun startMonitor(): IBlock?
    fun stopMonitor(): IBlock
    fun updateConfig(config: BlockBoxConfig): IBlock
}
