package com.junho.app.kingcon.main.Product.View

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.junho.app.kingcon.Etc.AWSDB
import com.junho.app.kingcon.Etc.StringData.PRODUCT_DATA
import com.junho.app.kingcon.Etc.StringData.REVIEW_CREATE_DONE
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.main.Product.Contractor.ContractorCreateReview
import com.junho.app.kingcon.main.Product.View.ViewProductInfoFragment.Companion.mainProduct
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.activity_view_create_review.*
import kotlinx.android.synthetic.main.activity_view_create_review.progress
import kotlinx.android.synthetic.main.activity_view_login.*
import kotlin.concurrent.thread

class ViewCreateReview : AppCompatActivity(), ContractorCreateReview.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_create_review)

        setInitialize()
        setView()
    }
    override fun makeDialog(text: String, onReview:() -> Unit) {
        AlertDialog.Builder(this).run {
            setTitle("리뷰")
            setMessage(text)
            setPositiveButton("예") { dialog, which ->
                Util.isSoftKeyView(this@ViewCreateReview, etReview, View.INVISIBLE)
                onReview()
            }
            setNegativeButton("아니오") { _, _ -> }
            show()
        }
    }
    //프로그래스바
    override fun onProgress(visible: Boolean) { Util.progressVisible(visible, progress, this) }
    override fun onFinish(){
        val intent = intent.putExtra(PRODUCT_DATA, mainProduct)
        setResult(REVIEW_CREATE_DONE, intent)
        finish()
    }
    private fun onUpdateReview(productData: ProductData, rating: Float, review: String) {
        thread {
            ThreadUtils.runOnUiThread { onProgress(true) }
            AWSDB.createReview(productData, rating, review)
            ThreadUtils.runOnUiThread { onProgress(false) }
            onFinish()
        }
    }
    private fun onCreateButton(rating: Float, review: String) {
        onCreateReview(mainProduct, ratingBar.rating, etReview.text.toString()).let {
            if(review.isBlank() && rating == 0f) makeDialog("별점없이 후기만 작성하시겠습니까?") { it }
            else if(review.isBlank() && rating != 0f) makeDialog("후기없이 별점만 작성하시겠습니까?") { it }
            else if(review.isBlank() && rating != 0f) makeDialog("리뷰를 작성하시겠습니까?") { it }
        }
    }
    private fun onCreateReview(productData: ProductData, rating: Float, review: String) {
        thread {
            ThreadUtils.runOnUiThread { onProgress(true) }
            AWSDB.createReview(productData, rating, review)
            ThreadUtils.runOnUiThread { onProgress(false) }
            onFinish()
        }
    }
    ////////////////////////////////////////////////////////////
    private fun setInitialize() {}
    private fun setView() {
        Util.getPostPicture(this, mainProduct.id, ivProduct)    //제품 사진
        tvProductName.text = mainProduct.name         //제품 이름
        tvCompany.text = mainProduct.company          //제조사
        ratingBar.rating = mainProduct.myReview.point //내 평가 점수
        //등록 버튼
        btnReviewSuccess.setOnClickListener {
            onCreateButton(ratingBar.rating, etReview.text.toString())
        }
        //수정일 시
        if(mainProduct.myReview.content.isBlank()){
            etReview.setText(mainProduct.myReview.content)
            //등록 버튼
            btnReviewSuccess.setOnClickListener {
                if(mainProduct.myReview.content.isBlank()) {
                    makeDialog("리뷰를 수정하시겠습니까?") { onUpdateReview(mainProduct, ratingBar.rating, etReview.text.toString()) }
                }
            }
        }else{
            //등록 버튼
            btnReviewSuccess.setOnClickListener {
                onCreateButton(ratingBar.rating, etReview.text.toString())
            }
        }
    }
}