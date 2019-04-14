package com.junho.app.kingcon.Main.Product.View

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.junho.app.kingcon.Etc.AWSDB
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.R
import kotlin.concurrent.thread

class ViewInfoMoreFragment : Fragment() {
    private val argProductData = "productData"
    private lateinit var  productData: ProductData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productData = it.getSerializable(argProductData) as ProductData
        }
    }

    private lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_view_info_more, container, false)

        setInitialize()
        setView()

        return rootView
    }


    private fun setInitialize() {
        thread {
//            AWSDB.readInfoData(productData)
            ThreadUtils.runOnUiThread {

            }
        }
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