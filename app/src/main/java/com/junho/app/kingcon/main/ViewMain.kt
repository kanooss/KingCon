package com.junho.app.kingcon.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.facebook.login.LoginManager
import com.instacart.library.truetime.TrueTime
import com.junho.app.kingcon.Etc.InitTrueTimeAsyncTask
import com.junho.app.kingcon.Etc.StringData.COLUMN
import com.junho.app.kingcon.Etc.StringData.FACEBOOK
import com.junho.app.kingcon.Etc.StringData.HOME
import com.junho.app.kingcon.Etc.StringData.MY
import com.junho.app.kingcon.Etc.StringData.TAG
import com.junho.app.kingcon.Etc.User
import com.junho.app.kingcon.Item.ColumnData
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.Item.ReviewData
import com.junho.app.kingcon.Login.View.ViewLogin
import com.junho.app.kingcon.main.bottom.Column.ViewColumnDetailFragment
import com.junho.app.kingcon.main.bottom.Column.ViewColumnFragment
import com.junho.app.kingcon.main.bottom.MyPage.ViewActFragment
import com.junho.app.kingcon.main.bottom.MyPage.ViewMyPageFragment
import com.junho.app.kingcon.main.bottom.Tag.ViewTagFragment
import com.junho.app.kingcon.main.bottom.Tag.ViewTagResultFragment
import com.junho.app.kingcon.main.bottom.home.ViewHomeFragment
import com.junho.app.kingcon.main.Product.View.ViewGraphFragment
import com.junho.app.kingcon.main.Product.View.ViewInfoMoreFragment
import com.junho.app.kingcon.main.Product.View.ViewProductInfoFragment
import com.junho.app.kingcon.main.Review.ViewReviewDetailFragment
import com.junho.app.kingcon.main.Review.ViewReviewMoreFragment
import com.junho.app.kingcon.main.Search.ViewSearchFragment
import com.junho.app.kingcon.main.Search.ViewSearchResultFragment
import com.junho.app.kingcon.R
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_view_main.*
import kotlinx.android.synthetic.main.fragment_view_search.*
import java.util.function.Consumer
import android.R.attr.tag
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class ViewMain : AppCompatActivity() {
    private var homeFragment = ViewHomeFragment()
    var searchFragment = ViewSearchFragment()
    var searchResultFragment = ViewSearchResultFragment.newInstance("")
    private var tagFragment = ViewTagFragment()
    var tagResultFragment = ViewTagResultFragment.newInstance(arrayListOf())
    private var columnFragment = ViewColumnFragment()
    var columnDetailFragment = ViewColumnDetailFragment.newInstance(ColumnData())
    private var myPageFragment = ViewMyPageFragment()
    var actMoreFragment = ViewActFragment()
    var productInfoFragment = ViewProductInfoFragment.productInfo(ProductData())
    var productInfoMoreFragment = ViewInfoMoreFragment.newInstance(ProductData())
    var reviewDetailFragment = ViewReviewDetailFragment.newInstance(ReviewData())
    var reviewMoreFragment = ViewReviewMoreFragment.newInstance()
    var graphFragment = ViewGraphFragment.newInstance(ArrayList(),ProductData())

    var beforeProductInfoFragment : Boolean = false
    var beforeReviewMoreFragment : Boolean = false
    var beforeSearchResultFragment : Boolean = false
    var currentHomeFragment : androidx.fragment.app.Fragment? = null
    var currentTagFragment : androidx.fragment.app.Fragment? = null
    var currentColumnFragment : androidx.fragment.app.Fragment? = null
    var currentMyFragment : androidx.fragment.app.Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_main)



        setInitialize()
        setView()
        setMVP()
        //테스트 로그아웃 버튼
        btnTestLogout.setOnClickListener {
            try {
                AWSMobileClient.getInstance().signOut()
            }catch (e: NullPointerException){
                AWSMobileClient.getInstance().initialize(this, object : Callback<UserStateDetails> {
                    override fun onError(e: Exception?) {}
                    override fun onResult(result: UserStateDetails?) {AWSMobileClient.getInstance().signOut()}
                })
            }
            //페이스북 유저일 경우 페이스북 로그아웃
            if(User.provider == FACEBOOK)
                LoginManager.getInstance().logOut()
            finish()
            User.logOut()
            startActivity(Intent(this, ViewLogin::class.java))
        }
    }
    //프레그먼트 스텍 뒤로가기
    private fun fragmentBackPress(fragment : androidx.fragment.app.Fragment, container : String) {
        when(container){
            HOME->{
                supportFragmentManager.beginTransaction().hide(currentHomeFragment!!).show(fragment).commit()
                currentHomeFragment = fragment
            }
            TAG->{
                supportFragmentManager.beginTransaction().hide(currentTagFragment!!).show(fragment).commit()
                currentTagFragment = fragment
            }
            COLUMN->{
                supportFragmentManager.beginTransaction().hide(currentColumnFragment!!).show(fragment).commit()
                currentColumnFragment = fragment
            }
            MY->{
                supportFragmentManager.beginTransaction().hide(currentMyFragment!!).show(fragment).commit()
                currentMyFragment = fragment
            }
        }
    }
    //바탐네비게이션 페이지 변경
    private fun changePage(position: Int) {
        when (position) {
            1 -> {
                if (homeFragment.isHidden && bottomNavPageContainerHome.visibility == View.VISIBLE)  //홈 프래그면 안보이는상태에 바텀네비 누르면 홈으로
                    fragmentBackPress(homeFragment, "HOME")
                bottomNavPageContainerHome.visibility = View.VISIBLE
                bottomNavPageContainerTag.visibility = View.GONE
                bottomNavPageContainerColumn.visibility = View.GONE
                bottomNavPageContainerMy.visibility = View.GONE
            }
            2 -> {
                if (!tagFragment.isAdded)
                    supportFragmentManager.beginTransaction().add(R.id.bottomNavPageContainerTag, tagFragment).commitAllowingStateLoss()
                if (tagFragment.isHidden && bottomNavPageContainerTag.visibility == View.VISIBLE)  //홈 프래그면 안보이는상태에 바텀네비 누르면 홈으로
                    fragmentBackPress(tagFragment, "TAG")
                bottomNavPageContainerTag.visibility = View.VISIBLE
                bottomNavPageContainerHome.visibility = View.GONE
                bottomNavPageContainerColumn.visibility = View.GONE
                bottomNavPageContainerMy.visibility = View.GONE
            }
            3 -> {
                if (!columnFragment.isAdded)
                    supportFragmentManager.beginTransaction().add(R.id.bottomNavPageContainerColumn, columnFragment).commitAllowingStateLoss()
                if (columnFragment.isHidden && bottomNavPageContainerColumn.visibility == View.VISIBLE)  //홈 프래그면 안보이는상태에 바텀네비 누르면 홈으로
                    fragmentBackPress(columnFragment, "COLUMN")
                bottomNavPageContainerColumn.visibility = View.VISIBLE
                bottomNavPageContainerHome.visibility = View.GONE
                bottomNavPageContainerTag.visibility = View.GONE
                bottomNavPageContainerMy.visibility = View.GONE
            }
            4 -> {
                if (!myPageFragment.isAdded)
                    supportFragmentManager.beginTransaction().add(R.id.bottomNavPageContainerMy, myPageFragment).commitAllowingStateLoss()
                if (myPageFragment.isHidden && bottomNavPageContainerMy.visibility == View.VISIBLE)  //홈 프래그면 안보이는상태에 바텀네비 누르면 홈으로
                    fragmentBackPress(myPageFragment, "MY")
                bottomNavPageContainerMy.visibility = View.VISIBLE
                myPageFragment.setData()
                bottomNavPageContainerHome.visibility = View.GONE
                bottomNavPageContainerTag.visibility = View.GONE
                bottomNavPageContainerColumn.visibility = View.GONE
            }
        }
    }
    //백버튼
    override fun onBackPressed() {
        setView()
        if(bottomNavPageContainerHome.visibility == View.VISIBLE) {
            bottomNavigation.visibility = View.VISIBLE
            //홈 페이지 켜져있을 때
            if (searchFragment.isVisible) { //검색화면에서 홈
                fragmentBackPress(homeFragment,HOME)
                searchFragment.etSearchProd.setText("")
            } else if (productInfoFragment.isVisible) { //제품리뷰에서 홈 또는 검색 결과
                if(beforeSearchResultFragment)
                    fragmentBackPress(searchResultFragment,HOME)
                else
                    fragmentBackPress(homeFragment, HOME)
            } else if (productInfoMoreFragment.isVisible) { //정보 더보기 에서 제품리뷰
                fragmentBackPress(productInfoFragment,HOME)
            } else if (searchResultFragment.isVisible) { //검색결과 에서 제품리뷰
                fragmentBackPress(searchFragment,HOME)
            } else if (reviewMoreFragment.isVisible) { //리뷰더보기에서 제품리뷰
                fragmentBackPress(productInfoFragment,HOME)
            } else if (reviewDetailFragment.isVisible){ //리뷰자세히보기에서 백버튼
                when {
                    beforeProductInfoFragment -> fragmentBackPress(productInfoFragment, HOME)//전에 제품 정보 였을경우 리뷰 자세히 -> 정보 보기
                    beforeReviewMoreFragment -> fragmentBackPress(reviewMoreFragment, HOME) //전에 리뷰 더보기 였을경우 리뷰 자세히 -> 리뷰 더보기
                    else -> fragmentBackPress(searchResultFragment,HOME)                     //전에 제품 정보 였을경우
                } //제품 정보보기로
            } else if(graphFragment.isVisible){
                fragmentBackPress(productInfoFragment,HOME)
            } else if(homeFragment.isVisible) { //홈 프래그면 종료
                super.onBackPressed()
            }
        }else if(bottomNavPageContainerTag.visibility == View.VISIBLE){   //태그검색에서 홈
            when {
                tagResultFragment.isVisible  //태그검색 결과에서 태그 검색 페이지로
                -> fragmentBackPress(tagFragment, TAG)
                productInfoFragment.isVisible //제품 정보에서 태그검색 결과 페이지로
                -> fragmentBackPress(tagResultFragment, TAG)
                tagFragment.isVisible //  마이페이지에서 홈
                -> bottomNavigation.selectedItemId = R.id.navigation_home
            }
        }else if(bottomNavPageContainerColumn.visibility == View.VISIBLE){//     팁에서 홈
            if(columnDetailFragment.isVisible)  //칼럼 내용 보기에서 칼럼 리스트로
                fragmentBackPress(columnFragment, COLUMN)
            else if(columnFragment.isVisible) //  마이페이지에서 홈
                bottomNavigation.selectedItemId = R.id.navigation_home
        }else if(bottomNavPageContainerMy.visibility == View.VISIBLE){//        내정보에서 홈
            when {
                actMoreFragment.isVisible  //활동 리스트 전체보기에서 마이페이지
                -> fragmentBackPress(myPageFragment,MY)
                productInfoFragment.isVisible //평가리스트 제품리뷰에서 마이페이지
                -> fragmentBackPress(actMoreFragment, MY)
                myPageFragment.isVisible //  마이페이지에서 홈
                -> bottomNavigation.selectedItemId = R.id.navigation_home
            }
        }
    }
    @SuppressLint("CheckResult")
    private fun initTrueTime(ctx: Context) {
        if (isNetworkConnected(ctx)) {
            if (!TrueTime.isInitialized()) {
                Observable.just(ctx)
                    .subscribeOn(Schedulers.io())
                    .subscribe {
                        TrueTime.build()
                            .withSharedPreferences(it)
                            .withNtpHost("time.google.com")
                            .withLoggingEnabled(false)
                            .withConnectionTimeout(31428)
                            .initialize()
                    }
            }
        }
    }

    private fun isNetworkConnected(context: Context): Boolean{
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = cm.activeNetworkInfo

        return ni != null && ni.isConnected
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("CommitPrefEdits")
    private fun setInitialize() {
        if(!homeFragment.isAdded) {
            supportFragmentManager.beginTransaction().add(R.id.bottomNavPageContainerHome, searchFragment).hide(searchFragment).commitAllowingStateLoss()
            supportFragmentManager.beginTransaction().add(R.id.bottomNavPageContainerHome, homeFragment).show(homeFragment).commitAllowingStateLoss()
            currentHomeFragment = homeFragment
            currentTagFragment = tagFragment
            currentColumnFragment = columnFragment
            currentMyFragment = myPageFragment
        }
        bottomNavigation.run {
            onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        changePage(1)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_tag -> {
                        changePage(2)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_column -> {
                        changePage(3)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_myPage-> {
                        //야매로 고침
//                        if(bottomNavPageContainerMy.visibility == View.GONE)
                        changePage(4)
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }
            enableAnimation(false)
            enableItemShiftingMode(false)
            enableShiftingMode(false)
        }
        //네트워크 시간
        initTrueTime(this)
        //쿠팡 데이터 가져오
//        Coupang.getDeepLink()
//        Coupang.getPL()
    }
    private fun setView() {
        textbtn.setOnClickListener {
            Log.d("fragmentTest H ", homeFragment.isVisible.toString())
            Log.d("fragmentTest T ", tagFragment.isVisible.toString())
            Log.d("fragmentTest C ", columnFragment.isVisible.toString())
            Log.d("fragmentTest M ", myPageFragment.isVisible.toString())
            Log.d("fragmentTest S ", searchFragment.isVisible.toString())
            Log.d("fragmentTest I ", productInfoFragment.isVisible.toString())
            Log.d("fragmentTest IM", productInfoMoreFragment.isVisible.toString())
            Log.d("fragmentTest SR", searchResultFragment.isVisible.toString())
            Log.d("fragmentTest RM", reviewMoreFragment.isVisible.toString())
            Log.d("fragmentTest RD", reviewDetailFragment.isVisible.toString())
            Log.d(":","\n")
        }
    }
    private fun setMVP() {
    }
}
