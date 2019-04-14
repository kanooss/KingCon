package com.junho.app.kingcon.Main.Product.Contractor

import com.junho.app.kingcon.Item.ProductData

interface ContractorCreateReview {
    interface View{
        fun makeDialog(text: String)
        fun onProgress(visible: Boolean)
        fun onFinish()
    }
    interface Presenter{
        fun onCreateButton(rating: Float, review: String)
        fun onCreateReview(productData: ProductData, rating: Float, review: String)
        fun onUpdateReview(productData: ProductData, rating: Float, review: String)
    }
}