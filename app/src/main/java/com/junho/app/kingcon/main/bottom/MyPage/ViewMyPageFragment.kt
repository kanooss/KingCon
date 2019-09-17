package com.junho.app.kingcon.main.bottom.MyPage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.engine.Initializable
import com.junho.app.kingcon.Etc.User
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.fragment_view_my_page.view.*
import java.lang.Exception

class ViewMyPageFragment : androidx.fragment.app.Fragment() {

    private lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_view_my_page, container, false)

        setData()

        return rootView
    }
    //유저 데이터 갱신
    fun setData(){
        try {
            rootView.tvRatingNum.text = User.rating.toString()
            rootView.tvWishNum.text = User.wish.toString()
            rootView.tvReviewNum.text = User.review.toString()
        }catch (e: Exception){
            Log.e("initializeError",e.toString())
        }
    }


    //////////////////////////////////////////////////////////////////
    companion object {
        @JvmStatic
        fun newInstance() = ViewMyPageFragment()
    }
}
