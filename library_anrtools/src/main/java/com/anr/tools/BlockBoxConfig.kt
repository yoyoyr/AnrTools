package com.anr.tools

import java.util.*


class BlockBoxConfig private constructor() {
    /**
     * 超过这个时间输出警告 超过这个时间消息单独罗列出来
     * 合并消息包的阈值。例如16个消息耗时达到300ms，则打包成一个消息包。因为有很多的短消息
     */
    var warnTime: Long = 300
        private set

    private val anrSamplerListeners = ArrayList<IAnrSamplerListener>()

    //这个值暂定50ms
    var gapTime: Long = 50
        private set

    /**
     * 超过这个时间可直接判定为anr
     */
    var anrTime: Long = 3000
        private set

    /**
     * 三大流程掉帧数 超过这个值判定为jank
     */
    var jankFrame = 30
        private set
    var isUseAnalyze = true
        private set

    class Builder {
        private val config: BlockBoxConfig = BlockBoxConfig()
        fun setWarnTime(warnTime: Long): Builder {
            config.warnTime = warnTime
            return this
        }

        fun setGapTime(gapTime: Long): Builder {
            config.gapTime = gapTime
            return this
        }

        fun setAnrTime(anrTime: Long): Builder {
            config.anrTime = anrTime
            return this
        }

        fun setJankFrame(jankFrme: Int): Builder {
            config.jankFrame = jankFrme
            return this
        }
        fun useAnalyze(useAnalyze: Boolean): Builder {
            config.isUseAnalyze = useAnalyze
            return this
        }

        fun build(): BlockBoxConfig {
            return config
        }

        init {
            val fileSample: FileSample = FileSample.instance
            config.anrSamplerListeners.add(fileSample)
        }
    }

    interface IConfigChangeListener {
        fun onConfigChange(config: BlockBoxConfig?)
    }
}
