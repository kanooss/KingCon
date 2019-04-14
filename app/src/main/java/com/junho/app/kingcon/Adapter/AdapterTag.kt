package com.junho.app.kingcon.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.lujun.androidtagview.TagView
import com.junho.app.kingcon.Etc.CallBack.CallbackAdapter
import com.junho.app.kingcon.Etc.StringData
import com.junho.app.kingcon.Item.TagData
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.adapter_search_tag_list.view.*

class AdapterTag(val context: Context, private var tagList: TagData , private val callbackTag: CallbackAdapter.Tag?): RecyclerView.Adapter<AdapterTag.ViewHolder>() {
    override fun getItemCount(): Int = tagList.title.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mainView = inflater.inflate(R.layout.adapter_search_tag_list, parent, false)
        return ViewHolder(mainView)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(tagList.title[position], tagList.tag[tagList.title[position]]!!,
            tagList.tagColor[tagList.title[position]]!!, callbackTag, position)
    }

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun onBind(title: String, tagList: ArrayList<String>, tagColor: ArrayList<IntArray>, callback: CallbackAdapter.Tag?, position: Int) {
            view.tvListName.visibility = View.VISIBLE
            view.tvListName.text = title
            view.tagsContainer.setTags(tagList, tagColor)
            view.tagsContainer.setOnTagClickListener(object : TagView.OnTagClickListener{
                override fun onTagLongClick(position: Int, text: String?) {}
                override fun onTagCrossClick(position: Int) {}
                override fun onTagClick(position: Int, text: String?) {
                    callback?.tagClick(title, position)
                }
            })
        }
    }
}