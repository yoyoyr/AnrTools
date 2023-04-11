package com.anr.tools


class BlockBoxConfig private constructor() {
    /**
     * 超过这个时间输出警告 超过这个时间消息单独罗列出来
     * 合并消息包的阈值。例如16个消息耗时达到300ms，则打包成一个消息包。因为有很多的短消息
     */
    private var warnTime: Long = 300

    //这个值暂定50ms
    private var gapTime: Long = 50

    /**
     * 超过这个时间可直接判定为anr
     */
    private var anrTime: Long = 3000

    /**
     * 三大流程掉帧数 超过这个值判定为jank
     */
    private var jankFrame = 30

    private var useAnalyze = true

    fun isUseAnalyze(): Boolean {
        return useAnalyze
    }

    fun getWarnTime(): Long {
        return warnTime
    }

    fun getGapTime(): Long {
        return gapTime
    }

    fun getAnrTime(): Long {
        return anrTime
    }

    fun getJankFrame(): Int {
        return jankFrame
    }

    private fun BlockBoxConfig() {}


    class Builder {
        private val config: BlockBoxConfig
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
            config.useAnalyze = useAnalyze
            return this
        }

        fun build(): BlockBoxConfig {
            return config
        }

        init {
            config = BlockBoxConfig()
        }
    }

    interface IConfigChangeListener {
        fun onConfigChange(config: BlockBoxConfig?)
    }
}
