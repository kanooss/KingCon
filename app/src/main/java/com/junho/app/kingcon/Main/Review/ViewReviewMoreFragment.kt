package com.junho.app.kingcon.Main.Review

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.R

class ViewReviewMoreFragment : Fragment() {
    private val ARG_PARAM1 = "param1"
    private lateinit var  productData: ProductData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productData = it.getSerializable(ARG_PARAM1) as ProductData
        }
    }

    private lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_view_review_more, container, false)

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(productData: ProductData) =
            ViewReviewMoreFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, productData)
                }
            }
    }
}