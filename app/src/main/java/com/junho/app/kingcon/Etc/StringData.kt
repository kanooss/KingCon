package com.junho.app.kingcon.Etc

object StringData {

    //테이블
    const val USER_TABLE = "kingcon-mobilehub-140270308-User"
    const val APPDATA_TABLE = "AppData"
    const val VERSION_TABLE = "Version"
    const val PRODUCT_TABLE = "Product"
    const val PRODUCT_INDEX = "type-index"
    const val REVIEW_TABLE = "Review"
    const val REVIEW_INDEX_LIKE = "Id-Like-index"
    const val REVIEW_INDEX_DATE = "Id-Date-index"
    const val REVIEW_INDEX_POINT = "Id-Point-index"
    //retrofi type
    const val PRODUCT_API_KEY = "53Og9qtVhj7tbcEnYvlv02CYrgAB3ZC55x8B8YmX"
    const val USER_API_KEY = "Qp19GTRwQf62zg83JxPOh8hUcJGhRIfM6WvNWoHg"
    const val REVIEW_API_KEY = "53Og9qtVhj7tbcEnYvlv02CYrgAB3ZC55x8B8YmX"
    const val PRODUCT_TYPE = "kingCong"
    const val USER_TYPE = "kingCon_User"
    const val REVIEW_TYPE = "kingCon_Review"
    //제품 타입
    const val CONDOM_TYPE = "Condom"
    const val GEL_TYPE = "Gel"

    //제품 아이디 범위
    const val CONDOM_MAX_NUM = 4
    //제품 초기 가져올 개수
    const val PRODUCT_INIT_NUM = 4
    //리뷰 리드 수
    const val REVIEW_READ_NUM = 8

    fun AWS_S3_PRODUTC_PICTURE(path: String) =
        "https://s3.ap-northeast-2.amazonaws.com/kingcon-userfiles-mobilehub-140270308/product/$path.jpg"
    fun AWS_S3_USER_PICTURE(path: String) =
        "https://s3.ap-northeast-2.amazonaws.com/kingcon-userfiles-mobilehub-140270308/user/$path.jpg"

    //api section
    ////product
    const val SECTION_PRODUCT_SERCH= "section_product_serch"
    const val SECTION_PRODUCT_INFO= "section_product_info"
    const val SECTION_PRODUCT_GRAPH= "section_product_graph"
    ////review
    const val SECTION_MY_REVIEW = "section_my_review"
    const val SECTION_REVIEW_LIKE = "section_review_like"
    const val SECTION_REVIEW_BEST = "section_review_best"
    const val SECTION_REVIEW_COMMENT = "section_review_comment"
    const val SECTION_REVIEW_COMMENT_LIKE = "section_review_comment_like"
    const val SECTION_REVIEW_MORE = "section_review_more"
    //////review like section
    const val MY_REVIEW = "my_review"
    const val BEST_REVIEW = "best_review"
    const val MORE_REVIEW = "more_review"
    ////user
    const val SECTION_USER_DATA = "section_user_data"
    //디비 항목 이름
    //제품 테이블
    const val DB_TYPE = "type"
    const val DB_ID = "id"
    const val DB_COMPANY = "Company"
    const val DB_FEMALE = "Female"
    const val DB_MALE = "Male"
    const val DB_FEMALE_GRAPH = "FemaleGraph"
    const val DB_MALEGRAPH = "MaleGraph"
    const val DB_GRAPH = "Graph"
    const val DB_INFO = "Info"
    const val DB_NAME = "Name"
    const val DB_PICTURE = "Picture"
    const val DB_POINT = "Point"
    const val DB_RATING = "Rating"
    const val DB_REVIEW = "Review"
    const val DB_WISH = "Wish"
    const val DB_TAG = "Tag"
    const val DB_TOTALPOINT = "TotalPoint"
    //유저 테이블
    const val DB_USER_ID = "Id"
    const val DB_USER_AGE = "Age"
    const val DB_USER_GENDER = "gender"
    const val DB_USER_NAME = "Name"
    const val DB_USER_PICTURE = "picture"
    const val DB_USER_PREFERTAG = "PreferTag"
    const val DB_USER_PROVIDER = "Provider"
    const val DB_USER_RATING = "Rating"
    const val DB_USER_RATINGLIST = "RatingList"
    const val DB_USER_REVIEW = "Review"
    const val DB_USER_REVIEWLIST = "ReviewList"
    const val DB_USER_WISH = "Wish"
    const val DB_USER_WISHLIST = "WishList"
    //리뷰 테이블
    const val DB_REVIEW_ID = "Id"
    const val DB_REVIEW_USER_ID = "UserId"
    const val DB_REVIEW_DATE = "Date"
    const val DB_REVIEW_POINT = "Point"
    const val DB_REVIEW_WISH = "Wish"
    const val DB_REVIEW_CONTENT = "Content"
    const val DB_REVIEW_LIKE = "Like"
    const val DB_REVIEW_LIKE_LIST = "LikeList"
    const val DB_REVIEW_COMMENT = "Comment"
    //리뷰 댓글 속성
    const val DB_REVIEW_COMMENT_DATA = "CommentData"
    const val DB_REVIEW_COMMENT_ID = "CommentId"
    const val DB_REVIEW_COMMENT_CONTENT = "CommentContent"
    const val DB_REVIEW_COMMENT_DATE = "CommentDate"
    const val DB_REVIEW_COMMENT_LIKE = "CommentLike"
    const val DB_REVIEW_COMMENT_LIKE_LIST = "CommentLikeList"
    //어뎁터 타입
    const val HEADER_TYPE = 123
    const val ITEM_TYPE = 124
    const val FOOTER_TYPE = 125
    //뷰페이저 페이지 이름
    const val HOME = "HOME"
    const val TAG = "TAG"
    const val COLUMN = "COLUMN"
    const val MY = "MY"
    //etc
    const val TAG_HIGHLIGHT = "HL"  //태그 하이라이트 표시
    const val TAG_SEARCH_CHECK = "TAG_SEARCH"
    //비회원유저 아이디
    const val UNAUTH_USER = "UNAUTH_USER"
    //비회원유저 로그인 시도
    const val UNAUTH_USER_LOGIN = "UNAUTH_USER_LOGIN"
    //비회원유저 로그인 완료
    const val UNAUTH_USER_LOGIN_REQUEST = 1000
    const val UNAUTH_USER_LOGIN_SUCCESS = 1001
    const val SIGN_UP_SUCCESS = 1002
    const val DONE_DATA_SET = 1003
    const val DONE_TAG_SET = 1004
    const val CANCEL_TAG_SET = 1005
    const val SIGN_IN_SUCCESS = 1006
    const val SIGN_IN_VIEW = 1007
    const val SIGN_UP_VIEW = 1008
    //이메일가입 첫 로그인시 유저 정보 저장용 인텐트 name
    const val CONFIRM_SUCCESS = 1009
    const val CONFIRM_VIEW = 1010
    const val FIRST_SIGN_IN = 1011
    const val SET_INFO = 1012
    const val SET_TAG = 1013
    const val SIGN_IN_LET_UP = 1014
    const val SIGN_IN_SUCCESS_FOR_CONFIRM = 1015
    const val REVIEW_CREATE = 1016
    const val REVIEW_CREATE_DONE = 1017
    //provider
    const val UNAUTH = "PROVIDER_UNAUTH"
    const val COGNITO = "PROVIDER_COGNITO"
    const val FACEBOOK = "graph.facebook.com"
    const val GOOGLE = "accounts.google.com"
    const val TWITTER = "api.twitter.com"
    const val AMAZON = "www.amazon.com"

