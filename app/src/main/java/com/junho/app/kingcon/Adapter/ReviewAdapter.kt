package com.junho.app.kingcon.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import com.bumptech.glide.Glide
import com.facebook.FacebookSdk
import com.junho.app.kingcon.Etc.CallBack.CallbackAdapter
import com.junho.app.kingcon.Etc.User
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.Item.ReviewData
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.adapter_review_list.view.*


class ReviewAdapter(var context: Context, private val reviewData: ArrayList<ReviewData>, private val callbackAdapter: CallbackAdapter.Review): RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    override fun getItemCount(): Int = reviewData.size
    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ReviewAdapter.ReviewViewHolder {
        val inflate = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mainView = inflate.inflate(R.layout.adapter_review_list, parent, false)
        return ReviewViewHolder(mainView)
    }
    override fun onBindViewHolder(holder: ReviewAdapter.ReviewViewHolder, position: Int) {
        holder.onBind(context, reviewData[position], callbackAdapter, position)
    }
    class ReviewViewHolder(val view: View): RecyclerView.ViewHolder(view){
        private var pictureFlag = true
        @SuppressLint("SetTextI18n")
        fun onBind(context: Context, reviewData: ReviewData, callbackAdapter: CallbackAdapter.Review, position: Int) {
            //프사
            if(pictureFlag) {
                Util.getPictureUri(context, view.ivUserPicture, reviewData.userData)
                pictureFlag = false
            }
            //이름
            view.tvName.text = reviewData.userData.name
            //나이 . 성별
            val gender =
                if(reviewData.userData.gender)
                    "남"
                else
                    "여"
            view.tvAgeGender.text = "${Util.getAge(reviewData.userData.age)/10 *10}대・$gender"
            //평가 점수
            view.tvRating.text = reviewData.point.toString()
            //위시
            if(reviewData.wish){

            }
            //등록 날짜
            view.tvDate.text = Util.regTime(reviewData.date)
            //옵션 클릭
            view.tvOption.setOnClickListener {
                val popup = PopupMenu(context, view.tvOption)//v는 클릭된 뷰를 의미
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
                    .into(view.btnCommentLike)
            else
                Glide.with(view)
                    .load(R.drawable.ic_like)
                    .into(view.btnCommentLike)
            //좋아요 클릭
            view.btnCommentLike.setOnClickListener { callbackAdapter.onLikeClick(reviewData, position) }
            view.tvLikeNum.setOnClickListener { callbackAdapter.onLikeClick(reviewData, position) }
            view.tvLikeNum.text = reviewData.like.toString() //좋아요 수
            //댓글창
            view.btnComment.setOnClickListener { callbackAdapter.onReviewDetail(reviewData, position) }
            view.btnComment.text = "댓글 ${reviewData.comment}" //댓글 수
            //리뷰 내용
            view.tvContent.setOnClickListener { callbackAdapter.onReviewDetail(reviewData, position) }
            view.tvContent.text = reviewData.content

        }
    }
}