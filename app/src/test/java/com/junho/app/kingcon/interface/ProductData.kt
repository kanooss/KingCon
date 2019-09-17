package com.junho.app.kingcon.`interface`

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.http.*

interface ProductData {
    //GET 방식으로 url을 맵핑할 수 있음.
    @GET("default/{type}")
    fun getSearch(
        //Header를 통해서 각종 값을 전송할 수 있음
        //Path를 통해서 구체적인 API URI로 이동할 수 있음
        //Query를 통해서 웹에 쿼리를
        @Path("type") type: String,
        @Query("TableName") query: String
    ): Observable<Product>

    @GET("default/{type}")
    @Headers("Accept: application/json")
    fun getSearch(
        @Header("x-api-key") api_key: String,
        @Path("type") type: String,
        @Query("TableName") tableName: String,
        @Query("testId") id: String
    ): Observable<Product>

    @GET("search/{type}")
    //Header를 통해서 각종 값을 전송할 수 있음
    //Path를 통해서 구체적인 API URI로 이동할 수 있음
    //Query를 통해서 웹에 쿼리를
    fun getNaver(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientPw: String,
        @Path("type") type: String,
        @Query("query") query: String
    ): Observable<Movie>
}

class Movie(
    @SerializedName("lastBuildDate") var lastBuildDate: String,
    @SerializedName("total") var total: Int,
    @SerializedName("start") var start: Int,
    @SerializedName("display") var display: Int,
    @SerializedName("items") var items: Array<Items>
) {
    data class Items(
        @SerializedName("title")
        val title: String,
        @SerializedName("link")
        val link: String,
        @SerializedName("image")
        val image: String,
        @SerializedName("subtitle")
        val subtitle: String,
        @SerializedName("pubdate")
        val pubDate: String,
        @SerializedName("director")
        val director: String,
        @SerializedName("actor")
        val actor: String,
        @SerializedName("userRating")
        val userRating: Double
    )
}

class Product(
    @SerializedName("Count") var count: Int,
    @SerializedName("ScannedCount") var scannedCount: Int,
    @SerializedName("Items") var items: Array<Items>
) {
    data class Items(
        @SerializedName("type") var type: String,
        @SerializedName("id") var id: String,
        @SerializedName("Name") var Name: String,
        @SerializedName("Picture") var Picture: String,
        @SerializedName("Company") var Company: String,
        @SerializedName("Tag") var Tag: ArrayList<String>,
        @SerializedName("Point") var Point: Float,
        @SerializedName("TotalPoint") var TotalPoint: Float,
        @SerializedName("Rating") var Rating: Int,
        @SerializedName("Wish") var Wish: Int,
        @SerializedName("Review") var Review: Int,
        @SerializedName("Graph") var Graph: ArrayList<Int>,
        @SerializedName("MaleGraph") var MaleGraph: ArrayList<Int>,
        @SerializedName("FemaleGraph") var FemaleGraph: ArrayList<Int>,
        @SerializedName("Male") var Male: Int,
        @SerializedName("Female") var Female: Int,
        @SerializedName("Info") var Info: Map<String, String>
    ) {
        data class Infos(
            @SerializedName("Coun") var Coun: String,
            @SerializedName("Ingr") var Ingr: String,
            @SerializedName("Char") var Char: String
        )
    }
}