//package com.junho.app.kingcon.Etc
//
//import android.annotation.SuppressLint
//import android.os.StrictMode
//import com.amazonaws.ClientConfiguration
//import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
//import com.amazonaws.mobile.client.AWSMobileClient
//import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
//import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression
//import com.amazonaws.regions.Region
//import com.amazonaws.regions.Regions
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
//import com.amazonaws.services.dynamodbv2.model.*
//import com.junho.app.kingcon.Etc.StringData.USER_TABLE
//import com.junho.app.kingcon.Item.UserData
//import java.util.*
//import kotlin.concurrent.thread
//
//class AWSdbT {
//    private var client: AmazonDynamoDBClient? = null
//    private lateinit var dynamoDBMapper: DynamoDBMapper
//    private lateinit var dynamoDb: AmazonDynamoDBClient
//    fun init() {
//        client = Region.getRegion(Regions.AP_NORTHEAST_2) // CRUCIAL
//            .createClient(
//                AmazonDynamoDBClient::class.java,
//                AWSMobileClient.getInstance().credentialsProvider,
//                ClientConfiguration()
//            )
//        dynamoDBMapper = DynamoDBMapper.builder()
//            .dynamoDBClient(client)
//            .awsConfiguration(AWSMobileClient.getInstance().configuration)
//            .build()
//        dynamoDb = AmazonDynamoDBClient(AWSMobileClient.getInstance().credentialsProvider,
//            ClientConfiguration())
//    }
//    //공급자로 유저 이미 가입했는지 확인
//    fun isSignUp(callBack: CallbackAWSDB.Login){
//        runOnUiThread {
//            networkThread() //네트워크 사용 쓰레드
//            val userData = UserData()
//            userData.id = User.id
//            val queryExpression = DynamoDBQueryExpression<UserData>()
//                .withHashKeyValues(userData)
//                .withScanIndexForward(false)
//            val a = dynamoDBMapper.query(UserData::class.java, queryExpression).size
//            callBack.isUserSignUp(a != 0)
//        }
//    }
//    //유저 정보 저장
//    fun userAttrCreate(userData: UserData){
//        thread {
//            dynamoDBMapper.save(userData)
//        }
//    }
//    fun userAttrUpdate(userData: UserData){
//        thread {
//            dynamoDBMapper.save(userData)
//        }
//    }
//    /////////////////////////////////////////////////////////////////////////댓글//////////////////////////////////////////////////////////////////
//    //댓글 항목 생성
//    fun commentCreate(postData: PostCommentData){
//        thread {
//            val key = hashMapOf<String, AttributeValue>()
//            key["Type"] = AttributeValue().withS(postData.type)           //hash
//            key["Date"] = AttributeValue().withN(postData.date.toString())//range
//            val attrValue = hashMapOf<String, AttributeValue>()
//            attrValue["Date"] = AttributeValue().withN(postData.commentList[0].date.toString())
//            attrValue["Comment"] = AttributeValue().withS(postData.commentList[0].comment)
//            attrValue["UserId"] = AttributeValue().withS(postData.commentList[0].userId)
//            val uirList = UpdateItemRequest()
//                .withTableName(POST_TABLE)
//                .withKey(key)
//                .addAttributeUpdatesEntry("CommentList",
//                    AttributeValueUpdate().withValue(AttributeValue().withL(AttributeValue().withM(attrValue))).withAction(
//                        AttributeAction.ADD))
//            val uir = UpdateItemRequest()
//                .withTableName(POST_TABLE)
//                .withKey(key)
//                .addAttributeUpdatesEntry("Comment",
//                    AttributeValueUpdate().withValue(AttributeValue().withN("1")).withAction(AttributeAction.ADD))
//            client?.updateItem(uirList)
//            client?.updateItem(uir)
//        }
//    }
//    //댓글 초기화 목록 가져오기
//    fun commentGetList(post: PostData, callBack: CallbackAWSDB.Comment){
//        thread {
//            networkThread() //네트워크 사용 쓰레드
//
//            val a = PostCommentData(post.type, post.date)
//            val b = arrayListOf<UserData>()
//            //댓글 개수 불러오기
//            var num = PostCommentNum(a.type, a.date)
//            val queryExpression = DynamoDBQueryExpression<PostCommentNum>()
//                .withHashKeyValues(num)
//                .withRangeKeyCondition("Date"
//                    ,Condition().withAttributeValueList(AttributeValue().withN(num.date.toString()))
//                        .withComparisonOperator(ComparisonOperator.EQ.toString()))
//                .withLimit(1)
//                .withScanIndexForward(false)
//            num = dynamoDBMapper.queryPage(PostCommentNum::class.java, queryExpression).results[0]
//            //게시물 리스트에서 댓글 개수 변경해주기
//            post.comment = num.commentNum
//            //입력된 댓글 없으면 스레드 종료
//            if(num.commentNum==0L) {
//                callBack.loadDataEmpty()
//                return@thread
//            }
//            val start = num.commentNum-1
//            val end = if(num.commentNum>INITIAL_COMMENT_NUM) num.commentNum - INITIAL_COMMENT_NUM
//            else 0
//
//            getCommentList(start, end, a)
//            a.commentList.sortWith(PostCommentData.CommentData.DateComparator)
//            getCommentUserList(a.commentList, b)
//
//            callBack.loadDataCallback(a, b)
//        }
//    }
//    //댓글 리스트 추가
//    fun commentAddList(post: PostData, callBack: CallbackAWSDB.Comment, listNum: Long){
//        //댓글 다가져오지 않았을때 실행
//        if(post.comment!! > listNum)
//            thread {
//                networkThread() //네트워크 사용 쓰레드
//                val a = PostCommentData(post.type, post.date)
//                val b = arrayListOf<UserData>()
//                //가져올 범위 설정
//                val start = post.comment?.minus(listNum+1)
//                val end: Long =
//                    if(start!!.minus(ADD_COMMENT_LIST_NUM) > 0) start.minus(ADD_COMMENT_LIST_NUM)
//                    else 0
//                //댓글 리스트 가져오기
//                getCommentList(start, end, a)
//                a.commentList.sortWith(PostCommentData.CommentData.DateComparator)
//                getCommentUserList(a.commentList, b)
//                callBack.addDataCallback(a,b)
//            }
//        else{ //댓글 다 가져온 경우 하단바 닿아도 아무 변화 없도록
//            callBack.listFull()
//        }
//    }
//    //댓글 삭제
//    fun commentDelete(postData: PostData, position: Int) {
//        thread {
//            val key = hashMapOf<String, AttributeValue>()
//            key["Type"] = AttributeValue().withS(postData.type)           //hash
//            key["Date"] = AttributeValue().withN(postData.date.toString())//range
//            //댓글 항목 삭제
//            val uirList = UpdateItemRequest()
//                .withTableName(POST_TABLE)
//                .withKey(key)
//                .withUpdateExpression("REMOVE CommentList[$position]")
//            client?.updateItem(uirList)
//            //댓글 개수 -카운트
//            val uir = UpdateItemRequest()
//                .withTableName(POST_TABLE)
//                .withKey(key)
//                .addAttributeUpdatesEntry("Comment",
//                    AttributeValueUpdate().withValue(AttributeValue().withN("-1")).withAction(AttributeAction.ADD))
//            client?.updateItem(uir)
//        }
//    }
//    //댓글 리스트 가져오기 및 세팅
//    private fun getCommentList(start: Long, end: Long, postData: PostCommentData){
//        val keys = hashMapOf<String, AttributeValue>()
//        keys["Type"] = AttributeValue().withS(postData.type)           //hash
//        keys["Date"] = AttributeValue().withN(postData.date.toString())//range
//        //리스트 가져올 목록
//        var commentListNum = ""
//        for(i in start downTo end){
//            commentListNum += "CommentList[$i]"
//            if(i != end) commentListNum += ","
//        }
//        //댓글 리스트 받아오기
//        val gi = client?.getItem(GetItemRequest()
//            .withTableName(POST_TABLE)
//            .withKey(keys)
//            .withProjectionExpression(commentListNum))
//        //댓글리스트 세팅
//        gi?.item?.forEach {
//            it.value.l.forEach{list->
//                postData.commentList.add(PostCommentData.CommentData(
//                    list.m["Date"]!!.n.toLong(),
//                    list.m["Comment"]!!.s.toString(),
//                    list.m["UserId"]!!.s.toString()
//                ))
//            }
//        }
//    }
//    //해당 댓글 유저 목록 가져오기
//    private fun getCommentUserList(commentList: ArrayList<PostCommentData.CommentData>, userData: ArrayList<UserData>){
//        val batchGetItemRequest = BatchGetItemRequest()
//        val requestItems = HashMap<String, KeysAndAttributes>()     //
//        val tableKeys = ArrayList<Map<String, AttributeValue>>()    //
//        var key: MutableMap<String, AttributeValue>     //
//        val keyCheck= hashMapOf<String, Boolean>() //
//        //키값 중복 안되도록 저장
//        commentList.forEach {
//            if(keyCheck[it.userId] != true) {
//                key = HashMap()
//                key["Id"] = AttributeValue().withS(it.userId)
//                tableKeys.add(key)
//                keyCheck[it.userId] = true
//            }
//        }
//        requestItems[USER_TABLE] = KeysAndAttributes().withKeys(tableKeys)
//        batchGetItemRequest.withRequestItems(requestItems)
//        val result = client?.batchGetItem(batchGetItemRequest)
//        //게시물에 맞게 유저 리스트
//        commentList.forEach{post->
//            result?.responses?.forEach {
//                loof@ for(i in 0 until it.value.size){
//                    val id = it.value[i]["Id"]?.s
//                    if(id == post.userId){
//                        userData.add(UserData(id.toString(), it.value[i]["Provider"]!!.s, it.value[i]["Name"]!!.s))
//                        break@loof
//                    }
//                }
//            }
//        }
//    }
//    /////////////////////////////////////////////////////////////////게시물//////////////////////////////////////////////////////////////////////////////
//    //게시물 항목 생성
//    fun postCreate(postData: PostData) {
//        thread {
//            dynamoDBMapper.save(postData)
//        }
//    }
//    //테이블에서 데이터 가져오기
//    @SuppressLint("ObsoleteSdkInt")
//    fun postGetList(callBack: CallbackAWSDB.List, type: String, lastKey: HashMap<String, AttributeValue>?, init: Boolean) {
//        thread {
//            networkThread() //네트워크 사용 쓰레드
////        val rangeKeyCondition = Condition()
////            .withComparisonOperator(ComparisonOperator.BETWEEN.toString())
////            .withAttributeValueList(
////                        AttributeValue().withN("${index+initialListSize}"),
////                        AttributeValue().withN("$index")
////        )
//            if(lastKey == null && !init)
//                return@thread
//            val postData = PostData()
//            postData.type = type
//            val queryExpression = DynamoDBQueryExpression<PostData>()
//                .withHashKeyValues(postData)
//                .withLimit(INITAIL_POST_NUM)
//                .withExclusiveStartKey(lastKey)
//                .withScanIndexForward(false)
////            val eav = hashMapOf<String, AttributeValue>()
////            eav[":val1"] = AttributeValue().withS(POST_TYPE)
//            //스켄이 비용 많이듬 (테이블전체 검색)
////            val scanExpression = DynamoDBScanExpression()
////                .withFilterExpression("Type = :val1")
////                .withExpressionAttributeValues(eav)
//            val a = arrayListOf<PostData>()
//            val b = arrayListOf<UserData>()
//            val data = dynamoDBMapper.queryPage(PostData::class.java, queryExpression)
//            a.addAll (data.results)
//
////            a.addAll(dynamoDBMapper.scan(PostData::class.java, scanExpression))
//            a.sortWith(PostData.DateComparatorDate)
//            getPostUserList(a,b)
//            callBack.loadDataCallback(a,b, data.lastEvaluatedKey)
//        }
//    }
//    //스크롤 하단 내릴시 리스트 추가
//    fun postAddList(callBack: CallbackAWSDB.List, lastPost: PostData, type: String) {
//        thread {
//            networkThread() //네트워크 사용 쓰레드
//            val postData = PostData()
//            postData.type = type
//            val rangeKeyCondition = Condition()
//                .withComparisonOperator(ComparisonOperator.LT.toString())
//                .withAttributeValueList(AttributeValue().withN("${lastPost.date}"))
//            val queryExpression = DynamoDBQueryExpression<PostData>()
//                .withHashKeyValues(postData)
//                .withRangeKeyCondition("Date",rangeKeyCondition)
//                .withLimit(ADD_POST_NUM)
//                .withScanIndexForward(false)
//            val a = arrayListOf<PostData>()
//            val b = arrayListOf<UserData>()
//            a.addAll(dynamoDBMapper.queryPage(PostData::class.java, queryExpression).results)
//            //리스트 다 가져옴
//            if(a.size==0) {
//                callBack.listFull()
//                return@thread
//            }
//            a.sortWith(PostData.DateComparatorDate)
//            getPostUserList(a,b)
//            callBack.addDataCallback(a,b)
//        }
//    }
//    //삭제
//    fun postDelete(postData: PostData) {
//        thread {
//            dynamoDBMapper.delete(postData)
//        }
//    }
//    //좋아요 개수 업데이트
//    fun postLikeUpdate(callBack: CallbackAWSDB.List, postData: PostData, position: Int){
//        thread {
//            /**
//             * DynamoDBMapperConfig.SaveBehavior 속성
//             * UPDATE(기본값)
//             * 저장하려는 객체에 null 있으면 db에 해당 속성 제거하고 업데이트
//             *
//             * UPDATE_SKIP_NULL_ATTRIBUTES
//             * null 제거하지 않고 덮어 쓰기
//             *
//             * APPEND_SET
//             * 스칼라속성은 null 제거하지않고 덮어씀
//             * set 속성은 덮어쓰지않고 추가(유형 일치해야함)
//             *
//             * CLOBBER
//             * 모든데이터 지우고 새로 쓰기
//             * --------------------------
//             * DynamoDBSaveExpression()
//             * 저장 조건 설정
//             */
//            /**
//             *              updateItem 파라미터 참조
//             *                  updateExpression
//             * SET - 하나 이상의 속성과 값을 항목에 추가. 속성 중 하나라도 이미 존재하면 새 값으로 대체.
//             * Number 유형 증감 예 SET myNum = myNum + : val (val = 증감 할 수)
//             * 다음 기능을 지원합니다.
//             * if_not_exists (경로, 피연산자)
//             * - 항목에 지정된 경로에 속성이없는 경우
//             * if_not_exists 가 피연산자로 평가됨. 그렇지 않으면 경로로 평가됨.
//             * 이 함수를 사용하여 이미 항목에있을 수있는 특성을 덮어 쓰지 않도록 할 수 있음.
//             * list_append (피연산자, 피연산자)
//             * - 새 요소가 추가 된 목록으로 평가됨. 피연산자의 순서를 반대로하여 목록의 시작 또는 끝에 새 요소를 추가 할 수 있음.
//             * 이 함수 이름은 대소 문자 구분.
//             *
//             * REMOVE - 항목에서 하나 이상의 속성을 제거.
//             *
//             * ADD - 속성이 이미 존재하지 않으면 지정된 값을 항목에 추가.
//             * 속성이 존재하면 ADD 의 동작은 속성의 데이터 유형에 따라 다르다.
//             * - 기존 속성이 숫자이고 Value 가 숫자 인 경우
//             * Value 가 수학적으로 기존 속성에 더해짐 Value 가 음수이면 기존 속성에서 뺌.
//             * ADD 를 사용하여 업데이트 전에 존재하지 않는 항목의 숫자 값을 증가 시키거나 감소 시키면 DynamoDB는 0 을 초기 값으로 사용합니다.
//             * - ADD 를 사용하여 업데이트 전에 존재하지 않는 속성 값을 증가 또는 감소시키는 경우 DynamoDB는 0을 초기 값으로 사용합니다.
//             * 예를 들어, 업데이트하려는 항목에 itemcount 라는 속성이 없지만 3 에 ADD를 지정했다고 가정 해보십시오.
//             * DynamoDB는 itemcount 속성을 만들고 초기 값을 0으로 설정하고 마지막으로 3을 추가합니다.
//             * 결과는 아이템의 새로운 itemcount 속성이며 값은 3 입니다.
//             * - 기존 데이터 유형이 set 이고 Value 도 set 인 경우 Value 값은 기존 집합에 추가됩니다.
//             * 예를 들어 속성 값이 set [1,2] 이고 ADD 작업이 [3] 으로 지정된 경우 최종 속성 값은 [1,2,3] 입니다.
//             * set 속성에 ADD 액션이 지정되고 지정된 속성 유형이 기존 설정 유형과 일치하지 않으면 오류가 발생합니다.
//             * - 두 set 모두 동일한 데이터 유형을 가져야합니다.
//             * 예를 들어, 기존 데이터 유형이 문자열 set 인 경우 Value 도 문자열 set 여야합니다.
//             * ADD 액션은 숫자 및 데이터 유형만 지원합니다. 또한 ADD 는 중첩 된 속성이 아닌 최상위 속성에서만 사용할 수 있습니다.
//             *
//             * DELETE - 세트에서 요소를 삭제합니다.
//             * - set 의 값이 지정되면 해당 값은 set 에서 뺍니다.
//             * 예를 들어 속성 값이 [a, b, c] set 이고 DELETE 동작이 [a, c] 최종 속성 값은 [ b ] 이다.
//             * 빈 set 를 지정하면 오류가 발생.
//             * - DELETE 액션은 set 데이터 타입만을 지원합니다. 또한 DELETE 는 중첩 된 속성이 아닌 최상위 속성에서만 사용할 수 있습니다.
//             *
//             *
//             * SET a = : value1, b = : value2 DELETE : value3, : value4, : value5 와 같이 하나의 식에서 여러 가지 작업을 수행 할 수 있습니다.
//             *
//             * ##업데이트 표현식에 대한 자세한 내용은
//             * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.Modifying.html" 을 참조하십시오.
//             * 'Amazon DynamoDB 개발자 가이드' 의 '항목 및 속성 수정' 을 참조하십시오.
//             *
//             *
//             *                      conditionExpression
//             * conditional update 가 성공하기 위해 충족되어야하는 조건.
//             * 표현식은 다음 중 하나를 포함 할 수 있습니다.
//             * Functions - attribute_exists | attribute_not_exists | attribute_type | contains | begins_with | size
//             * 대소문자 구분해야함
//             * Comparison operators(비교 연산자) :
//             * = | &lt;&gt; | &lt; | &gt; | &lt;= | &gt;= | BETWEEN | IN
//             * Logical operators(논리 연산자):
//             * AND | OR | NOT
//             *
//             * 조건 표현식에 대한 자세한 내용은 'Amazon DynamoDB Developer Guide' 에서 조건 지정
//             * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html"을 참조하십시오.
//             *
//             *           expression attribute value
//             * 표현식에서 대체 할 수있는 하나 이상의 값.
//             * 표현식에서 : (콜론) 문자를 사용하여 속성 값을 참조 해제하십시오.
//             * 예를 들어 ProductStatus 속성의 값이 다음 중 하나인지 확인하려고한다고 가정합니다.
//             * Available | Backordered | Discontinued
//             * (이용 가능) | (값 반환)     | (중단 됨)
//             *
//             * 예
//             * ExpressionAttributeValues 는 다음과 같다 :
//             * {":avail":{"S":"Available"}, ":back":{"S":"Backordered"}, ":disc":{"S":"Discontinued"} }
//             * 그러면 다음과 같은 표현식에서이 값을 사용할 수 있습니다.
//             * ProductStatus IN (:avail, :back, :disc)
//             *
//             *          expression attribute names
//             * 표현식에서 속성 이름에 대한 하나 이상의 대체 토큰. 다음은 몇 가지 사용 사례입니다.
//             * 이름이 DynamoDB 예약어와 충돌하는 속성에 액세스합니다.? (테이블 항목하고 이름 같아야한다는 뜻인듯)
//             * 표현식에서 속성 이름의 반복 항목에 대한 자리 표시자를 만들려면
//             * 표현식에서 속성 이름의 특수 문자가 잘못 해석되는 것을 방지합니다.
//             * 표현식에서 # 문자를 사용하여 속성 이름의 참조를 취소하십시오. 예를 들어, 다음 속성을 고려하십시오.
//             * 이름 : Percentile
//             * 이 속성의 이름은 예약어와 충돌하므로 표현식에서 직접 사용할 수 없습니다.
//             * 전체 예약어 목록은 Amazon의 "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/ReservedWords.html" 예약어 를 참조하십시오.
//             * DynamoDB 개발자 안내서). 이 문제를 해결하려면 ExpressionAttributeNames 에 대해 다음을 지정할 수 있습니다. : {"#P":"Percentile"}
//             * 이 예제에서와 같이 식에서이 대체를 사용할 수 있습니다. : #P = :val
//             *  :  문자로 시작하는 토큰은 런타임시 실제 값의 자리 표시자인  expression attribute values 입니다.
//             * For more information on expression attribute names,
//             * see "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.AccessingItemAttributes.html"
//             * 'Accessing Item Attributes' in the 'Amazon DynamoDB' Developer Guide'.
//             *
//             */
//            //좋아요 개수 가감 (트랜잭션)
//            val like = postData.isLike!! //좋아요 상태
//            val key = hashMapOf<String, AttributeValue>()
//            key["Type"] = AttributeValue().withS(postData.type)           //hash
//            key["Date"] = AttributeValue().withN(postData.date.toString())//range
//            val attrAction: AttributeAction
//            val n = if(like){ //좋아요 ㅇㅇ -> 좋아요 해제
//                attrAction = AttributeAction.DELETE
//                "-1"
//            }
//            else { // 좋아요 ㄴㄴ -> 좋아요
//                attrAction = AttributeAction.ADD
//                "1"
//            }
//            editor.putBoolean(postLikeSignature(postData.date!!), !like).commit()
//            postData.isLike = !like
//            val uir = UpdateItemRequest()
//                .withTableName(POST_TABLE)
//                .withKey(key)
//                .addAttributeUpdatesEntry("Like",
//                    AttributeValueUpdate().withValue(AttributeValue().withN(n)).withAction(AttributeAction.ADD))
//                .addAttributeUpdatesEntry("LikeList",
//                    AttributeValueUpdate().withValue(AttributeValue().withSS(User.id)).withAction(attrAction))
//            client?.updateItem(uir)
//            //좋아요 개수 반영
//            callBack.likeUpdateCallback(postData, position)
//        }
//    }
//    /////////////////////랭킹 데이터////////////////////////
//    @SuppressLint("ObsoleteSdkInt")
//    fun rankingGetList(callBack: CallbackAWSDB.Ranking, trueTime: Date, lastKey: ArrayList<Map<String, AttributeValue>?>, init: Boolean) {
//        thread {
//            if(lastKey[0] == null && lastKey[1] == null && !init)
//                return@thread
//            val t = System.currentTimeMillis()
//            networkThread() //네트워크 사용 쓰레드
//            val keyConditions = HashMap<String, Condition>()
//            keyConditions["Type"] = Condition()
//                .withComparisonOperator(ComparisonOperator.EQ.toString())
//                .withAttributeValueList(AttributeValue().withS(NEWS_TYPE))
//            keyConditions["Like"] = Condition()
//                .withComparisonOperator(ComparisonOperator.GE.toString())
//                .withAttributeValueList(AttributeValue().withN("0"))
//
//            //힙
//            val queryRequest = QueryRequest()
//                .withTableName(POST_TABLE)
//                .withKeyConditions(keyConditions)
//                .withLimit(5)
//                .withIndexName("Type-Like-index-copy")
//                .withProjectionExpression("#i, #l, #d, #t, #c")
//                .withExpressionAttributeNames(mapOf("#d" to "Date", "#i" to "ImageUrl", "#l" to "Like", "#t" to "Title", "#c" to "Comment"))//예약어 쓸 때 주의
//                .withScanIndexForward(false)
//            //전체 랭킹 힙 뉴스
//            queryRequest.withExclusiveStartKey(lastKey[0])
//            val allHipRanking = client?.query(queryRequest)
//            //주간 랭킹 힙 뉴스
//            val eav = HashMap<String, AttributeValue>()
//            eav[":v1"] = AttributeValue().withN((trueTime.time - (1000 * 60 * 60 * 24 * 7)).toString())
//            eav[":v2"] = AttributeValue().withN(trueTime.time.toString())
//            queryRequest.withFilterExpression("#date BETWEEN :v1 AND :v2")
//                .withExpressionAttributeValues(eav)
//                .addExpressionAttributeNamesEntry("#date","Date")
//            keyConditions["Type"] = Condition()
//                .withComparisonOperator(ComparisonOperator.EQ.toString())
//                .withAttributeValueList(AttributeValue().withS(NEWS_TYPE))
//            queryRequest.keyConditions = keyConditions
//            queryRequest.withExclusiveStartKey(lastKey[1])
//            val weekHipRanking = client?.query(queryRequest)
//
//            //검색 마지막 결과값 반환 -> null이면 더이상 값없음, null아닐경우 withExclusiveStartKey 에 값 넣으면 마지막 값부터 데이터 가져오기 시작
//            lastKey.clear()
//            lastKey.add(allHipRanking!!.lastEvaluatedKey)
//            lastKey.add(weekHipRanking!!.lastEvaluatedKey)
//
//            val allDataTemp = arrayListOf<PostData>()
//            val weekDataTemp = arrayListOf<PostData>()
//
//            for(item in allHipRanking.items){
//                allDataTemp.add(PostData(item["Date"]?.n?.toLong(),item["ImageUrl"]?.s.toString(),
//                    item["Title"]?.s?.toString(),item["Like"]?.n?.toLong(),item["Comment"]?.n?.toLong()))
//            }
//            for(item in weekHipRanking.items){
//                weekDataTemp.add(PostData(item["Date"]?.n?.toLong(),item["ImageUrl"]?.s.toString(),
//                    item["Title"]?.s?.toString(),item["Like"]?.n?.toLong(),item["Comment"]?.n?.toLong()))
//            }
//            allDataTemp.sortWith(PostData.DateComparatorLike)
//            weekDataTemp.sortWith(PostData.DateComparatorLike)
//
//            callBack.loadDataCallback(allDataTemp, weekDataTemp, lastKey)
//        }
//    }
//    /////////////@#!@#!@#@!#////////////////ect
//    //해당 게시물에 유저 목록 가져오기
//    private fun getPostUserList(postData: ArrayList<PostData>, userData: ArrayList<UserData>) {
//        postData.forEach {
//            //좋아요, 댓글 null 이면 0개로 표시되게
//            if (it.like == null) it.like = 0
//            if (it.comment == null) it.comment = 0
//            //좋아요 한건지 확인
//            it.isLike = pref.getBoolean(postLikeSignature(it.date!!), false)
//        }
//        val batchGetItemRequest = BatchGetItemRequest()
//        val requestItems = HashMap<String, KeysAndAttributes>()     //
//        val tableKeys = ArrayList<Map<String, AttributeValue>>()    //
//        var key: MutableMap<String, AttributeValue>     //
//        val keyCheck = hashMapOf<String, Boolean>() //
//        //키값 중복 안되도록 저장
//        postData.forEach {
//            if (keyCheck[it.userId] != true) {
//                key = HashMap()
//                key["Id"] = AttributeValue().withS(it.userId)
//                tableKeys.add(key)
//                keyCheck[it.userId!!] = true
//            }
//        }
//        requestItems[USER_TABLE] = KeysAndAttributes().withKeys(tableKeys)
//        batchGetItemRequest.withRequestItems(requestItems)
//
//        if (tableKeys.size != 0){
//            val result = client?.batchGetItem(batchGetItemRequest)
//            //게시물에 맞게 유저 리스트
//            postData.forEach { post ->
//                result?.responses?.forEach {
//                    loof@ for (i in 0 until it.value.size) {
//                        val id = it.value[i]["Id"]?.s
//                        if (id == post.userId) {
//                            userData.add(UserData(id.toString(), it.value[i]["Provider"]!!.s, it.value[i]["Name"]!!.s))
//                            break@loof
//                        }
//                    }
//                }
//            }
//        }
//
//    }
//    @SuppressLint("ObsoleteSdkInt")
//    private fun networkThread() {
//        //네트워크 사용 쓰레드
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//            StrictMode.setThreadPolicy(policy)
//        }
//    }
//}