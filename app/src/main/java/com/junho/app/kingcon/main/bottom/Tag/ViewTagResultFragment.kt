package com.junho.app.kingcon.main.bottom.Tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.junho.app.kingcon.Adapter.AdapterSelectTag
import com.junho.app.kingcon.Adapter.ProductListAdapter
import com.junho.app.kingcon.Etc.AWSDB
import com.junho.app.kingcon.Etc.CallBack.CallbackAdapter
import com.junho.app.kingcon.Etc.StringData.TAG
import com.junho.app.kingcon.Etc.StringData.TAG_SEARCH_CHECK
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.Item.TagData
import com.junho.app.kingcon.main.Product.View.ViewProductInfoFragment
import com.junho.app.kingcon.main.ViewMain
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.fragment_view_tag_result.view.*
import kotlin.concurrent.thread


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")
class ViewTagResultFragment : androidx.fragment.app.Fragment(), CallbackAdapter.Product {
    private val argTag = "tag"
    private val productData = arrayListOf<ProductData>()
    private lateinit var basicColor : IntArray
    private var tagData = TagData()
    private lateinit var tag : ArrayList<String>
    private lateinit var adapterProduct: ProductListAdapter
    private lateinit var adapterTag: AdapterSelectTag
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tag = it.getStringArrayList(argTag)
        }
    }

    private lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_view_tag_result, container, false)

        setInitialize()
        setView()

        return rootView
    }
    /** 제품 클릭 -> 자세히 보기 **/
    override fun click(productData: ProductData) {
        (activity as ViewMain).productInfoFragment = ViewProductInfoFragment.productInfo(productData)
        Util.fragmentChanger((activity as ViewMain),(activity as ViewMain).productInfoFragment, TAG)
    }
    /** ///asdasdjaslkfjaslkfjskladjsakl/////////////// **/
    companion object {
        @JvmStatic
        fun newInstance(tag: ArrayList<String>) =
            ViewTagResultFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(argTag, tag)
                }
            }
    }
    /** 초기화 데이터 **/
    private fun setInitialize() {
        Util.progressVisible(true, rootView.rotateLoading, activity)
        basicColor = intArrayOf(
            this.resources.getColor(R.color.white),
            this.resources.getColor(R.color.colorPrimary),
            this.resources.getColor(R.color.black))
        tagData.title.add(TAG_SEARCH_CHECK)
        tagData.tag[TAG_SEARCH_CHECK] = arrayListOf()
        tagData.tag[TAG_SEARCH_CHECK]?.addAll(tag)
        tagData.tagColor[TAG_SEARCH_CHECK] = arrayListOf()
        tagData.tag[TAG_SEARCH_CHECK]?.forEach { _ ->
            tagData.tagColor[TAG_SEARCH_CHECK]?.add(basicColor)
        }
        thread {
            adapterProduct = ProductListAdapter(rootView.context, productData, this)
            rootView.rvProductList.adapter = adapterProduct
            adapterTag = AdapterSelectTag(rootView.context, tagData, null)
            rootView.rvTagList.adapter = adapterTag

            AWSDB.readTagList(tag, productData) //태그 가져오기
            ThreadUtils.runOnUiThread {
                rootView.rvProductList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                    rootView.context,
                    androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                    false
                )
                rootView.rvTagList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                    rootView.context,
                    androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapterProduct.notifyDataSetChanged()
                adapterTag.notifyDataSetChanged()
                Util.progressVisible(false, rootView.rotateLoading, activity)
            }
        }
    }
    private fun setView(){

    }
}
