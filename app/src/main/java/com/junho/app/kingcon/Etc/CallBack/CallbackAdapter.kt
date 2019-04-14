package com.junho.app.kingcon.Etc.CallBack

import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.Item.ReviewComment
import com.junho.app.kingcon.Item.ReviewData

interface CallbackAdapter {
    interface Tag{
        fun tagClick(title: String, position: Int)
    }
    interface Product{
        fun click(productData: ProductData)
    }
    interface Review{
        fun optionDeclaration()
        fun optionReviewCustom(reviewData: ReviewData)
        fun optionReviewDelete(reviewData: ReviewData)
        fun onLikeClick(reviewData: ReviewData, position: Int)
        fun onReviewDetail(reviewData: ReviewData, position: Int)
    }
    interface ReviewDetail {
        fun optionDeclaration()
        fun optionReviewCustom(reviewData: ReviewData)
        fun optionReviewDelete(reviewData: ReviewData)
        fun onLikeClick(reviewData: ReviewData, position: Int)
        fun onCommentLikeClick(reviewData: ReviewComment, position: Int)
        fun onDeleteLikeClick(reviewData: ReviewComment, position: Int)
    }
}