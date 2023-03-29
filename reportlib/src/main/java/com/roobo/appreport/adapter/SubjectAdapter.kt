package com.roobo.appreport.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roobo.appreport.R
import com.roobo.appreport.data.LastSelectEntity
import com.roobo.appreport.utils.UIUtils

class SubjectAdapter(val list: MutableList<LastSelectEntity>,var selectIndex:Int=0,var onItemClick:(LastSelectEntity,Int)->Unit) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {



    inner class SubjectViewHolder(view: View) :RecyclerView.ViewHolder(view){
        var tvTips: TextView

        init {
            tvTips = itemView.findViewById<TextView>(R.id.tv_tip)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_course_layout, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.tvTips.text="${list[position].editionName?:""}${list[position].subjectName?:""}${list[position].gradeName?:""}" //list[position].editionName?:""+ list[position].subjectName?:""+list[position].gradeName+"(${list[position].editionName})"
        if(selectIndex == position){
            holder.tvTips.setTextColor(UIUtils.getColor(R.color.tab_indicator_color))
        }else{
            holder.tvTips.setTextColor(UIUtils.getColor(R.color.unchoose_color))
        }
        holder.itemView.setOnClickListener {
            onItemClick.invoke(list[position],position)
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }
}