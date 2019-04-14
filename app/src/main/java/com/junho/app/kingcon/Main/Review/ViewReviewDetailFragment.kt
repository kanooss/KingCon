package com.junho.app.kingcon.Main.Review

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.junho.app.kingcon.Adapter.ReviewDetailAdapter
import com.junho.app.kingcon.Etc.AWSDB
import com.junho.app.kingcon.Etc.CallBack.CallbackAdapter
import com.junho.app.kingcon.Etc.StringData
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.Item.ReviewComment
import com.junho.app.kingcon.Item.ReviewData
import com.junho.app.kingcon.Main.Product.View.ViewCreateReview
import com.junho.app.kingcon.Main.ViewMain
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.fragment_view_review_detail.view.*
import kotlin.concurrent.thread

@Suppress("DEPRECATION")
class ViewReviewDetailFragment : Fragment(), CallbackAdapter.ReviewDetail {
    private lateinit var adapterReview: ReviewDetailAdapter
    private var review = ReviewData()
    private var product = ProductData()
    private var listFull: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            review = it.getSerializable("reviewData") as ReviewData
            product = it.getSerializable("productData") as ProductData
        }
    }

    lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_view_review_detail, container, false)

        setInitialize()
        setView()
        return rootView
    }

    /** 리뷰 신고 */
    override fun optionDeclaration() {

    }
    /** 리뷰 수정 */
    override fun optionReviewCustom(reviewData: ReviewData) {
        val intent = Intent(rootView.context, ViewCreateReview::class.java)
        intent.putExtra(StringData.PRODUCT_DATA,product)
        startActivityForResult(intent, StringData.REVIEW_CREATE)
    }
    /** 리뷰삭제 */
    override fun optionReviewDelete(reviewData: ReviewData) {
        val builder = AlertDialog.Builder(rootView.context)
        builder.setTitle("리뷰 삭제")
        builder.setMessage("리뷰를 삭제하시겠습니까?")
        builder.setPositiveButton("삭제") { _, _ ->
            thread {
                AWSDB.deleteReview(reviewData, product)
            }
            val index = product.checkInBestReview(product.myReview)
            if(index != -1)
                ThreadUtils.runOnUiThread {
                    product.bestReview.removeAt(index)
                }
            product.myReview.removeReview()
            (activity as ViewMain).productInfoFragment.changeReviewData(reviewData)
            (activity as ViewMain).onBackPressed()
        }
        builder.setNegativeButton("취소") { _, _ -> }
        builder.show()
    }
    /** 리뷰 좋아요 클릭 */
    override fun onLikeClick(reviewData: ReviewData, position: Int) {
        thread {
            AWSDB.updateReviewLike(reviewData)
            ThreadUtils.runOnUiThread{
                adapterReview.notifyItemChanged(position)
            }
            (activity as ViewMain).productInfoFragment.changeReviewData(reviewData)
        }
    }
    /** 리뷰 댓글 좋아요 클릭 */
    override fun onCommentLikeClick(reviewData: ReviewComment, position: Int) {
        thread {
            AWSDB.updateCommentLike(review, reviewData, position)
            ThreadUtils.runOnUiThread { adapterReview.notifyItemChanged(position) }
        }
    }
    /** 리뷰 댓글 삭제 클릭 */
    override fun onDeleteLikeClick(reviewData: ReviewComment, position: Int) {
        val builder = AlertDialog.Builder(rootView.context)
        builder.setTitle("댓글 삭제")
        builder.setMessage("댓글을 삭제하시겠습니까?")
        builder.setPositiveButton("삭제") { _, _ ->
            thread {
                AWSDB.deleteCommentData(review, position)
                review.removeComment(reviewData)
            }
        }
        builder.setNegativeButton("취소") { _, _ -> }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //리뷰 생성
        if(requestCode == StringData.REVIEW_CREATE && resultCode == StringData.REVIEW_CREATE_DONE){
            product = data?.getSerializableExtra(StringData.PRODUCT_DATA) as ProductData
            review.content = product.myReview.content
            val index = product.checkInBestReview(product.myReview)
            if(product.bestReview.size<2 && index == -1)
                product.bestReview.add(product.myReview)
            if(index != -1)
                product.bestReview[index] = product.myReview
            (activity as ViewMain).productInfoFragment.changeReviewData(product.myReview)
            adapterReview.notifyDataSetChanged()
        }
    }
    /** ////////////////////////////@!#!@$#^$$^#%@$!%$#^$%&^%^#%@^%$&^%*^&#^%@!$%#////////////// **/
    companion object {
        @JvmStatic
        fun newInstance(reviewData: ReviewData, productData: ProductData) =
            ViewReviewDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("reviewData", reviewData)
                    putSerializable("productData", productData)
                }
            }
    }
    private fun setInitialize() {
        Util.progressVisible(true, rootView.rotateLoading, activity!!)
        review.reviewComment.clear()
        adapterReview = ReviewDetailAdapter(rootView.context, product, review, this)
        adapterReview.setHasStableIds(true) //깜빡임 제거
        rootView.rvCommentList.adapter = adapterReview
        rootView.rvCommentList.layoutManager = LinearLayoutManager(rootView.context, LinearLayoutManager.VERTICAL, false)
        thread {
            if(listFull) {
                listFull = AWSDB.readComment(review)
                ThreadUtils.runOnUiThread {
                    adapterReview.notifyDataSetChanged()
                    Util.progressVisible(false, rootView.rotateLoading, activity!!)
                }
            }else
                ThreadUtils.runOnUiThread {
                    Util.progressVisible(false, rootView.rotateLoading, activity!!)
                }
        }
    }
    private fun setView(){
        /** 입력 감지 (공백 x) */
        rootView.etInputComment.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(!(s!!.isEmpty() || s.toString().replace(" ","").replace("\n","").isEmpty())){
                    rootView.btnSendComment.isEnabled = true
                    rootView.btnSendComment.setTextColor(rootView.resources.getColor(R.color.colorPrimary))
                }
                else{
                    rootView.btnSendComment.isEnabled = false
                    rootView.btnSendComment.setTextColor(rootView.resources.getColor(R.color.gray1))
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        /** 댓글 달기 버튼 클릭 */
        rootView.btnSendComment.setOnClickListener {
            Util.isSoftKeyView(rootView.context, rootView.etInputComment, View.INVISIBLE)
            thread {
                AWSDB.createComment(review, rootView.etInputComment.text.toString())
                //입력창 공백 만들기
                ThreadUtils.runOnUiThread {
                    rootView.etInputComment.setText("")
                    adapterReview.notifyItemInserted(review.reviewComment.size)
                    rootView.rvCommentList.smoothScrollToPosition(review.reviewComment.size)
                    adapterReview.notifyItemChanged(0)
                }
            }
        }
        /** 새로고침 **/
        rootView.layerSwipe.setOnRefreshListener {
            thread {
                review.reviewComment.clear()
                listFull = AWSDB.readComment(review)
                ThreadUtils.runOnUiThread {
                    adapterReview.notifyDataSetChanged()
                    rootView.layerSwipe.isRefreshing = false
                }
            }
        }
    }
}
