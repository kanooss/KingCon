package com.junho.app.kingcon.Main.Product.Presenter

import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.junho.app.kingcon.Etc.AWSDB
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.Main.Product.Contractor.ContractorCreateReview
import com.junho.app.kingcon.Main.Product.View.ViewCreateReview
import kotlin.concurrent.thread

class PresenterCreateReview(var view: ViewCreateReview) : ContractorCreateReview.Presenter {
    override fun onCreateReview(productData: ProductData, rating: Float, review: String) {
        thread {
            ThreadUtils.runOnUiThread { view.onProgress(true) }
            AWSDB.createReview(productData, rating, review)
            ThreadUtils.runOnUiThread { view.onProgress(false) }
            view.onFinish()
        }
    }
    override fun onUpdateReview(productData: ProductData, rating: Float, review: String) {
        thread {
            ThreadUtils.runOnUiThread { view.onProgress(true) }
            AWSDB.createReview(productData, rating, review)
            ThreadUtils.runOnUiThread { view.onProgress(false) }
            view.onFinish()
        }
    }
    //생성 버튼
    override fun onCreateButton(rating: Float, review: String) {
        if(review != "" && rating == 0f) view.makeDialog("별점없이 후기만 작성하시겠습니까?")
        else if(review == "" && rating != 0f) view.makeDialog("후기없이 별점만 작성하시겠습니까?")
        else if(review != "" && rating != 0f) view.makeDialog("리뷰를 작성하시겠습니까?")
    }
}