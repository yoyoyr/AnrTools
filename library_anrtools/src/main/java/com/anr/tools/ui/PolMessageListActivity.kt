package com.anr.tools.ui


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.anr.tools.ANR_INFO
import com.anr.tools.bean.InfoBean
import com.anr.tools.util.FileUtil
import com.anr.tools.IO_EXECUTOR
import com.anr.tools.R
import com.anr.tools.ui.analyze.AnalyzeActivity
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


class PolMessageListActivity : Activity() {
    private val refresh = AtomicBoolean(false)
    private val adapter = FileAdapter()
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)
        initView()
    }

    private fun initView() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        swipeRefreshLayout?.setOnRefreshListener { refreshAnrData() }
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = adapter
        refreshAnrData()
    }

    private fun refreshAnrData() {
        if (refresh.get()) {
            return
        }
        refresh.set(true)
        IO_EXECUTOR.execute {
            refresh.set(false)
            val anrInfoList = FileUtil.getInstance().restoreData()
            Collections.sort(
                anrInfoList
            ) { o1, o2 -> o2.markTime.compareTo(o1.markTime) }
            adapter.anrInfoList = anrInfoList
            runOnUiThread {
                swipeRefreshLayout?.isRefreshing = false
                adapter.notifyDataSetChanged()
            }
        }
    }

    private class FileAdapter : RecyclerView.Adapter<FileViewHolder?>() {
        var anrInfoList: List<InfoBean>? = null

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): FileViewHolder {
            return FileViewHolder(
                LayoutInflater.from(viewGroup.context).inflate(R.layout.item_anr, viewGroup, false)
            )
        }

        override fun onBindViewHolder(fileViewHolder: FileViewHolder, i: Int) {
            anrInfoList?.get(i)?.let { anrInfo ->
                fileViewHolder.itemView.setOnClickListener {
                    ANR_INFO = anrInfo
                    val context: Context = fileViewHolder.itemView.context
                    context.startActivity(Intent(context, AnalyzeActivity::class.java))
                }
                fileViewHolder.textView.text = anrInfo.markTime
            }

        }


        override fun getItemCount(): Int = if (anrInfoList == null) 0 else anrInfoList!!.size
    }

    private class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tvFileName)

    }
}