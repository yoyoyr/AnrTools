package com.anr_tools.ui.analyze


import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anr.tools.R
import com.anr.tools.bean.MessageListBean


class AnalyzeMessageQueueDispatchViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private val tvMsgType: TextView = itemView.findViewById(R.id.tvMsgType)
    private val tvWallTime: TextView = itemView.findViewById(R.id.tvWallTime)
    private val tvCpuTime: TextView = itemView.findViewById(R.id.tvCpuTime)
    private val tvMsgCount: TextView = itemView.findViewById(R.id.tvMsgCount)
    private val tvMsgId: TextView = itemView.findViewById(R.id.tvMsgId)
    private val tvMsgTime: TextView = itemView.findViewById(R.id.tvMsgTime)
    fun parse(messageInfo: MessageListBean) {
        itemView.setBackgroundResource(getItemBg(messageInfo))

        if (messageInfo.boxMessages.isNotEmpty()) {
            tvMsgId.text = "msgId: " + messageInfo.boxMessages[0].monitorMsgId
        } else {
            tvMsgId.text = "msgId: "
        }
        tvMsgType.text = "类型：" + MessageListBean.msgTypeToString(messageInfo.msgType)
        tvWallTime.text = "总耗时: " + messageInfo.wallTime
        tvCpuTime.text = "cpu耗时: " + messageInfo.cpuTime
        tvMsgCount.text = "消息个数: " + messageInfo.count
        tvMsgTime.text = "消息类创建时间: " + messageInfo.messageCreateTime
    }

    private fun getItemBg(messageInfo: MessageListBean): Int {
        when (messageInfo.msgType) {
            MessageListBean.MSG_TYPE_GAP -> return R.drawable.icon_msg_gap_bg
            MessageListBean.MSG_TYPE_ANR -> return R.drawable.icon_msg_anr_bg
            MessageListBean.MSG_TYPE_WARN -> return R.drawable.icon_msg_warn_bg
            MessageListBean.MSG_TYPE_ACTIVITY_THREAD_H -> return R.drawable.icon_msg_activity_thread_h_bg
        }
        return R.drawable.icon_msg_info_bg
    }

}