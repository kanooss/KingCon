package com.junho.app.kingcon.Main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
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
import com.junho.app.kingcon.Main.BottomNav.Column.ViewColumnDetailFragment
import com.junho.app.kingcon.Main.BottomNav.Column.ViewColumnFragment
import com.junho.app.kingcon.Main.BottomNav.MyPage.ViewActFragment
import com.junho.app.kingcon.Main.BottomNav.MyPage.ViewMyPageFragment
import com.junho.app.kingcon.Main.BottomNav.Tag.ViewTagFragment
import com.junho.app.kingcon.Main.BottomNav.Tag.ViewTagResultFragment
import com.junho.app.kingcon.Main.BottomNav.ViewHomeFragment
import com.junho.app.kingcon.Main.Product.View.ViewGraphFragment
import com.junho.app.kingcon.Main.Product.View.ViewInfoMoreFragment
import com.junho.app.kingcon.Main.Product.View.ViewProductInfoFragment
import com.junho.app.kingcon.Main.Review.ViewReviewDetailFragment
import com.junho.app.kingcon.Main.Review.ViewReviewMoreFragment
import com.junho.app.kingcon.Main.Search.ViewSearchFragment
import com.junho.app.kingcon.Main.Search.ViewSearchResultFragment
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.activity_view_main.*
import kotlinx.android.synthetic.main.fragment_view_search.*


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
    var reviewDetailFragment = ViewReviewDetailFragment.newInstance(ReviewData(), ProductData())
    var reviewMoreFragment = ViewReviewMoreFragment.newInstance(ProductData())
    var graphFragment = ViewGraphFragment.newInstance(ArrayList(),ProductData())

    var beforeProductInfoFragment : Boolean = false
    var beforeSearchResultFragment : Boolean = false
    var currentHomeFragment : Fragment? = null
    var currentTagFragment : Fragment? = null
    var currentColumnFragment : Fragment? = null
    var currentMyFragment : Fragment? = null

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
    private fun fragmentBackPress(fragment : Fragment, container : String) {
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
    private fun changePage(position: Int){
        when(position){
            1->{
                bottomNavPageContainerHome.visibility = View.VISIBLE
                bottomNavPageContainerTag.visibility = View.GONE
                bottomNavPageContainerColumn.visibility = View.GONE
                bottomNavPageContainerMy.visibility = View.GONE
                if(homeFragment.isHidden && bottomNavigation.selectedItemId == R.id.navigation_home)  //홈 프래그면 안보이는상태에 바텀네비 누르면 홈으로
                    fragmentBackPress(homeFragment,"HOME")
            }
            2->{
                bottomNavPageContainerTag.visibility = View.VISIBLE
                bottomNavPageContainerHome.visibility = View.GONE
                bottomNavPageContainerColumn.visibility = View.GONE
                bottomNavPageContainerMy.visibility = View.GONE
                if(tagFragment.isHidden && bottomNavigation.selectedItemId == R.id.navigation_tag)  //홈 프래그면 안보이는상태에 바텀네비 누르면 홈으로
                    fragmentBackPress(tagFragment,"TAG")
            }
            3->{
                bottomNavPageContainerColumn.visibility = View.VISIBLE
                bottomNavPageContainerHome.visibility = View.GONE
                bottomNavPageContainerTag.visibility = View.GONE
                bottomNavPageContainerMy.visibility = View.GONE
            }
            4->{
                myPageFragment.setData()
                bottomNavPageContainerMy.visibility = View.VISIBLE
                bottomNavPageContainerHome.visibility = View.GONE
                bottomNavPageContainerTag.visibility = View.GONE
                bottomNavPageContainerColumn.visibility = View.GONE
                if(myPageFragment.isHidden && bottomNavigation.selectedItemId == R.id.navigation_myPage)  //홈 프래그면 안보이는상태에 바텀네비 누르면 홈으로
                    fragmentBackPress(myPageFragment,"MY")
            }
        }
    }
    //백버튼
    override fun onBackPressed() {
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
                if(beforeProductInfoFragment)   //전에 제품 정보 였을경우
                    fragmentBackPress(productInfoFragment, HOME) //리뷰 더보기로
                else
                    fragmentBackPress(searchResultFragment,HOME) //제품 정보보기로
            } else if(graphFragment.isVisible){
                fragmentBackPress(productInfoFragment,HOME)
            } else if(homeFragment.isVisible) { //홈 프래그면 종료
                super.onBackPressed()
            }
        }else if(bottomNavPageContainerTag.visibility == View.VISIBLE){   //태그검색에서 홈
            if(tagResultFragment.isVisible)  //태그검색 결과에서 태그 검색 페이지로
                fragmentBackPress(tagFragment, TAG)
            else if(tagFragment.isVisible) //  마이페이지에서 홈
                bottomNavigation.selectedItemId = R.id.navigation_home

        }else if(bottomNavPageContainerColumn.visibility == View.VISIBLE){//     팁에서 홈
            if(columnDetailFragment.isVisible)  //칼럼 내용 보기에서 칼럼 리스트로
                fragmentBackPress(columnFragment, COLUMN)
            else if(columnFragment.isVisible) //  마이페이지에서 홈
                bottomNavigation.selectedItemId = R.id.navigation_home
        }else if(bottomNavPageContainerMy.visibility == View.VISIBLE){//        내정보에서 홈
            if(actMoreFragment.isVisible)  //활동 리스트 전체보기에서 마이페이지
                fragmentBackPress(myPageFragment,MY)
            else if(productInfoFragment.isVisible) //평가리스트 제품리뷰에서 마이페이지
                fragmentBackPress(actMoreFragment, MY)
            else if(myPageFragment.isVisible) //  마이페이지에서 홈
                bottomNavigation.selectedItemId = R.id.navigation_home
        }
    }
    private fun initTrueTime(ctx: Context) {
        if (isNetworkConnected(ctx)) {
            if (!TrueTime.isInitialized()) {
                val trueTime = InitTrueTimeAsyncTask(ctx)
                trueTime.execute()
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
        if(!searchFragment.isAdded) {
            supportFragmentManager.beginTransaction().add(R.id.bottomNavPageContainerHome, searchFragment).hide(searchFragment).commitAllowingStateLoss()
            supportFragmentManager.beginTransaction().add(R.id.bottomNavPageContainerHome, homeFragment).hide(homeFragment).commitAllowingStateLoss()
            supportFragmentManager.beginTransaction().add(R.id.bottomNavPageContainerTag, tagFragment).commitAllowingStateLoss()
            supportFragmentManager.beginTransaction().add(R.id.bottomNavPageContainerColumn, columnFragment).commitAllowingStateLoss()
            supportFragmentManager.beginTransaction().add(R.id.bottomNavPageContainerMy, myPageFragment).commitAllowingStateLoss()
            supportFragmentManager.beginTransaction().show(homeFragment).commitAllowingStateLoss()
            currentHomeFragment = homeFragment
            currentTagFragment = tagFragment
            currentColumnFragment = columnFragment
            currentMyFragment = myPageFragment
        }
        bottomNavigation.onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    changePage(1)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_tag -> {
                    changePage(2)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    changePage(3)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_myPage-> {
                    changePage(4)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
        bottomNavigation.enableAnimation(false)
        bottomNavigation.enableItemShiftingMode(false)
        bottomNavigation.enableShiftingMode(false)
        //네트워크 시간
        initTrueTime(this)
        //쿠팡 데이터 가져오
//        Coupang.getDeepLink()
//        Coupang.getPL()
    }
    private fun setView() {
        textbtn.setOnClickListener {
            Log.d("fragmentTest H ", homeFragment.isHidden.toString())
            Log.d("fragmentTest H ", homeFragment.isVisible.toString())
            Log.d("fragmentTest S ", searchFragment.isVisible.toString())
            Log.d("fragmentTest I "  , productInfoFragment.isVisible.toString())
            Log.d("fragmentTest IM", productInfoMoreFragment.isVisible.toString())
            Log.d("fragmentTest SR", searchResultFragment.isVisible.toString())
            Log.d("fragmentTest RM", reviewMoreFragment.isVisible.toString())
            Log.d("fragmentTest RM", reviewMoreFragment.isHidden.toString())
            Log.d("fragmentTest RD", reviewDetailFragment.isVisible.toString())
            Log.d("","    ")

        }
    }
    private fun setMVP() {
    }
}
