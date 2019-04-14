package com.junho.app.kingcon.Login.View

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.junho.app.kingcon.Adapter.AdapterTag
import com.junho.app.kingcon.Etc.AWSDB
import com.junho.app.kingcon.Etc.CallBack.CallbackAWSDB
import com.junho.app.kingcon.Etc.CallBack.CallbackAdapter
import com.junho.app.kingcon.Etc.StringData
import com.junho.app.kingcon.Item.TagData
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.activity_view_set_tag.*


@Suppress("DEPRECATION")
class ViewSetTag : AppCompatActivity(), CallbackAWSDB.Tag, CallbackAdapter.Tag {
    lateinit var adapter: AdapterTag
    lateinit var basicColor : IntArray
    private lateinit var selectColors : IntArray
    private var tagData = TagData()
    private var clickTag = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_set_tag)

        initialize()
        setView()
        //TODO 종료시 다이얼로그 띄우고 앱 죵료되게
    }
    //태그 버전 다르면 데이터 가져오기
    override fun callbackTagData(data: TagData) {
        tagData = data
        //클릭 논클릭 색 데이터 넣기
        tagData.tag.keys.forEach{key ->
            tagData.tagColor[key] = arrayListOf()
            tagData.tag[key]?.forEach { _ ->
                tagData.tagColor[key]?.add(basicColor)
            }
        }
        adapter = AdapterTag(this, tagData, this)
        rvPreferTagList.adapter = adapter
        rvPreferTagList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
    //태그 리스트 클릭 이벤트
    override fun tagClick(title: String, position: Int) {
        //클릭상태로 변경
        if(tagData.tagColor[title]!![position].contentEquals(basicColor)) {
            if(clickTag.size <5) {
                tagData.tagColor[title]!![position] = selectColors
                clickTag.add(tagData.tag[title]!![position])
            }else{
                Snackbar.make(window.decorView.rootView, "최대 5개까지 선택할 수 있습니다.", Snackbar.LENGTH_SHORT).show()
            }
        }
        else { //클릭 해제
            tagData.tagColor[title]!![position] = basicColor
            clickTag.remove(tagData.tag[title]!![position])
        }
        adapter.notifyDataSetChanged()
    }
    //백키
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("태그선택 건너뛰기")
        builder.setMessage("선호하는 태그를 선택하지않고 시작합니다.")
        builder.setPositiveButton("예") { dialog, which ->
            setResult(StringData.CANCEL_TAG_SET)
            finish()
        }
        builder.setNegativeButton("아니오") { _, _ -> }
        builder.show()
    }
    /////////////////////////////////////////////////////////////////
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
        AWSDB.tagVersionCheck(this)
    }
    private fun setView() {
        //다음 버튼 클릭
        btnDone.setOnClickListener {
            //유저 선호 태그 저장
            if(clickTag.size!=0) {
                AWSDB.userTagUpdate(clickTag, 5)
                setResult(StringData.DONE_TAG_SET)
                finish()
            }else{
                onBackPressed()
            }
        }
    }
}
