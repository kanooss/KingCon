package com.junho.app.kingcon.main.bottom.Tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.junho.app.kingcon.Adapter.AdapterTag
import com.junho.app.kingcon.Etc.AWSDB
import com.junho.app.kingcon.Etc.CallBack.CallbackAWSDB
import com.junho.app.kingcon.Etc.CallBack.CallbackAdapter
import com.junho.app.kingcon.Etc.StringData.TAG
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.Item.TagData
import com.junho.app.kingcon.main.ViewMain

import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.fragment_view_tag.view.*
import kotlin.concurrent.thread

@Suppress("DEPRECATION")
class ViewTagFragment : androidx.fragment.app.Fragment(), CallbackAWSDB.Tag, CallbackAdapter.Tag {
    lateinit var adapter: AdapterTag
    lateinit var basicColor : IntArray
    private lateinit var selectColors : IntArray
    private var tagData = TagData()
    private var clickTag = arrayListOf<String>()

    private lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_view_tag, container, false)

        initialize()
        setView()

        return rootView
    }
    /** 태그 데이터 콜백 **/
    override fun callbackTagData(data: TagData) {
        tagData = data
        //클릭 논클릭 색 데이터 넣기
        tagData.tag.keys.forEach{key ->
            tagData.tagColor[key] = arrayListOf()
            tagData.tag[key]?.forEach { _ ->
                tagData.tagColor[key]?.add(basicColor)
            }
        }
        ThreadUtils.runOnUiThread {
            adapter = AdapterTag(rootView.context, tagData, this)
            rootView.rvTagList.adapter = adapter
            rootView.rvTagList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                rootView.context,
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                false
            )
        }
    }
    /** 태그 클릭 **/
    override fun tagClick(title: String, position: Int) {
        //클릭상태로 변경
        if(tagData.tagColor[title]!![position].contentEquals(basicColor)) {
            tagData.tagColor[title]!![position] = selectColors
            clickTag.add(tagData.tag[title]!![position])
        }
        else { //클릭 해제
            tagData.tagColor[title]!![position] = basicColor
            clickTag.remove(tagData.tag[title]!![position])
        }
        //버튼 enable
        btnEnable(clickTag.size>0)
        adapter.notifyDataSetChanged()
    }
    private fun btnEnable(enable: Boolean){
        if(enable){
            rootView.tvListRefresh.isEnabled = true
            rootView.tvListRefresh.setTextColor(rootView.resources.getColor(R.color.colorPrimary))
            rootView.tvListSearch.isEnabled = true
            rootView.tvListSearch.setTextColor(rootView.resources.getColor(R.color.colorPrimary))
        }else{
            rootView.tvListRefresh.isEnabled = false
            rootView.tvListRefresh.setTextColor(rootView.resources.getColor(R.color.gray0_5))
            rootView.tvListSearch.isEnabled = false
            rootView.tvListSearch.setTextColor(rootView.resources.getColor(R.color.gray0_5))
        }
    }
    private fun initialize() {
        //선택 컬러
        basicColor = intArrayOf(
            this.resources.getColor(R.color.white),
            this.resources.getColor(R.color.colorPrimary),
            this.resources.getColor(R.color.black))
        selectColors = intArrayOf(
            this.resources.getColor(R.color.colorPrimary2),
            this.resources.getColor(R.color.colorPrimary),
            this.resources.getColor(R.color.white))
        //태그 버전 체크하고 갱신
        thread {
            AWSDB.tagVersionCheck(this)
        }
    }
    private fun setView() {
        // 태그 검색
        rootView.tvListSearch.setOnClickListener {
            (activity as ViewMain).tagResultFragment = ViewTagResultFragment.newInstance(clickTag)
            Util.fragmentChanger((activity as ViewMain),(activity as ViewMain).tagResultFragment, TAG)
        }
        // 태그 선택 초기화
        rootView.tvListRefresh.setOnClickListener {
            tagData.tagColor.forEach {
                for(i in 0 until tagData.tagColor[it.key]?.size!!)
                    tagData.tagColor[it.key]!![i] = basicColor
            }
            clickTag.clear()
            btnEnable(false)
            adapter.notifyDataSetChanged()
        }
    }
}
