package com.junho.app.kingcon.Item

import android.util.Log
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.google.gson.annotations.SerializedName
import com.junho.app.kingcon.Etc.StringData.PRODUCT_TABLE
import com.junho.app.kingcon.Etc.StringData.TAG_HIGHLIGHT
import com.junho.app.kingcon.Etc.User
import java.io.Serializable

/**
 * 제품 테이블 - 품종(hash) / 아이디(range)
 *
 * 제품 데이터 - 제품명 / 사진 / 판매사 / 태그 / 평점 / 총점 / 평가수 / 위시수 / 리뷰수 / 별점분포 / 남자 별점 분포 / 여자 별점 분포 / 남성평가 수 / 여성평가 수
 *              / 제품정보들 / 리뷰탑3 / 내 리뷰 / 내 평가 / 내 위시
 * 홈 리스트 - 제품명 / 사진 / 판매사 / 태그 / 평점 / 평가수 / 위시수 / 리뷰수 + 가격?, 판매 단위? (정렬 - 평가, 평점, 리뷰)
 * 제품 자세히 - 별점 분포도 / 제품정보들 / 리뷰 탑3 / 내 리뷰 / 내 평가 / 내 위시
 * 제품 정보 - 정보(HashMap)
 * 제품 별점 분포 - 남자 별점 분포 / 여자 별점 분포 / 남성평가 수 / 여성평가 수
 * 댓글 리스트 - 댓글리스트 (정렬 - 날짜, 평점, 좋아요)
 */
