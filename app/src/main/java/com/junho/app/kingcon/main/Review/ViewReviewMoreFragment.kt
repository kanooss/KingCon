package com.junho.app.kingcon.main.Review

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.junho.app.kingcon.Adapter.ReviewAdapter
import com.junho.app.kingcon.Etc.*
import com.junho.app.kingcon.Etc.CallBack.CallbackAdapter
import com.junho.app.kingcon.Etc.StringData.REVIEW_INDEX_DATE
import com.junho.app.kingcon.Etc.StringData.REVIEW_INDEX_LIKE
import com.junho.app.kingcon.Etc.StringData.REVIEW_INDEX_POINT
import com.junho.app.kingcon.Etc.StringData.REVIEW_READ_NUM
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.Item.ReviewData
import com.junho.app.kingcon.main.Product.View.ViewCreateReview
import com.junho.app.kingcon.main.Product.View.ViewProductInfoFragment.Companion.mainProduct
import com.junho.app.kingcon.main.ViewMain
import com.junho.app.kingcon.R
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_view_review_more.view.*
import kotlinx.android.synthetic.main.fragment_view_review_more.view.rotateLoading
import kotlin.concurrent.thread

class ViewReviewMoreFragment : androidx.fragment.app.Fragment(), CallbackAdapter.Review {
    private var initFlag = false
    private var startKey = arrayListOf<Map<String, String>?>()
    private lateinit var adapterReview: ReviewAdapter
    private var sort = REVIEW_INDEX_LIKE

    private var listFull = false
    private var listAddFlag = false

