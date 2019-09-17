package com.junho.app.kingcon.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.junho.app.kingcon.Etc.StringData
import com.junho.app.kingcon.Etc.StringData.TAG_HIGHLIGHT
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.adapter_tag_list.view.*

@Suppress("DEPRECATION")
class ProductTagListAdapter(val context: Context, var tagList: ArrayList<String>): androidx.recyclerview.widget.RecyclerView.Adapter<ProductTagListAdapter.ViewHolder>() {
    override fun getItemCount(): Int = tagList.size
    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mainView = inflater.inflate(R.layout.adapter_tag_list, parent, false)
        return ViewHolder(mainView)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(tagList[position], position)
    }
    class ViewHolder(val view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view){
        @SuppressLint("SetTextI18n")
        fun onBind(tag: String, position: Int){
            if(tag.contains(TAG_HIGHLIGHT)){ //하이라이트 태그
                view.tvTag.run {
                    text = "#${tag.replace(TAG_HIGHLIGHT, "")}"
                    setTextColor(view.resources.getColor(R.color.white))
                    setBackgroundDrawable(view.resources.getDrawable(R.drawable.tag_rect2))
//                    setTypeface(null, Typeface.BOLD)
                }
            }else{
                view.tvTag.run {
                    text = "#$tag"
                    setTextColor(view.resources.getColor(R.color.black))
                    setBackgroundDrawable(view.resources.getDrawable(R.drawable.tag_rect))
                    setTypeface(null, Typeface.NORMAL)
                }
            }
        }
    }
}