@DynamoDBTable(tableName = PRODUCT_TABLE)
data class ProductData (
    @SerializedName("type") @get:DynamoDBHashKey var type: String,
    @SerializedName("id") @get:DynamoDBRangeKey var id: String,
    @SerializedName("Name") @get:DynamoDBAttribute (attributeName = "Name") var name: String = "",
    @SerializedName("Picture") @get:DynamoDBAttribute (attributeName = "Picture") var picture: String = "",
    @SerializedName("Company") @get:DynamoDBAttribute (attributeName = "Company") var company: String = "",
    @SerializedName("Tag") @get:DynamoDBAttribute (attributeName = "Tag") var tag: ArrayList<String> = arrayListOf(),
    @SerializedName("Point") @get:DynamoDBAttribute (attributeName = "Point") var point: Float = 0f,
    @SerializedName("TotalPoint") @get:DynamoDBAttribute (attributeName = "TotalPoint") var totalPoint: Float = 0f,
    @SerializedName("Rating") @get:DynamoDBAttribute (attributeName = "Rating") var rating: Int = 0,
    @SerializedName("Wish") @get:DynamoDBAttribute (attributeName = "Wish") var wish: Int = 0,
    @SerializedName("Review") @get:DynamoDBAttribute (attributeName = "Review") var review: Int = 0,
    @SerializedName("Graph") @get:DynamoDBAttribute (attributeName = "Graph") var graph: ArrayList<Int> = arrayListOf(0,0,0,0,0,0,0,0,0,0),
    @SerializedName("MaleGraph") @get:DynamoDBAttribute (attributeName = "MaleGraph") var maleGraph: ArrayList<Int> = arrayListOf(0,0,0,0,0,0,0,0,0,0),
    @SerializedName("FemaleGraph") @get:DynamoDBAttribute (attributeName = "FemaleGraph") var femaleGraph: ArrayList<Int> = arrayListOf(0,0,0,0,0,0,0,0,0,0),
    @SerializedName("Male") @get:DynamoDBAttribute (attributeName = "Male") var male: Int = 0,
    @SerializedName("Female") @get:DynamoDBAttribute (attributeName = "Female") var female: Int = 0,
    @SerializedName("Info") @get:DynamoDBAttribute (attributeName = "Info") var info: HashMap<String, String> = HashMap(),
    @DynamoDBIgnore var malePoint: Float = 0f,
    @DynamoDBIgnore var femalePoint: Float = 0f,
    @DynamoDBIgnore var bestReview: ArrayList<ReviewData> = arrayListOf(),
    @DynamoDBIgnore var reviews: ArrayList<ReviewData> = arrayListOf(),
    @DynamoDBIgnore var myReview: ReviewData = ReviewData()): Serializable{

    //내 평가 가져왔는지 확인
    fun myReviewIsNull() = !(myReview.content.isNotBlank() || myReview.point != 0.0f || myReview.wish)

    //AttributeValue -> 태그어레이로
    fun setTagData(l: MutableList<AttributeValue>) {
        l.forEach { tag.add(it.s.toString()) }
    }
    //태그 검색시 검색된 태그 하이라이트
    fun setTagHighlight(tag: ArrayList<String>){
        tag.forEach {
            for(i in 0 until this.tag.size)
                if(this.tag[i] == it){
                    this.tag[i] += TAG_HIGHLIGHT
                }
        }
    }
    //?
    fun updateReview(reviewData: ReviewData): Boolean{
        if(User.id == reviewData.id)
            myReview = reviewData
        for (i in 0 until bestReview.size) {
            if(bestReview[i].userId == reviewData.userId) {
                bestReview[i] = reviewData
            }
        }
        for (i in 0 until reviews.size) {
            if(reviews[i].userId == reviewData.userId) {
                reviews[i] = reviewData
                return true
            }
        }
        return false
    }
    //베댓이에 해당리뷰 포함돼있는지 확인 후 인덱스 리턴
    fun checkInBestReview(reviewData: ReviewData): Int{
        for (i in 0 until bestReview.size){
            if(bestReview[i].userId == reviewData.userId)
                return i
        }
        return -1
    }
    fun checkInReviews(reviewData: ReviewData): Int{
        for (i in 0 until reviews.size){
            if(reviews[i].userId == reviewData.userId)
                return i
        }
        return -1
    }
    fun graphToString(): String =
        "graph: $graph, rating: $rating, point: $point \n" +
                "maleGraph: $maleGraph, rating: $male, point: $malePoint \n" +
                "femeGraph: $femaleGraph, rating: $female, point: $femalePoint \n"

    fun graphInit() {
        graph = arrayListOf(0,0,0,0,0,0,0,0,0,0)
        maleGraph = arrayListOf(0,0,0,0,0,0,0,0,0,0)
        femaleGraph = arrayListOf(0,0,0,0,0,0,0,0,0,0)
        rating = 0
        male = 0
        female = 0
        point = +0F
        malePoint = 0f
        femalePoint = 0f
    }

    constructor():this("","")
    constructor(homeList: ProductHomeList): this(homeList.type, homeList.id,homeList.name, homeList.picture,homeList.company, homeList.tag,
        homeList.point, 0f, homeList.rating, homeList.wish, homeList.review)
    constructor(detailData: ProductDetailData): this(detailData.type, detailData.id){
        graph.clear()
        graph.addAll(detailData.graph)
        info.clear()
        info.putAll(detailData.info)
        bestReview.clear()
        bestReview.addAll(detailData.topReview)
        myReview = detailData.myReview
    }
    constructor(infoData: ProductInfoData): this(infoData.type, infoData.id){
        info.clear()
        info.putAll(infoData.info)
    }
    constructor(graphData: ProductGraphData): this(graphData.type, graphData.id){
        graph.clear()
        graph.addAll(graphData.graph)
        maleGraph.clear()
        maleGraph.addAll(graphData.maleGraph)
        femaleGraph.clear()
        femaleGraph.addAll(graphData.femaleGraph)
        male = graphData.male
        female = graphData.female
    }
    constructor(reviewData: ProductReviewData): this(reviewData.type, reviewData.id){
        bestReview.clear()
        bestReview.addAll(reviewData.review)
    }
}
@DynamoDBTable(tableName = PRODUCT_TABLE)
data class ProductHomeList(
    @get:DynamoDBHashKey var type: String,
    @get:DynamoDBRangeKey var id: String,
    @get:DynamoDBAttribute (attributeName = "Name") var name: String = "",
    @get:DynamoDBAttribute (attributeName = "Picture") var picture: String = "",
    @get:DynamoDBAttribute (attributeName = "Company") var company: String = "",
    @get:DynamoDBAttribute (attributeName = "Tag") var tag: ArrayList<String> = arrayListOf(),
    @get:DynamoDBAttribute (attributeName = "Point") var point: Float = 0f,
    @get:DynamoDBAttribute (attributeName = "Rating") var rating: Int = 0,
    @get:DynamoDBAttribute (attributeName = "Wish") var wish: Int = 0,
    @get:DynamoDBAttribute (attributeName = "Review") var review: Int = 0): Serializable{
}
@DynamoDBTable(tableName = PRODUCT_TABLE)
data class ProductDetailData(
    @get:DynamoDBHashKey var type: String,
    @get:DynamoDBRangeKey var id: String,
    @get:DynamoDBAttribute(attributeName = "Graph") var graph: ArrayList<Int> = arrayListOf(0,0,0,0,0,0,0,0,0,0),
    @get:DynamoDBAttribute(attributeName = "Info") var info: HashMap<String, String>,
    @get:DynamoDBIgnore var topReview: ArrayList<ReviewData>,
    @get:DynamoDBIgnore var myReview: ReviewData): Serializable{}
data class ProductInfoData(var type: String,
                           var id: String,
                           var info: HashMap<String, String>): Serializable{}
data class ProductGraphData(var type: String,
                            var id: String,
                            var graph: ArrayList<Int> = arrayListOf(0,0,0,0,0,0,0,0,0,0),
                            var maleGraph: ArrayList<Int> = arrayListOf(0,0,0,0,0,0,0,0,0,0),
                            var femaleGraph: ArrayList<Int> = arrayListOf(0,0,0,0,0,0,0,0,0,0),
                            var male: Int,
                            var female: Int): Serializable{}
data class ProductReviewData(var type: String,
                             var id: String,
                             var review: ArrayList<ReviewData>): Serializable{}