    private lateinit var rootView: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_view_review_more, container, false)

        setInitialize()
        setView()

        return rootView
    }

    /** 리뷰 수정 **/
    override fun optionReviewCustom(reviewData: ReviewData) {
        val intent = Intent(rootView.context, ViewCreateReview::class.java)
        intent.putExtra(StringData.PRODUCT_DATA, mainProduct)
        startActivityForResult(intent, StringData.REVIEW_CREATE)
    }

    /** 리뷰 삭제 **/
    override fun optionReviewDelete(reviewData: ReviewData) {
        val builder = AlertDialog.Builder(rootView.context)
        builder.setTitle("리뷰 삭제")
        builder.setMessage("리뷰를 삭제하시겠습니까?")
        builder.setPositiveButton("삭제") { _, _ ->
            thread {
                AWSDB.deleteReview(reviewData, mainProduct)
                mainProduct.myReview.removeReview()
                val indexBest = mainProduct.checkInBestReview(mainProduct.myReview)
                if (indexBest != -1) mainProduct.bestReview.removeAt(indexBest)
                val index = mainProduct.checkInReviews(mainProduct.myReview)
                mainProduct.reviews.removeAt(index)
                ThreadUtils.runOnUiThread {
                    adapterReview.notifyItemChanged(index)
                    (activity as ViewMain).productInfoFragment.changeReviewData()
                }
            }
        }
        builder.setNegativeButton("취소") { _, _ -> }
        builder.show()
    }

    /** 리뷰 클릭 -> 자세히 보기 **/
    override fun onReviewDetail(reviewData: ReviewData, position: Int) {
        reviewData.id = mainProduct.id
        (activity as ViewMain).reviewDetailFragment =
            ViewReviewDetailFragment.newInstance(reviewData)
        Util.fragmentChanger(
            (activity as ViewMain),
            (activity as ViewMain).reviewDetailFragment,
            StringData.HOME
        )
    }

    /** 리뷰 좋아요 **/
    override fun onLikeClick(reviewData: ReviewData, position: Int) {
        thread {
            AWSDB.updateReviewLike(reviewData)
            ThreadUtils.runOnUiThread {
                mainProduct.updateReview(reviewData)
                adapterReview.notifyItemChanged(position)
                (activity as ViewMain).productInfoFragment.changeReviewData()
            }
        }
    }

    /** 리뷰 신고 **/
    override fun optionDeclaration() {

    }

    /** 리뷰 정렬 **/
    @SuppressLint("CheckResult")
    private fun reviewSort(sort: String) {
        mainProduct.reviews.clear()
        AWSDB.readProductReview(mainProduct, sort, startKey)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                adapterReview.notifyDataSetChanged()
                Util.progressVisible(false, rootView.rotateLoading, activity)
            }) { it.printStackTrace() }
    }

    /** 리뷰 새로고침 **/
    @SuppressLint("CheckResult")
    private fun reviewRefresh() {
        initFlag = false
        startKey[0] = null
        mainProduct.reviews.clear()
        AWSDB.readProductReview(mainProduct, sort, startKey)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                adapterReview.notifyDataSetChanged()
                initFlag = true
                rootView.layerSwipe.isRefreshing = false
                listAddFlag = true
            }) { it.printStackTrace() }
    }

    //리뷰 추가할거 없을 시 데이터 안가져오고 바로 정렬
    private fun sortData() {
        if (mainProduct.reviews.size % REVIEW_READ_NUM != 0 || startKey[0] == null) {
            when (sort) {
                REVIEW_INDEX_LIKE -> mainProduct.reviews.sortWith(ReviewData.ReviewSortByLike)
                REVIEW_INDEX_DATE -> mainProduct.reviews.sortWith(ReviewData.ReviewSortByDate)
                REVIEW_INDEX_POINT -> mainProduct.reviews.sortWith(ReviewData.ReviewSortByPoint)
            }
            adapterReview.notifyDataSetChanged()
            Util.progressVisible(false, rootView.rotateLoading, activity)
        } else {
            reviewSort(sort)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //리뷰 수정
        if (requestCode == StringData.REVIEW_CREATE && resultCode == StringData.REVIEW_CREATE_DONE) {
            val product = data?.getSerializableExtra(StringData.PRODUCT_DATA) as ProductData
            val index = mainProduct.checkInReviews(product.myReview)
            mainProduct.reviews[index] = product.myReview
            mainProduct.myReview = product.myReview
            adapterReview.notifyItemChanged(index)
            (activity as ViewMain).productInfoFragment.changeReviewData()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ViewReviewMoreFragment()
    }

    @SuppressLint("CheckResult")
    private fun setInitialize() {
        Util.progressVisible(true, rootView.rotateLoading, activity)
        adapterReview = ReviewAdapter(rootView.context, mainProduct.reviews, this)
        startKey.add(null)
        mainProduct.reviews.clear()
        AWSDB.readProductReview(mainProduct, sort, startKey)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                rootView.rvReviewList.adapter = adapterReview
                rootView.rvReviewList.layoutManager = NpaGridLayoutManager(
                    rootView.context,
                    androidx.recyclerview.widget.GridLayoutManager.VERTICAL
                )
                initFlag = true
                listAddFlag = true
                Util.progressVisible(false, rootView.rotateLoading, activity)
            }) { it.printStackTrace() }
    }

    private fun setView() {
        val arrayList = arrayListOf("좋아요 순", "최신 순", "점수 순")

        rootView.spinnerSortReview.adapter =
            ArrayAdapter(rootView.context, android.R.layout.simple_spinner_dropdown_item, arrayList)
        //정렬
        rootView.spinnerSortReview.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Util.progressVisible(true, rootView.rotateLoading, activity)
                    if (position == 0 && initFlag) {    //좋아요순
                        sort = REVIEW_INDEX_LIKE
                        sortData()
                    } else if (position == 1 && initFlag) { //최신순
                        sort = REVIEW_INDEX_DATE
                        sortData()
                    } else if (position == 2 && initFlag) { //점수
                        sort = REVIEW_INDEX_POINT
                        sortData()
                    }
                }
            }
        val term = 3  //새로고침 밑에서 몇번 째
        rootView.rvReviewList.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPosition = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val itemTotalCount = rootView.rvReviewList.adapter?.itemCount?.minus(term)
                if ((lastVisibleItemPosition == itemTotalCount) && !listFull && listAddFlag) {
                    listAddFlag = false
                    AWSDB.readProductReview(mainProduct, sort, startKey)
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.subscribe({
                            MyLog.i("sflskflskflksflskflks")
                            listAddFlag = true
                        }){ it.printStackTrace() }
                }
            }
        })
        //새로고침
        rootView.layerSwipe.setOnRefreshListener {
            reviewRefresh()
        }
    }
}