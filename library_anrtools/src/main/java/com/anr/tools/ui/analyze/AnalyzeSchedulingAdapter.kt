package com.anr.tools.ui.analyze


import android.view.LayoutInflater
import android.view.ViewGroup
import com.anr.tools.R
import com.anr.tools.ScheduledInfo


class AnalyzeSchedulingAdapter :
    androidx.recyclerview.widget.RecyclerView.Adapter<AnalyzeSchedulingViewHolder?>() {
    var scheduledInfos: List<ScheduledInfo>? = null
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
