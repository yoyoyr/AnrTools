package com.anr.tools.ui.analyze


import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anr.tools.ANR_INFO
import com.anr.tools.bean.PolMessageBean
import com.anr.tools.R


/**
 * 分析每一个anr消息
 */
class AnalyzeActivity : Activity() {
    private val analyzeSchedulingAdapter: AnalyzeSchedulingAdapter = AnalyzeSchedulingAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyze)
        initView()
    }

    private fun initView() {
        val recyclerMainThreadScheduling: RecyclerView =
            findViewById(R.id.recyclerMainThreadScheduling)
        recyclerMainThreadScheduling.adapter = analyzeSchedulingAdapter
        val recyclerViewMessageQueue: RecyclerView = findViewById(R.id.recyclerViewMessageQueue)
        val analyzeMessageDispatchAdapter = AnalyzeMessageDispatchAdapter()
        recyclerViewMessageQueue.adapter = analyzeMessageDispatchAdapter
        val tvNameMessageQueueDispatchItemInfo =
            findViewById<TextView>(R.id.tvNameMessageQueueDispatchItemInfo)
        val tvNameMessageQueueInfo = findViewById<TextView>(R.id.tvNameMessageQueueInfo)
        val tvNameMainThreadStackInfo = findViewById<TextView>(R.id.tvNameMainThreadStackInfo)
        val anrInfo = ANR_INFO
        analyzeMessageDispatchAdapter.setOnItemClickListener(object : AnalyzeMessageDispatchAdapter.OnItemClickListener {

            override fun onItemClick(messageInfo: PolMessageBean?) {
                tvNameMessageQueueDispatchItemInfo.text = messageInfo.toString()
            }

        })

        anrInfo?.run {
            tvNameMessageQueueInfo.text = String(anrInfo.messageQueueSample)
            tvNameMainThreadStackInfo.text = anrInfo.mainThreadStack
            analyzeSchedulingAdapter.scheduledInfos = anrInfo.scheduledSamplerCache.getAll()

            analyzeSchedulingAdapter.notifyDataSetChanged()
            analyzeMessageDispatchAdapter.messageInfos = anrInfo.messageSamplerCache.getAll()
            analyzeMessageDispatchAdapter.notifyDataSetChanged()
        }
    }
}