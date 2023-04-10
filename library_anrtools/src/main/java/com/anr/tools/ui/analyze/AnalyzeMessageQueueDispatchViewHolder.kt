package com.anr.tools.ui.analyze


import android.view.View
import android.widget.TextView
import com.anr.tools.MessageInfo
import com.anr.tools.R


class AnalyzeMessageQueueDispatchViewHolder(itemView: View) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
    private val tvMsgType: TextView = itemView.findViewById(R.id.tvMsgType)
    private val tvMsgId: TextView = itemView.findViewById(R.id.tvMsgId)
    private val tvWallTime: TextView = itemView.findViewById(R.id.tvWallTime)
    private val tvCpuTime: TextView = itemView.findViewById(R.id.tvCpuTime)
    private val tvMsgCount: TextView = itemView.findViewById(R.id.tvMsgCount)
    fun parse(messageInfo: MessageInfo) {
        itemView.setBackgroundResource(getItemBg(messageInfo))
        tvMsgId.text = "msgId: "
        if (messageInfo.boxMessages != null && messageInfo.boxMessages.size !== 0) {
            if (messageInfo.boxMessages.get(0) != null) {
                tvMsgId.text = "msgId: " + messageInfo.boxMessages.get(0).msgId
            } else {
                tvMsgId.text = "msgId: "
            }
        }
        tvMsgType.text = "消息类型：" + MessageInfo.msgTypeToString(messageInfo.msgType)
        tvWallTime.text = "wall: " + messageInfo.wallTime
        tvCpuTime.text = "cpu: " + messageInfo.cpuTime
        tvMsgCount.text = "msgCount: " + messageInfo.count
    }

    private fun getItemBg(messageInfo: MessageInfo): Int {
        when (messageInfo.msgType) {
            MessageInfo.MSG_TYPE_GAP -> return R.drawable.icon_msg_gap_bg
            MessageInfo.MSG_TYPE_ANR -> return R.drawable.icon_msg_anr_bg
            MessageInfo.MSG_TYPE_WARN -> return R.drawable.icon_msg_warn_bg
            MessageInfo.MSG_TYPE_ACTIVITY_THREAD_H -> return R.drawable.icon_msg_activity_thread_h_bg
        }
        return R.drawable.icon_msg_info_bg
    }

}