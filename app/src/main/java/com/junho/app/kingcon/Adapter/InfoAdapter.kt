package com.junho.app.kingcon.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.adapter_info_list.view.*

class InfoAdapter(val context: Context, private val infoTitle: ArrayList<String>, private val infoContent: ArrayList<String>):
    RecyclerView.Adapter<InfoAdapter.InfoAdapterViewHolder>() {
    override fun getItemCount(): Int = infoTitle.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoAdapterViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mainView = inflater.inflate(R.layout.adapter_info_list, parent, false)
        return InfoAdapterViewHolder(mainView)
    }
    override fun onBindViewHolder(holder: InfoAdapterViewHolder, position: Int) {
        holder.bind(infoTitle[position], infoContent[position])
    }

    class InfoAdapterViewHolder(val view: View): RecyclerView.ViewHolder(view){
        fun bind(infoTitle: String, infoContext: String){
            view.tvInfoTitle.text = infoTitle
            view.tvInfoContent.text = infoContext
        }
    }
}
enum class Info{
    제품명,
    제조사,
    태그 ,
    제조국가,
    전성분,
    특징,
    가격
}