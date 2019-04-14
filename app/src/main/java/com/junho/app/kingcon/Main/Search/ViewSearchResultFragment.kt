package com.junho.app.kingcon.Main.Search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.junho.app.kingcon.Adapter.ProductListAdapter
import com.junho.app.kingcon.Etc.AWSDB
import com.junho.app.kingcon.Etc.CallBack.CallbackAdapter
import com.junho.app.kingcon.Etc.StringData.HOME
import com.junho.app.kingcon.Etc.StringData.TAG
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.fragment_view_search_result.view.*
import kotlin.concurrent.thread

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ViewSearchResultFragment : Fragment(), CallbackAdapter.Product {
    private val argSearchText = "searchText"
    private val productData = arrayListOf<ProductData>()
    private lateinit var searchText: String
    private lateinit var tag : ArrayList<String>
    private lateinit var adapterProduct: ProductListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            searchText = it.getString(searchText, "")
        }
    }

    private lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_view_search_result, container, false)

        setInitialize()
        setView()

        return rootView
    }
    /** 제품 클릭 -> 자세히 보기 **/
    override fun click(productData: ProductData) {

    }
    /** ///asdasdjaslkfjaslkfjskladjsakl/////////////// **/

    companion object {
        @JvmStatic
        fun newInstance(search: String) =
            ViewSearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString(argSearchText, search)
                }
            }
    }
    /** 초기화 데이터 **/
    private fun setInitialize() {
        thread {
            adapterProduct = ProductListAdapter(rootView.context, productData, this)
            rootView.rvSearchProductList.adapter = adapterProduct
            ThreadUtils.runOnUiThread {
                rootView.ivSearch.visibility = View.GONE
                rootView.searchText.visibility = View.GONE
                rootView.rcSelectTag.visibility = View.VISIBLE
            }
            AWSDB.readTagList(tag, productData) //태그 가져오기
            ThreadUtils.runOnUiThread {
                rootView.rvSearchProductList.layoutManager = LinearLayoutManager(rootView.context, LinearLayoutManager.VERTICAL, false)
                adapterProduct.notifyDataSetChanged()
            }
        }
    }
    private fun setView(){

    }
}
