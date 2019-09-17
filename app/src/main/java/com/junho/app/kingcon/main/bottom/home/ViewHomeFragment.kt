package com.junho.app.kingcon.main.bottom.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.view.clicks
import com.junho.app.kingcon.Adapter.ProductListAdapter
import com.junho.app.kingcon.Etc.AWSDB
import com.junho.app.kingcon.Etc.CallBack.CallbackAdapter
import com.junho.app.kingcon.Etc.Product
import com.junho.app.kingcon.Etc.StringData.CONDOM_TYPE
import com.junho.app.kingcon.Etc.StringData.HOME
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.Etc.listScrolled
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.R
import com.junho.app.kingcon.databinding.FragmentViewHomeBinding
import com.junho.app.kingcon.main.CreatePost
import com.junho.app.kingcon.main.Product.View.ViewProductInfoFragment
import com.junho.app.kingcon.main.Search.ViewSearchFragment
import com.junho.app.kingcon.main.ViewMain
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_view_home.view.*


@SuppressLint("CheckResult")
class ViewHomeFragment : androidx.fragment.app.Fragment(), CallbackAdapter.Product {
    val REFRESH = 1001
    val INITIALIZE = 1002
    private lateinit var productListAdapter: ProductListAdapter

    private val home: ViewModelHome
            by lazy {
                ViewModelProviders.of(this, ViewModelFactoryHome).get(ViewModelHome::class.java)
            }

    private lateinit var rootView: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_view_home, container, false)
        val binding = DataBindingUtil.inflate<FragmentViewHomeBinding>(
            inflater,
            R.layout.fragment_view_home,
            container,
            false
        )
        binding.home = home

        initialize()
        setView()
        return rootView
    }

    //제품 클릭
    override fun click(productData: ProductData) {
        (activity as ViewMain).productInfoFragment =
            ViewProductInfoFragment.productInfo(productData)
        Util.fragmentChanger(
            (activity as ViewMain),
            (activity as ViewMain).productInfoFragment,
            HOME
        )
    }

    private fun getProductData(flag: Int) {
        rootView.rvProductList.listScrolled(false)
        Observable
            .zip(AWSDB.readProduct()) {
                it.forEach { data ->
                    mainProductData.add((data as Product).items[0])
                    mainProductData[mainProductData.size-1].type = CONDOM_TYPE
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                if (flag == INITIALIZE) {
                    rootView.rvProductList.adapter = productListAdapter
                    rootView.rvProductList.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    Util.progressVisible(false, rootView.progress, this@ViewHomeFragment.activity)
                } else {
                    productListAdapter.notifyDataSetChanged()
                    rootView.refresh.isRefreshing = false
                }
                rootView.rvProductList.listScrolled(true)
            },{
                Log.e("error : ", it.toString())
            })
    }

    /////////////////////////////////////////////////////
    @SuppressLint("WrongConstant")
    private fun initialize() {
        productListAdapter =
            ProductListAdapter(rootView.context, mainProductData, this@ViewHomeFragment)
        if (mainProductData.size == 0) {
            Util.progressVisible(true, rootView.progress, this@ViewHomeFragment.activity)
            getProductData(INITIALIZE)
        } else {
            rootView.rvProductList.adapter = productListAdapter
            rootView.rvProductList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setView() {
        //제품 클릭 -> 정보, 리뷰 보기
        rootView.addProduct.clicks()
            .subscribe {
                startActivity(
                    Intent(
                        rootView.context,
                        CreatePost::class.java
                    )
                )
            }
        //새로고침
        rootView.refresh.setOnRefreshListener {
            mainProductData.clear()
            getProductData(REFRESH)
        }
        rootView.layerSearch.setOnClickListener {
            (activity as ViewMain).searchFragment = ViewSearchFragment.newInstance()
            Util.fragmentChanger(
                (activity as ViewMain),
                (activity as ViewMain).searchFragment,
                HOME
            )
        }
    }

    companion object {
        val mainProductData = arrayListOf<ProductData>()
    }
}
