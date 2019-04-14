package com.junho.app.kingcon.Main.BottomNav.MyPage

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.junho.app.kingcon.Etc.User
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.fragment_view_my_page.view.*

class ViewMyPageFragment : Fragment() {

    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private lateinit var  param1: String
    private lateinit var  param2: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_view_my_page, container, false)

        return rootView
    }
    //유저 데이터 갱신
    fun setData(){
        rootView.tvRatingNum.text = User.rating.toString()
        rootView.tvWishList.text = User.wish.toString()
        rootView.tvReviewNum.text = User.review.toString()
    }

    //////////////////////////////////////////////////////////////////
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewMyPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
