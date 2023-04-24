package com.anr.tools.ui.analyze


import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anr.tools.MSG_INFO
import com.anr.tools.bean.MessageListBean
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
        val tvMemory =
            findViewById<TextView>(R.id.tvMemory)
        val tvAnrTime =
            findViewById<TextView>(R.id.tvAnrTime)
        val tvNameMessageQueueInfo = findViewById<TextView>(R.id.tvNameMessageQueueInfo)
        val anrInfo = MSG_INFO
        analyzeMessageDispatchAdapter.setOnItemClickListener(object :
            AnalyzeMessageDispatchAdapter.OnItemClickListener {

            override fun onItemClick(messageInfo: MessageListBean?) {
                tvNameMessageQueueDispatchItemInfo.text = messageInfo.toString()
            }

        })

        anrInfo?.run {
            tvNameMessageQueueInfo.text = String(anrInfo.messageQueueSample)
            tvAnrTime.text = "收到SigQuit时间:${anrInfo.anrTime}"
            tvMemory.text = anrInfo.memory
            analyzeSchedulingAdapter.scheduledInfos = anrInfo.scheduledSamplerCache.getAll()
            analyzeSchedulingAdapter.notifyDataSetChanged()
            recyclerMainThreadScheduling.scrollToPosition(analyzeSchedulingAdapter.itemCount - 1)


            analyzeMessageDispatchAdapter.messageInfos = anrInfo.messageSamplerCache.getAll()
            analyzeMessageDispatchAdapter.notifyDataSetChanged()
            recyclerViewMessageQueue.scrollToPosition(analyzeMessageDispatchAdapter.itemCount - 1)
        }
    }
}