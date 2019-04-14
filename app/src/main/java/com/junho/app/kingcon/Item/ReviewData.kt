package com.junho.app.kingcon.Item

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*
import com.junho.app.kingcon.Etc.StringData.DB_REVIEW_COMMENT
import com.junho.app.kingcon.Etc.StringData.DB_REVIEW_CONTENT
import com.junho.app.kingcon.Etc.StringData.DB_REVIEW_DATE
import com.junho.app.kingcon.Etc.StringData.DB_REVIEW_ID
import com.junho.app.kingcon.Etc.StringData.DB_REVIEW_LIKE
import com.junho.app.kingcon.Etc.StringData.DB_REVIEW_POINT
import com.junho.app.kingcon.Etc.StringData.DB_REVIEW_USER_ID
import com.junho.app.kingcon.Etc.StringData.DB_REVIEW_WISH
import com.junho.app.kingcon.Etc.StringData.REVIEW_TABLE
import java.io.Serializable

/**
 *리뷰 테이블 (정렬 - 날짜, 평점, 좋아요)
 *
 * 리뷰 데이터 - Id(hash) / 날짜(range) / 유저 아이디 / 평점 / 리뷰 내용 / 좋아요 / 리뷰 댓글
 * 리뷰 좋아요 한 사람 리스트 -
 * 리뷰 댓글 리스트 - 유저아이디 / 댓글내용 / 날짜
 * */
@DynamoDBTable(tableName = REVIEW_TABLE)
data class ReviewData(@get:DynamoDBHashKey @get:DynamoDBAttribute(attributeName =  DB_REVIEW_ID) var id: String,
                      @get:DynamoDBAttribute(attributeName = DB_REVIEW_DATE) var date: Long,
                      @get:DynamoDBRangeKey @get:DynamoDBAttribute(attributeName = DB_REVIEW_USER_ID) var userId: String,
                      @get:DynamoDBAttribute(attributeName = DB_REVIEW_POINT) var point: Float,
                      @get:DynamoDBAttribute(attributeName = DB_REVIEW_WISH) @get:DynamoDBNativeBoolean var wish: Boolean,
                      @get:DynamoDBAttribute(attributeName = DB_REVIEW_CONTENT) var content: String,
                      @get:DynamoDBAttribute(attributeName = DB_REVIEW_LIKE) var like: Int,
                      @get:DynamoDBAttribute(attributeName = DB_REVIEW_COMMENT) var comment: Int,
                      @get:DynamoDBIgnore var userData: UserProfileData,
                      @get:DynamoDBIgnore var reviewLike: Boolean = false,
                      @get:DynamoDBIgnore var reviewComment: ArrayList<ReviewComment> = arrayListOf(),
                      @get:DynamoDBIgnore var imageByte: ByteArray? = null): Serializable{
    constructor():this("", 0L, "", 0f, false, "", 0, 0, UserProfileData())
    //리뷰 삭제
    fun removeReview(){
        date = 0L
        content = ""
        like = 0
        comment = 0
        reviewComment = arrayListOf()
    }
    //댓글 추가
    fun createComment(reviewComment: ReviewComment) {
        this.reviewComment.add(reviewComment)
        comment++
    }
    //댓글 삭제시
    fun removeComment(reviewData: ReviewComment) {
        reviewComment.remove(reviewData)
        comment--
    }
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + point.hashCode()
        result = 31 * result + wish.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + like
        result = 31 * result + comment
        result = 31 * result + (imageByte?.contentHashCode() ?: 0)
        result = 31 * result + userData.hashCode()
        result = 31 * result + reviewLike.hashCode()
        result = 31 * result + reviewComment.hashCode()
        return result
    }

}
//ReviewData - date  ==  reviewComment - reviewId
data class ReviewComment(var userId: String, var comment: String, var date: Long, var like: Int = 0,
                         var userData: UserProfileData = UserProfileData(), var imageByte: ByteArray? = null, var isLike: Boolean = false ): Serializable{

}