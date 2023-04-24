package com.anr.tools.ui.analyze


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anr.tools.R
import com.anr.tools.bean.ScheduledBean


class AnalyzeSchedulingAdapter :
    RecyclerView.Adapter<AnalyzeSchedulingViewHolder?>() {
    var scheduledInfos: List<ScheduledBean>? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): AnalyzeSchedulingViewHolder {
        return AnalyzeSchedulingViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_analyze_scheduling, viewGroup, false)
        )
    }

    override fun onBindViewHolder(
        analyzeSchedulingViewHolder: AnalyzeSchedulingViewHolder,
        i: Int
    ) {
        analyzeSchedulingViewHolder.pares(scheduledInfos!![i])
    }

    override fun getItemCount(): Int  = if (scheduledInfos == null) 0 else scheduledInfos!!.size
}
