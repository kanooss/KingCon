package com.junho.app.kingcon.Item

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*
import com.junho.app.kingcon.Etc.User
import java.io.Serializable

/**
 * 유저 테이블 - 아이디(hash)
 * UserData(유저 데이터) - 이름 / 사진 / 나이 / 성별 / 제공자 / 선호태그 / 평가수 / 위시수 / 리뷰수 / 평가리스트 / 위시리스트 / 리뷰리스트
 *      사진은 있으면 true 없으면 false / 성별 false - 남자 true - 여자
 * UserProfileData(유저 프로필 데이터) - 이름 / 사진 / 나이 / 성별
 * UserProviderData(유저 공급자 데이터) - 공급자
 * UserPreferData(유저 태그 호도) - 태그 선호 점수
 * UserActData(유저 활동 데이터) - 평가수 / 위시수 / 리뷰수
 * UserRatingData(유저 평가 리스트 데이터) - 평가리스트
 * UserWishData(유저 위시 리스트 데이터) -  위시리스트
 * UserReviewData(유저 리뷰 리스트 데이터) - 리뷰리스트
 */
@DynamoDBTable(tableName = "kingcon-mobilehub-140270308-User")
data class UserData(@get:DynamoDBHashKey(attributeName = "Id") var id: String,
                    @get:DynamoDBAttribute(attributeName = "Name")  var name: String,
                    @get:DynamoDBNativeBoolean var picture: Boolean = false,
                    @get:DynamoDBAttribute(attributeName = "Age") var age: Long = 0,
                    @get:DynamoDBNativeBoolean var gender: Boolean = false,
                    @get:DynamoDBAttribute(attributeName = "Provider")  var provider: String = "",
                    @get:DynamoDBAttribute(attributeName = "PreferTag") var preferTag: HashMap<String, Int> = hashMapOf(),
                    @get:DynamoDBAttribute(attributeName = "Rating") var rating: Int = 0,
                    @get:DynamoDBAttribute(attributeName = "Wish") var wish: Int = 0,
                    @get:DynamoDBAttribute(attributeName = "Review") var review: Int = 0,
                    @get:DynamoDBIgnore var imageByte: ByteArray? = null): Serializable{

    constructor(user: User):this(user.id,user.name,user.picture,user.age,user.gender,user.provider,user.preferTag,user.rating,user.wish,user.review)
    constructor(user: UserProfileData):this(user.id, user.name, user.picture, user.age, user.gender, user.provider, user.preferTag)
    constructor(id:String, name: String, provider: String):this(id, name, false, 0, false, provider, hashMapOf())
    constructor(id: String, name: String, provider: String, age: Long, gender: Boolean):this(id, name, false, age, gender, provider, hashMapOf())
    constructor():this("","",false,0,false,"", hashMapOf(), 0,0,0)

    //유저 기본프로필 세팅
    fun setProfile(profile: UserProfileData){
        id = profile.id
        name = profile.name
        picture = profile.picture
        age = profile.age
        gender = profile.gender
    }
    fun setProvider(providerData: UserProviderData){
        provider = providerData.provider
    }
    fun setPrefer(prefer: UserPreferData){
        preferTag.clear()
        preferTag.putAll(prefer.preferTag)
    }
    //활동 세팅
    fun setAct(act: UserActData){
        rating = act.rating
        wish = act.wish
        review = act.review
    }
}
//유저 기본 데이터
@DynamoDBTable(tableName = "kingcon-mobilehub-140270308-User")
data class UserProfileData(@get:DynamoDBHashKey(attributeName = "Id") var id: String = "",
                           @get:DynamoDBAttribute(attributeName = "Name")  var name: String = "",
                           @get:DynamoDBNativeBoolean var picture: Boolean = false,
                           @get:DynamoDBAttribute(attributeName = "Age")  var age: Long = 0,
                           @get:DynamoDBNativeBoolean var gender: Boolean = false,
                           @get:DynamoDBAttribute(attributeName = "Provider")  var provider: String = "",
                           @get:DynamoDBAttribute(attributeName = "PreferTag")  var preferTag: HashMap<String, Int> = hashMapOf()): Serializable{
    constructor(user: User):this(user.id,user.name,user.picture,user.age,user.gender,user.provider)
    constructor():this("", "", false, 0, false, "")
}
//유저 선호 태그
@DynamoDBTable(tableName = "kingcon-mobilehub-140270308-User")
data class UserPreferData(@get:DynamoDBHashKey(attributeName = "Id") var id: String = "",
                          @get:DynamoDBAttribute(attributeName = "PreferTag") var preferTag: HashMap<String, Int>): Serializable{
    constructor():this("", hashMapOf())
}
//유저 자격 공급자
@DynamoDBTable(tableName = "kingcon-mobilehub-140270308-User")
data class UserProviderData(@get:DynamoDBHashKey(attributeName = "Id") var id: String = "",
                            @get:DynamoDBAttribute(attributeName = "Provider") var provider: String = ""): Serializable{
    constructor():this("", "")
}
@DynamoDBTable(tableName = "kingcon-mobilehub-140270308-User")
data class UserActData(@get:DynamoDBHashKey(attributeName = "Id") var id: String,
                       @get:DynamoDBAttribute(attributeName = "Rating") var rating: Int,
                       @get:DynamoDBAttribute(attributeName = "Wish") var wish: Int,
                       @get:DynamoDBAttribute(attributeName = "Review") var review: Int): Serializable{
    constructor():this("", 0, 0, 0)
}
@DynamoDBTable(tableName = "kingcon-mobilehub-140270308-User")
data class UserRatingData(@get:DynamoDBHashKey(attributeName = "Id") var id: String,
                          @get:DynamoDBAttribute(attributeName = "RatingList") var ratingList: ArrayList<String>): Serializable{
    constructor():this("", arrayListOf<String>())
}
@DynamoDBTable(tableName = "kingcon-mobilehub-140270308-User")
data class UserWishData(@get:DynamoDBHashKey(attributeName = "Id") var id: String,
                        @get:DynamoDBAttribute(attributeName = "WishList") var wishList: ArrayList<String>): Serializable{
    constructor():this("", arrayListOf<String>())
}
@DynamoDBTable(tableName = "kingcon-mobilehub-140270308-User")
data class UserReviewData(@get:DynamoDBHashKey(attributeName = "Id") var id: String,
                          @get:DynamoDBAttribute(attributeName = "ReviewList") var reviewList: ArrayList<String>): Serializable{
    constructor():this("", arrayListOf<String>())
}