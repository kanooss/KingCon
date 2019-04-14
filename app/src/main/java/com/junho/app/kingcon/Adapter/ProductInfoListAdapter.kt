package com.junho.app.kingcon.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.adapter_info_list.view.*

class ProductInfoListAdapter(val context: Context, private val infoData: ArrayList<String>): RecyclerView.Adapter<ProductInfoListAdapter.ProductInfoListViewHolder>() {
    override fun getItemCount(): Int = infoData.size
    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ProductInfoListViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mainView = inflater.inflate(R.layout.adapter_info_list, parent, false)
        return ProductInfoListViewHolder(mainView)
    }

    override fun onBindViewHolder(holder: ProductInfoListViewHolder, position: Int) {
        holder.onBind()
    }

    class ProductInfoListViewHolder(val view: View): RecyclerView.ViewHolder(view){
        fun onBind() {
            view.tvInfoTitle.text
            view.tvInfoContent.text
        }

    }
}