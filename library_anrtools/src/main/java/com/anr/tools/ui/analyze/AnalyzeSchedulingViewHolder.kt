package com.anr.tools.ui.analyze

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anr.tools.R
import com.anr.tools.ScheduledInfo


class AnalyzeSchedulingViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var tvSchedulingDealt: TextView = itemView.findViewById(R.id.tvSchedulingDealt)
    var tvMsgId: TextView = itemView.findViewById(R.id.tvMsgId)

    // 300ms 90dp  那么每 dp 对应0.9ms
    var dpMs = 0.9f
    fun pares(info: ScheduledInfo) {
        val widthPx = Math.max(
            itemView.resources.getDimensionPixelSize(R.dimen.dp_40),
            (info.dealt * dpMs).toInt()
        )
        tvSchedulingDealt.text = info.dealt.toString() + "ms"
        val params = tvSchedulingDealt.layoutParams
        params.width = widthPx
        tvSchedulingDealt.layoutParams = params
        tvMsgId.text = "msgId：" + info.msgId
    }

}
