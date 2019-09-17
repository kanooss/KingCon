package com.junho.app.kingcon.Etc

import android.annotation.SuppressLint
import com.junho.app.kingcon.Item.UserData
import com.junho.app.kingcon.Item.UserPreferData
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.*
import java.lang.Exception
@SuppressLint("CheckResult")
//아이디 / 이름 / 사진 / 나이 / 제공자 / 평가수 / 위시수 / 리뷰수 / 평가리스트 / 위시리스트 / 리뷰리스트
object User: Serializable {
    var id: String = ""
    var name: String = ""
    var picture: Boolean = false
    var age: Long = 0
    var gender: Boolean = false
    var preferTag: HashMap<String, Int> = hashMapOf()
    var provider: String = ""
    var rating: Int = 0
    var wish: Int = 0
    var review: Int = 0
    var ratingList: ArrayList<String> = arrayListOf()
    var wishList: ArrayList<String> = arrayListOf()
    var reviewList: ArrayList<String> = arrayListOf()
    var mapData: HashMap<String, Any> = hashMapOf()
    var imageByte: ByteArray? = null

    fun setUser(data: UserData){
        id = data.id
        name = data.name
        picture = data.picture
        age = data.age
        gender = data.gender
        provider = data.provider
        rating = data.rating
        wish = data.wish
        review = data.review
        preferTag.clear()
        ratingList.clear()
        wishList.clear()
        reviewList.clear()
        preferTag.putAll(data.preferTag)
    }
    fun setUser(data: User){
        id = data.id
        name = data.name
        picture = data.picture
        age = data.age
        gender = data.gender
        provider = data.provider
        rating = data.rating
        wish = data.wish
        review = data.review
        preferTag.clear()
        ratingList.clear()
        wishList.clear()
        reviewList.clear()
        preferTag.putAll(data.preferTag)
        ratingList.addAll(data.ratingList)
        wishList.addAll(data.wishList)
        reviewList.addAll(data.reviewList)
    }
    fun setUserPreferTag(data: UserPreferData){
        preferTag.clear()
        preferTag.putAll(data.preferTag)
    }
    //유저 데이터 업데이트
    fun localUserDataUpdate(){
        Observable.just(FileOutputStream(StringData.userData()))
            .map { ObjectOutputStream(it) }
            .subscribeOn(Schedulers.io())
            .subscribe {
                it.writeObject(UserData(User))
                it.flush()
            }
//        val fos = FileOutputStream(StringData.userData())
//        val oos = ObjectOutputStream(fos)
//        oos.writeObject(UserData(User))
//        oos.flush()
    }
    //유저 데이터 가져오기
    fun localUserDataRead(file: File) {
        val fis = FileInputStream(file)
        val ois = ObjectInputStream(fis)
        try {
            setUser(ois.readObject() as UserData)
            ois.close()
        }catch (e: Exception){}
    }
    //유저 리스트 데이터 업데이트
    fun localUserListUpdate(){
        val file = File(StringData.userListData())
        val fos = FileOutputStream(file)
        val oos = ObjectOutputStream(fos)
        oos.writeObject(mapData)
        oos.flush()
    }
    //유저 데이터 가져오기
    fun localUserListRead(file: File){
        val fis = FileInputStream(file)
        val ois = ObjectInputStream(fis)
        mapData = ois.readObject() as HashMap<String, Any>
        ois.close()
    }

    fun dataReset(){
        wish = 0
        rating = 0
        review = 0
        ratingList.clear()
        reviewList.clear()
        wishList.clear()
        localUserDataUpdate()
    }
    //유저 로그아웃하면 데이터 초기화
    fun logOut() {
        id = ""
        name = ""
        picture = false
        age = 0
        gender = false
        preferTag = hashMapOf()
        provider = ""
        rating = 0
        wish = 0
        review = 0
        ratingList = arrayListOf()
        wishList = arrayListOf()
        reviewList = arrayListOf()
        mapData = hashMapOf()
        mapData[StringData.NOW_USER] = ""
        localUserListUpdate()
    }

    /**
     * User.mapData
     * NOW_USER -> 현재 접속된 유저 아이디
     * PICTURE_SIGNATURE -> 프로필 사진 시그니쳐
     */
    override fun toString() = "\n id : $id \n name : $name \n picture : $picture \n age : $age \n gender : $gender \n provider : $provider " +
            "\n preferTag : $preferTag \n rating : $rating \n ratingList: $ratingList \n wish : $wish \n wishList: $wishList " +
            "\n review : $review \n reviewList : $reviewList"

}