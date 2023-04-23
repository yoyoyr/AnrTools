package com.anr_tools.ui.analyze


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anr.tools.bean.MessageListBean
import com.anr.tools.R


class AnalyzeMessageDispatchAdapter :
    androidx.recyclerview.widget.RecyclerView.Adapter<AnalyzeMessageQueueDispatchViewHolder?>() {
    var messageInfos: List<MessageListBean>? = null
    private var onItemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): AnalyzeMessageQueueDispatchViewHolder {
        return AnalyzeMessageQueueDispatchViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_analyze_message_dispatch, viewGroup, false)
        )
    }

    override fun onBindViewHolder(
        analyzeMessageQueueDispatchViewHolder: AnalyzeMessageQueueDispatchViewHolder,
        i: Int
    ) {
        analyzeMessageQueueDispatchViewHolder.itemView.setOnClickListener(View.OnClickListener {
            if (onItemClickListener != null) {
                onItemClickListener!!.onItemClick(messageInfos!![i])
            }
        })
        analyzeMessageQueueDispatchViewHolder.parse(messageInfos!![i])
    }

    interface OnItemClickListener {
        fun onItemClick(messageInfo: MessageListBean?)
    }

    override fun getItemCount(): Int {
        return if (messageInfos == null) 0 else messageInfos!!.size
    }
}
