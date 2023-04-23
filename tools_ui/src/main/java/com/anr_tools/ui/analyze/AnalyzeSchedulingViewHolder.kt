package com.anr_tools.ui.analyze

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anr.tools.R
import com.anr.tools.bean.ScheduledBean


class AnalyzeSchedulingViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private var tvSchedulingDealt: TextView = itemView.findViewById(R.id.tvSchedulingDealt)
    private var tvMsgId: TextView = itemView.findViewById(R.id.tvMsgId)

    // 300ms 100dp  那么每 dp 对应1ms
    var dpMs = 1f
    fun pares(info: ScheduledBean) {
        val widthPx = itemView.resources.getDimensionPixelSize(R.dimen.dp_40)
            .coerceAtLeast((info.overTime * dpMs).toInt())
        tvSchedulingDealt.text = "${info.overTime}ms"
        val params = tvSchedulingDealt.layoutParams
        params.width = widthPx
        tvSchedulingDealt.layoutParams = params
        tvMsgId.text = "msgId：${info.monitorMsgId}"
    }

}
