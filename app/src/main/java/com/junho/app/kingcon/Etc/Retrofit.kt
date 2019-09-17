package com.junho.app.kingcon.Etc

import com.google.gson.annotations.SerializedName
import com.junho.app.kingcon.Etc.StringData.PRODUCT_TABLE
import com.junho.app.kingcon.Etc.StringData.PRODUCT_TYPE
import com.junho.app.kingcon.Etc.StringData.REVIEW_TABLE
import com.junho.app.kingcon.Etc.StringData.REVIEW_TYPE
import com.junho.app.kingcon.Etc.StringData.USER_TABLE
import com.junho.app.kingcon.Etc.StringData.USER_TYPE
import com.junho.app.kingcon.Item.*
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

object Retrofit {
    val url_ = "https://rc5v6ky35k.execute-api.ap-northeast-2.amazonaws.com/"

    private val httpLoggingInterceptor = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    private val okHttpClient: OkHttpClient =
        OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    val retrofit: SearchService =
        Retrofit.Builder().baseUrl(url_)//movieUri
//        Retrofit.Builder().baseUrl(naver_url_)//movieUri
            .client(okHttpClient)//httpClient연결을 통해 log 확인
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//RxJava2를 사용하도록 Factory생성
            .addConverterFactory(GsonConverterFactory.create())//Gson을 쓸 수 있도록 Factory생성
//            .addConverterFactory(SimpleXmlConverterFactory.create())//Gson을 쓸 수 있도록 Factory생성
            .build().create(SearchService::class.java)//MovieService Interface사용
}


interface SearchService {
    //GET 방식으로 url을 맵핑할 수 있음.
    /**
     * 첫번째 자리 - 스테이지
     * 두번째 자리 - 검색 퀄;
     *
     * Header를 통해서 각종 값을 전송할 수 있음
     * Path를 통해서 구체적인 API URI로 이동할 수 있음
     * Query를 통해서 웹에 쿼리를
     */
    @GET("default/{type}")
    fun getProduct(
        @Header("x-api-key") api_key: String = "53Og9qtVhj7tbcEnYvlv02CYrgAB3ZC55x8B8YmX",
        @Path("type") type: String = PRODUCT_TYPE,
        @Query("tableName") tableName: String = PRODUCT_TABLE,
        @Query("itemType") itemType: String,
        @Query("productId") id: String,
        @Query("section") section: String
    ): Observable<Product>

    @GET("default/{type}")
    fun getReview(
        @Header("x-api-key") api_key: String = "53Og9qtVhj7tbcEnYvlv02CYrgAB3ZC55x8B8YmX",
        @Path("type") type: String = REVIEW_TYPE,
        @Query("tableName") tableName: String = REVIEW_TABLE,
        @Query("productId") productId: String,
        @Query("section") section: String
    ): Observable<Review>

    @GET("default/{type}")
    fun getReview(
        @Header("x-api-key") api_key: String = "53Og9qtVhj7tbcEnYvlv02CYrgAB3ZC55x8B8YmX",
        @Path("type") type: String = REVIEW_TYPE,
        @Query("tableName") tableName: String = REVIEW_TABLE,
        @Query("productId") productId: String,
        @Query("userId")  userId: String,
        @Query("section") section: String
    ): Observable<Review>

    @GET("default/{type}")
    fun getReview(
        @Header("x-api-key") api_key: String = "53Og9qtVhj7tbcEnYvlv02CYrgAB3ZC55x8B8YmX",
        @Path("type") type: String = REVIEW_TYPE,
        @Query("productId") productId: String,
        @Query("userId")  userId: String,
        @Query("searchUser")  searchUser: String,
        @Query("lastEvaluatedKey")  lastEvaluatedKey: Map<String, String>?,
        @Query("section") section: String
    ): Observable<Review>

    @GET("default/{type}")
    fun getReviewMore(
        @Header("x-api-key") api_key: String = "53Og9qtVhj7tbcEnYvlv02CYrgAB3ZC55x8B8YmX",
        @Path("type") type: String = REVIEW_TYPE,
        @Query("productId") productId: String,
        @Query("sort") userId: String,
        @Query("lastEvaluatedKey")  lastEvaluatedKey: Map<String, String>?,
        @Query("section") section: String
    ): Observable<Review>

    @GET("default/{type}")
    fun getCommentUser(
        @Header("x-api-key") api_key: String = "53Og9qtVhj7tbcEnYvlv02CYrgAB3ZC55x8B8YmX",
        @Path("type") type: String = REVIEW_TYPE,
        @Query("productId") productId: String,
        @Query("userId") userId: String,
        @Query("likeUserId") likeUserId: String,
        @Query("projectList") project: String,
        @Query("section") section: String
    ): Observable<CommentUser_>

    @GET("default/{type}")
    fun getReviewComment(
        @Header("x-api-key") api_key: String = "53Og9qtVhj7tbcEnYvlv02CYrgAB3ZC55x8B8YmX",
        @Path("type") type: String = REVIEW_TYPE,
        @Query("tableName") tableName: String = REVIEW_TABLE,
        @Query("productId") productId: String,
        @Query("userId") userId: String,
        @Query("likeUserId") likeUserId: String,
        @Query("projectList") project: String,
        @Query("section") section: String
    ): Observable<Comment_>

    @GET("default/{type}")
    fun getUser(
        @Header("x-api-key") api_key: String = "53Og9qtVhj7tbcEnYvlv02CYrgAB3ZC55x8B8YmX",
        @Path("type") type: String = USER_TYPE,
        @Query("tableName") tableName: String = USER_TABLE,
        @Query("userIds") userIds: String,
        @Query("section") section: String
    ): Observable<User_>
}


class Product(
    @SerializedName("Count") var count: Int,
    @SerializedName("ScannedCount") var scannedCount: Int,
    @SerializedName("Items") var items: Array<ProductData>
)

class Review(
    @SerializedName("Count") var count: Int,
    @SerializedName("ScannedCount") var scannedCount: Int,
    @SerializedName("Items") var items: Array<ReviewData>,
    @SerializedName("LastEvaluatedKey") var lastEvaluatedKey: Map<String,String>
)

class Comment_(
    @SerializedName("Count") var count: Int,
    @SerializedName("ScannedCount") var scannedCount: Int,
    @SerializedName("Items") var items: Array<Comment>
)

data class User_(
    @SerializedName("Responses") var responses: UserDataObject
//    @SerializedName("UnprocessedKeys") var scannedCount: String,
)

data class CommentUser_(
    @SerializedName("Count") var count: Int,
    @SerializedName("ScannedCount") var scannedCount: Int,
    @SerializedName("Items") var item: Array<ReviewComment>
//    @SerializedName("UnprocessedKeys") var scannedCount: String,
)

data class UserDataObject(@SerializedName("kingcon-mobilehub-140270308-User") val array: Array<UserData>)
data class UserCommentLikeObject(@SerializedName("CommentData") val array: Array<ReviewComment>)
data class Comment(@SerializedName("CommentData") var comment: ArrayList<ReviewComment>)