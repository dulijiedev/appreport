package com.roobo.appreport.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roobo.appreport.MyApp
import com.roobo.appreport.R
import com.roobo.appreport.ReportSDK
import com.roobo.appreport.utils.ScreenUtil
import com.roobo.appreport.utils.UIUtils
import toPx

class TipAdapter : RecyclerView.Adapter<TipAdapter.TipViewHolder>() {

    private var datas: List<String>? = null
    fun setData(data: List<String>?) {
        this.datas = data
        notifyDataSetChanged()
    }

    inner class TipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTips: TextView

        init {
            tvTips = itemView.findViewById<TextView>(R.id.tv_tip)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_tip_layout, parent, false)
        return TipViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        holder.tvTips.text = "${datas?.get(position) ?: ""}"
        val lpm= holder.tvTips.layoutParams as RecyclerView.LayoutParams
        if(!datas.isNullOrEmpty()) {
            lpm.width = (ScreenUtil.getWith(ReportSDK.getInstance().application) - (50 + 35+(datas?.size?:0)*10).toPx()).div(datas?.size?:1)
            lpm.marginStart=(6.toPx())
            holder.tvTips.layoutParams = lpm
        }
    }

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }
}