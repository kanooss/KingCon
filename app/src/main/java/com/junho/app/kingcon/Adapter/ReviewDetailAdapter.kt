package com.junho.app.kingcon.Adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.bumptech.glide.Glide
import com.junho.app.kingcon.Etc.CallBack.CallbackAdapter
import com.junho.app.kingcon.Etc.MyLog
import com.junho.app.kingcon.Etc.StringData
import com.junho.app.kingcon.Etc.User
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.Item.ReviewComment
import com.junho.app.kingcon.Item.ReviewData
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.adapter_review_comment_list.view.*
import kotlinx.android.synthetic.main.adapter_review_detail_header.view.*


class ReviewDetailAdapter(
    var context: Context, private val productData: ProductData, private val reviewData: ReviewData,
    private val callbackAdapter: CallbackAdapter.ReviewDetail): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemId(position: Int): Long {
        return if(position > 0)
            reviewData.reviewComment[position-1].date
        else
            productData.myReview.date
    }

    override fun getItemCount(): Int = reviewData.reviewComment.size + 1
    override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
        val inflate = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        return if(type == StringData.HEADER_TYPE) {
            val mainView =inflate.inflate(R.layout.adapter_review_detail_header, parent, false)
            ReviewViewHolderHeader(mainView)
        } else {
            val mainView = inflate.inflate(R.layout.adapter_review_comment_list, parent, false)
            ReviewViewHolder(mainView)
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if(position == 0) StringData.HEADER_TYPE
        else StringData.ITEM_TYPE
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int){
        if(holder is ReviewViewHolder)
            holder.onBind(context, reviewData.reviewComment[position-1], callbackAdapter, position)
        else if(holder is ReviewViewHolderHeader)
            holder.onBind(context, productData, reviewData, callbackAdapter, position)
    }
    /** Item Type **/
    class ReviewViewHolder(val view: View): RecyclerView.ViewHolder(view){
        @SuppressLint("SetTextI18n")
        fun onBind(context: Context, comment: ReviewComment, callbackAdapter: CallbackAdapter.ReviewDetail, position: Int) {
            //삭제 버튼 -> 내 댓글일경우 보이게
            if(comment.userId == User.id) {
                view.tvOption.visibility = View.VISIBLE
            }else
                view.tvOption.visibility = View.GONE
            //삭제 클릭
            view.tvOption.setOnClickListener {
                callbackAdapter.onDeleteLikeClick(comment, position)
            }
            //좋아요 클릭
            view.btnCommentLike.setOnClickListener { callbackAdapter.onCommentLikeClick(comment, position) }
            view.tvLikeNum.setOnClickListener { view.btnCommentLike.performClick()}
            Glide.with(view)
                .load(if(comment.isLike) R.drawable.ic_like_ok else R.drawable.ic_like)
                .into(view.btnCommentLike)
            view.tvLikeNum.text = comment.like.toString()   //좋아요 수
            view.tvDate.text = Util.regTime(comment.date)   //입력 날짜
            view.tvName.text = comment.userData.name        //유저 이름
            view.tvContent.text = comment.comment           //댓글 내용
            Util.getPictureUri(context, view.ivUserPicture, comment.userData) //유저 사진
        }
    }
    /** Header Type **/
    class ReviewViewHolderHeader(val view: View): RecyclerView.ViewHolder(view){
        @SuppressLint("SetTextI18n")
        fun onBind(context: Context, productData: ProductData, reviewData: ReviewData, callbackAdapter: CallbackAdapter.ReviewDetail, position: Int){
            //제품 정보
            view.tvProductName.text = productData.name
            view.tvProductCompany.text = productData.company
            //프사
            Util.getPictureUri(context, view.ivReviewUserPicture, reviewData.userData)
            //이름
            view.tvReviewUserName.text = reviewData.userData.name
            //나이 . 성별
            val gender =
                if(reviewData.userData.gender)
                    "남"
                else
                    "여"
            view.tvReviewUserAge.text = "${Util.getAge(reviewData.userData.age)/10 *10}대・$gender"
            //평가 점수
            view.tvReviewUserRatingNum.text = reviewData.point.toString()
            //위시
            if(reviewData.wish){

            }
            //댓글 수
            view.tvReviewCommentNum.text = "댓글 ${reviewData.comment}"
            //좋아요 수
            view.tvReviewLikeNum.text = reviewData.like.toString()
            //리뷰 내용
            view.tvReviewComment.text = reviewData.content
            //등록 날짜
            view.tvReviewDate.text = Util.regTime(reviewData.date)
            //옵션 클릭
            view.ivReviewOption.setOnClickListener {
                val popup = PopupMenu(context, view.ivReviewOption)//v는 클릭된 뷰
                val id =
                    if(reviewData.userId == User.id)
                        R.menu.popup_my_menu
                    else
                        R.menu.popup_menu
                MenuInflater(context).inflate(id, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.m1 -> callbackAdapter.optionReviewDelete(reviewData)
                        R.id.m2 -> callbackAdapter.optionReviewCustom(reviewData)
                        R.id.m3 -> callbackAdapter.optionDeclaration()
                        else -> {
                        }
                    }
                    false
                }
                popup.show()//Popup Menu 보이기
            }
            if(reviewData.reviewLike)
                Glide.with(view)
                    .load(R.drawable.ic_like_ok)
                    .into(view.ivReviewBtnLike)
            else
                Glide.with(view)
                    .load(R.drawable.ic_like)
                    .into(view.ivReviewBtnLike)
            //좋아요 클릭
            view.ivReviewBtnLike.setOnClickListener { callbackAdapter.onLikeClick(reviewData, position) }
            view.tvReviewLikeNum.setOnClickListener { callbackAdapter.onLikeClick(reviewData, position) }
            val a = View.OnClickListener {
            }
        }
    }
}
