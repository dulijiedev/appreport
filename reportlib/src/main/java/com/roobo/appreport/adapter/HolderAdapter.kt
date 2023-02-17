package com.roobo.appreport.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.roobo.appreport.R
import com.roobo.appreport.data.Chapter
import com.roobo.appreport.data.KnowledgeCharEnum
import com.roobo.appreport.utils.UIUtils

class HolderAdapter : RecyclerView.Adapter<HolderAdapter.SViewHolder>() {
    private var datas: Chapter? = null

    fun setDatas(datas: Chapter?) {
        this.datas = datas
        notifyDataSetChanged()
    }

    private var chooseEnum: KnowledgeCharEnum = KnowledgeCharEnum.All
    fun changeChoose(choose: KnowledgeCharEnum) {
        this.chooseEnum = choose
        notifyDataSetChanged()
    }

    inner class SViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView
        var tvValue: TextView
        var tvNodata: TextView
        var llData: LinearLayout
        var vDiv: View

        init {
            tvName = itemView.findViewById(R.id.tv_desc)
            tvValue = itemView.findViewById(R.id.tv_value)
            llData = itemView.findViewById(R.id.ll_data)
            vDiv = itemView.findViewById(R.id.v_div)
            tvNodata = itemView.findViewById(R.id.tv_no_data)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_holder_layout, parent, false)
        return SViewHolder(view)
    }

    override fun onBindViewHolder(holder: SViewHolder, position: Int) {
        when(chooseEnum){
            KnowledgeCharEnum.All,KnowledgeCharEnum.Mastered->{
                val dataShow=datas?.knowledgeList?.filter {
                    !it.noRecord && it.accuracy!=null && it.accuracy==100
                }


                if(dataShow.isNullOrEmpty()){
                    holder.llData.isVisible = false
                    holder.tvNodata.isVisible = true
                    holder.vDiv.setBackgroundResource(R.color.transparent)
                }else {
                    holder.llData.isVisible = true
                    holder.tvNodata.isVisible = false
                    holder.vDiv.setBackgroundResource(R.color.cf6)
                    holder.tvName.text = dataShow.get(position).knowledgename ?: ""
                    holder.tvValue.text = "已掌握"
                    holder.tvValue.setTextColor(UIUtils.getColor(R.color.master))
                    holder.vDiv.setBackgroundResource(R.color.cf6)
                }

            }
            KnowledgeCharEnum.WeakSpot->{
                val dataShow=datas?.knowledgeList?.filter {
                    !it.noRecord && it.accuracy!=null && it.accuracy!=100
                }
                if(dataShow.isNullOrEmpty()){
                    holder.llData.isVisible = false
                    holder.tvNodata.isVisible = true
                    holder.vDiv.setBackgroundResource(R.color.transparent)
                }else {
                    holder.llData.isVisible = true
                    holder.tvNodata.isVisible = false
                    holder.vDiv.setBackgroundResource(R.color.cf6)
                    holder.tvName.text = dataShow.get(position).knowledgename ?: ""
                    holder.tvValue.text = "${dataShow?.get(position)?.accuracy ?: 0}"
                    holder.tvValue.setTextColor(UIUtils.getColor(R.color.weak))
                    holder.vDiv.setBackgroundResource(R.color.cf6)
                }
            }
            KnowledgeCharEnum.Undetected->{
                val dataShow=datas?.knowledgeList?.filter {
                    it.noRecord
                }

                if(dataShow.isNullOrEmpty()){
                    holder.llData.isVisible = false
                    holder.tvNodata.isVisible = true
                    holder.vDiv.setBackgroundResource(R.color.transparent)
                }else {
                    holder.llData.isVisible = true
                    holder.tvNodata.isVisible = false
                    holder.vDiv.setBackgroundResource(R.color.cf6)
                    holder.tvName.text = dataShow.get(position).knowledgename ?: ""
                    holder.tvValue.text = "未检测"
                    holder.tvValue.setTextColor(UIUtils.getColor(R.color.undefine))
                    holder.vDiv.setBackgroundResource(R.color.cf6)
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return when(chooseEnum){
            KnowledgeCharEnum.All,KnowledgeCharEnum.Mastered->{
                val count=datas?.knowledgeList?.count {
                    !it.noRecord && it.accuracy!=null && it.accuracy==100
                }?:0
                count.coerceAtLeast(1)
            }
            KnowledgeCharEnum.WeakSpot->{
                val count=datas?.knowledgeList?.count {
                    !it.noRecord && it.accuracy!=null && it.accuracy!=100
                }?:0
                count.coerceAtLeast(1)
            }
            KnowledgeCharEnum.Undetected->{
                val count=datas?.knowledgeList?.count {
                    it.noRecord
                }?:0
                count.coerceAtLeast(1)
            }
        }
    }
}