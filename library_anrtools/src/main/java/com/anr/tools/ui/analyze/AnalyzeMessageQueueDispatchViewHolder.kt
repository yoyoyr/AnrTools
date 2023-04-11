package com.anr.tools.ui.analyze


import android.view.View
import android.widget.TextView
import com.anr.tools.bean.PolMessageBean
import com.anr.tools.R


class AnalyzeMessageQueueDispatchViewHolder(itemView: View) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
    private val tvMsgType: TextView = itemView.findViewById(R.id.tvMsgType)
    private val tvWallTime: TextView = itemView.findViewById(R.id.tvWallTime)
    private val tvCpuTime: TextView = itemView.findViewById(R.id.tvCpuTime)
    private val tvMsgCount: TextView = itemView.findViewById(R.id.tvMsgCount)
    fun parse(messageInfo: PolMessageBean) {
        itemView.setBackgroundResource(getItemBg(messageInfo))
        tvMsgType.text = "类型：" + PolMessageBean.msgTypeToString(messageInfo.msgType)
        tvWallTime.text = "总耗时: " + messageInfo.wallTime
        tvCpuTime.text = "cpu耗时: " + messageInfo.cpuTime
        tvMsgCount.text = "消息个数: " + messageInfo.count
    }

    private fun getItemBg(messageInfo: PolMessageBean): Int {
        when (messageInfo.msgType) {
            PolMessageBean.MSG_TYPE_GAP -> return R.drawable.icon_msg_gap_bg
            PolMessageBean.MSG_TYPE_ANR -> return R.drawable.icon_msg_anr_bg
            PolMessageBean.MSG_TYPE_WARN -> return R.drawable.icon_msg_warn_bg
            PolMessageBean.MSG_TYPE_ACTIVITY_THREAD_H -> return R.drawable.icon_msg_activity_thread_h_bg
        }
        return R.drawable.icon_msg_info_bg
    }

}