    const val USER_PASSWORD = "USER_PASSWORD"
    const val USER_DATA = "USER_DATA"
    const val USER_List_DATA = "USER_List_DATA"
    const val INTENT_PRODUCT_PICTURE = "INTENT_PRODUCT_PICTURE"
    const val INTENT_MY_REVIEW = "INTENT_MY_REVIEW"

    //유저 데이터 로컬저장 키
    const val USER_ID = "id"
    const val USER_NAME = "name"
    const val USER_PICTURE = "picture"
    const val USER_PROVIDER = "provider"
    const val USER_AGE = "age"
    const val USER_GENDER = "gender"
    const val USER_TAG = "preferTag"
    const val USER_ID_EMAIL = "email"
    const val USER_BIRTH = "birth"
    const val USER_NULL = false
    const val USER_DEFAULT_DATA = "NULL"
    const val USER_DEFAULT_DATA2 = false
    const val USER_DEFAULT_AGE = 0L
    //유저 데이터 로컬 저장 키 함수
    fun userId() = User.id // 유저 로컬에 정보 저장되어있는지 확인
    fun userName() = USER_NAME + User.id
    fun userPicture() = USER_PICTURE + User.id
    fun userProvider() = USER_PROVIDER + User.id
    fun userAge() = USER_AGE + User.id
    fun userGender() = USER_GENDER + User.id

    //인텐트 값
    //제품 데이터
    const val PRODUCT_DATA = "PRODUCT_DATA"
    //유저 맵데이터 키값
    const val NOW_USER = "NOW_USER"
    fun USER_TAG_PREFER() = "NOW_USER${User.id}"
    const val PICTURE_SIGNATURE = "PICTURE_SIGNATURE" //글라이드 캐시 이미지 변경 오브젝트 키
    //로컬 데이터 저장 경로
    fun userData() = "/data/user/0/com.junho.app.kingcon/files/${USER_DATA + User.id}.dat"
    fun userListData() = "/data/user/0/com.junho.app.kingcon/files/$USER_List_DATA.txt"
    fun appData() = "/data/user/0/com.junho.app.kingcon/files/AppData.txt"

    //앱 데이터 버전 체크
    const val APP_TAG_VERSION = "APP_TAG_VERSION"
    //앱 태그 데이터 저장 경로
    const val APP_TAG_DATA = "APP_TAG_DATA"

    const val TAG_ = "TAG_KING_CON"

}