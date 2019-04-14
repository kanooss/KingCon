package com.junho.app.kingcon.Main.BottomNav

import android.content.Intent
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
import com.junho.app.kingcon.Main.CreatePost
import com.junho.app.kingcon.Etc.StringData.CONDOM_TYPE
import com.junho.app.kingcon.Etc.StringData.HOME
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.Main.Product.View.ViewProductInfoFragment
import com.junho.app.kingcon.Main.ViewMain
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.fragment_view_home.view.*
import kotlin.concurrent.thread


class ViewHomeFragment : Fragment(), CallbackAdapter.Product {
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private lateinit var  param1: String
    private lateinit var  param2: String
    private lateinit var productListAdapter : ProductListAdapter
    private val productData = arrayListOf<ProductData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_view_home, container, false)

        initialize()
        setView()
        return rootView
    }
    //제품 클릭
    override fun click(productData: ProductData) {
        (activity as ViewMain).productInfoFragment = ViewProductInfoFragment.productInfo(productData)
        Util.fragmentChanger((activity as ViewMain),(activity as ViewMain).productInfoFragment, HOME)
    }
    private fun refresh(){
        thread {
            productData.clear()
            AWSDB.readProduct(CONDOM_TYPE, productData)
            rootView.rvProductList.run {
                ThreadUtils.runOnUiThread {
                    adapter?.notifyDataSetChanged()
                    rootView.refresh.isRefreshing = false
                }
            }
        }
    }
    /////////////////////////////////////////////////////
    private fun initialize() {
        productListAdapter = ProductListAdapter(rootView.context, productData, this@ViewHomeFragment)
        thread {
            AWSDB.readProduct(CONDOM_TYPE, productData)
            rootView.rvProductList.run {
                ThreadUtils.runOnUiThread {
                    adapter = productListAdapter
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
            }
        }
    }
    private fun setView() {
        //제품 클릭 -> 정보, 리뷰 보기
        rootView.addProduct.setOnClickListener { startActivity(Intent(rootView.context, CreatePost::class.java)) }
        rootView.refresh.setOnRefreshListener { refresh() } //새로고침
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewHomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
