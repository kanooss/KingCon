package com.junho.app.kingcon.Main.Product.View

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.junho.app.kingcon.Etc.StringData.PRODUCT_DATA
import com.junho.app.kingcon.Etc.StringData.REVIEW_CREATE_DONE
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.Main.Product.Contractor.ContractorCreateReview
import com.junho.app.kingcon.Main.Product.Presenter.PresenterCreateReview
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.activity_view_create_review.*

class ViewCreateReview : AppCompatActivity(), ContractorCreateReview.View {
    private var presenter = PresenterCreateReview(this)

    lateinit var productData: ProductData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_create_review)

        setInitialize()
        setView()
    }
    override fun makeDialog(text: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("리뷰")
        builder.setMessage(text)
        builder.setPositiveButton("예") { dialog, which ->
            presenter.onCreateReview(productData, ratingBar.rating, etReview.text.toString())
        }
        builder.setNegativeButton("아니오") { _, _ -> }
        builder.show()
    }
    //프로그래스바
    override fun onProgress(visible: Boolean) { Util.progressVisible(visible, progress, this) }
    override fun onFinish(){
        val intent = intent.putExtra(PRODUCT_DATA, productData)
        setResult(REVIEW_CREATE_DONE, intent)
        finish()
    }
    ////////////////////////////////////////////////////////////
    private fun setInitialize() {
        productData = intent.getSerializableExtra(PRODUCT_DATA) as ProductData
    }
    private fun setView() {
        //제품 사진
        Util.getPostPicture(this, productData.id, ivProduct)
        //제품 이름
        tvProductName.text = productData.name
        //제조사
        tvCompany.text = productData.company
        //등록 버튼
        btnReviewSuccess.setOnClickListener { presenter.onCreateButton(ratingBar.rating, etReview.text.toString()) }
        ratingBar.rating = productData.myReview.point
        //수정일 시
        if(productData.myReview.content != ""){
            etReview.setText(productData.myReview.content)
            //등록 버튼
            btnReviewSuccess.setOnClickListener {
                if(productData.myReview.content != "") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("리뷰 수정")
                    builder.setMessage("리뷰를 수정하시겠습니까?")
                    builder.setPositiveButton("수정") { dialog, which ->
                        presenter.onUpdateReview(productData, ratingBar.rating, etReview.text.toString())
                    }
                    builder.setNegativeButton("취소") { _, _ -> }
                    builder.show()
                }
            }
        }else{
            //등록 버튼
            btnReviewSuccess.setOnClickListener {
                presenter.onCreateButton(ratingBar.rating, etReview.text.toString())
            }
        }
    }
}
