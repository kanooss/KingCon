package com.junho.app.kingcon.main.Product.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.junho.app.kingcon.Adapter.InfoAdapter
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.fragment_view_info_more.view.*

class ViewInfoMoreFragment : androidx.fragment.app.Fragment() {
    private val argProductData = "mainProductData"
    private lateinit var  productData: ProductData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productData = it.getSerializable(argProductData) as ProductData
        }
    }
    private val infoTitle = arrayListOf<String>()
    private val infoContent = arrayListOf<String>()
    private val adapter: InfoAdapter by lazy {
        InfoAdapter(rootView.context, infoTitle, infoContent)
    }
    private lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_view_info_more, container, false)

        setInitialize()
        setView()

        return rootView
    }
    private val titleMap =
        mapOf("Ingr" to "전성분","Coun" to "제조국가","Char" to "특징")

    private fun infoSet(){
        infoTitle.add("제품명")
        infoContent.add(productData.name)
        infoTitle.add("제조사")
        infoContent.add(productData.company)
        infoTitle.add("연관 태그")
        infoContent.add(productData.tag.toString())
        productData.info.forEach {
            infoTitle.add(titleMap[it.key] ?: it.key)
            infoContent.add(it.value)
        }
    }

    private fun setInitialize() {
        Util.progressVisible(true, rootView.progress, this.activity)
        infoSet()
        rootView.rvInfoList.adapter = adapter
        rootView.rvInfoList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        Util.progressVisible(false, rootView.progress, this@ViewInfoMoreFragment.activity)
    }
    private fun setView() {

    }
    companion object {
        @JvmStatic
        fun newInstance(productData: ProductData) =
            ViewInfoMoreFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(argProductData, productData)
                }
            }
    }
}