package com.junho.app.kingcon.Login.View

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.IdentityProvider
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.client.results.SignInResult
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService
import com.facebook.*
import com.facebook.login.LoginResult
import com.junho.app.kingcon.Etc.*
import com.junho.app.kingcon.Etc.CallBack.CallbackAWSDB
import com.junho.app.kingcon.Etc.StringData.CANCEL_TAG_SET
import com.junho.app.kingcon.Etc.StringData.COGNITO
import com.junho.app.kingcon.Etc.StringData.CONFIRM_SUCCESS
import com.junho.app.kingcon.Etc.StringData.CONFIRM_VIEW
import com.junho.app.kingcon.Etc.StringData.DONE_DATA_SET
import com.junho.app.kingcon.Etc.StringData.DONE_TAG_SET
import com.junho.app.kingcon.Etc.StringData.FACEBOOK
import com.junho.app.kingcon.Etc.StringData.FIRST_SIGN_IN
import com.junho.app.kingcon.Etc.StringData.NOW_USER
import com.junho.app.kingcon.Etc.StringData.SET_INFO
import com.junho.app.kingcon.Etc.StringData.SET_TAG
import com.junho.app.kingcon.Etc.StringData.SIGN_IN_SUCCESS
import com.junho.app.kingcon.Etc.StringData.SIGN_IN_SUCCESS_FOR_CONFIRM
import com.junho.app.kingcon.Etc.StringData.SIGN_IN_VIEW
import com.junho.app.kingcon.Etc.StringData.SIGN_UP_SUCCESS
import com.junho.app.kingcon.Etc.StringData.SIGN_UP_VIEW
import com.junho.app.kingcon.Etc.StringData.UNAUTH
import com.junho.app.kingcon.Etc.StringData.UNAUTH_USER
import com.junho.app.kingcon.Etc.StringData.UNAUTH_USER_LOGIN
import com.junho.app.kingcon.Etc.StringData.UNAUTH_USER_LOGIN_SUCCESS
import com.junho.app.kingcon.Etc.StringData.USER_BIRTH
import com.junho.app.kingcon.Etc.StringData.USER_DEFAULT_DATA2
import com.junho.app.kingcon.Etc.StringData.USER_ID
import com.junho.app.kingcon.Etc.StringData.USER_NAME
import com.junho.app.kingcon.Etc.StringData.USER_PASSWORD
import com.junho.app.kingcon.Etc.StringData.userData
import com.junho.app.kingcon.Etc.StringData.userListData
import com.junho.app.kingcon.Item.UserData
import com.junho.app.kingcon.Item.UserProfileData
import com.junho.app.kingcon.main.ViewMain
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.activity_view_login.*
import java.io.File
import java.util.*
import kotlin.concurrent.thread

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")
class ViewLogin : AppCompatActivity(), CallbackAWSDB.Login {
    private var callbackManager: CallbackManager? = null
    private var unAuthLoginBool = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_login)

        initialize()
        setView()
    }

    //로그인 완료 후 메인뷰 넘어가
    private fun signInSuccess() {
        if (!unAuthLoginBool) { // 로그인 완료
            val intent = Intent(applicationContext, ViewMain::class.java)
            startActivity(intent)
            finish()
        } else { //비회원 로그인 완료
            val intent = intent
            setResult(UNAUTH_USER_LOGIN_SUCCESS, intent)
            finish()
        }
    }

    //유저 데이터 세팅
    private fun userDataSetting() {
        //로컬에 있는지 확인한다 시발
        val listFile = File(userListData())
        if (listFile.exists()) {
            //로컬에 유저 리스트 데이터 있음
            User.localUserListRead(listFile)
            User.id = User.mapData[NOW_USER] as String
            //로컬에 유저 리스트 데이터 있음
            val file = File(userData())
            if (file.exists()) {
                //로컬에 유저 데이터 파일 있음
                User.localUserDataRead(file)
                if (User.age == 0L) {
                    //속성 설정 안함 -> info 페이지
                    thread {
                        AWSDB.isSignUp(this)
                    }
                } else {
                    signInSuccess()
                }
            } else {
                //로컬에 유저 데이터 없음 -> 서버에서 유저 등록되어있는지 확인 / 가져와야함
                AWSDB.isSignUp(this@ViewLogin)
            }
        } else {
            //로컬데이터 없다면 -> 서버에서 가져오기
            User.localUserListUpdate()
        }
    }

    //비회원 정보 로컬 저장
    private fun unAuthUserDataSave() {
        User.run {
            id = UNAUTH_USER
            name = "비회원"
            picture = USER_DEFAULT_DATA2
            provider = UNAUTH
        }
        signInSuccess()
    }

    //등록된 공급자 유저인지 확인 콜백
    override fun isUserSignUp(id: Boolean, info: Boolean) {
        //서버에 저장 안된 유저면 속성 DB 저장
        if (!id) {
            if (User.provider == COGNITO)
                User.name = AWSMobileClient.getInstance().userAttributes[USER_NAME].toString()
            User.picture = false
            User.localUserDataUpdate()
            AWSDB.createUserBasicAttr(UserData(User))
        }
        //서버에 이름 나이 데이터 없음 -> 저장하러 가기
        if (!info) {
            User.localUserDataUpdate()
            startActivityForResult(Intent(this, ViewSetInfo::class.java), SET_INFO)
        }
        //다됐으면 서버에서 읽어와서 로컬에 저장
        else
            thread {
                AWSDB.readUserAttr()
                signInSuccess()
            }
        if (id && info) signInSuccess()

        runOnUiThread { Util.progressVisible(false, progress, this@ViewLogin) }
    }

    //페이스북 로그인 세션
    private fun setFacebookSession(accessToken: AccessToken) {
        thread {
            //facebook attr - partition / first_name / last_name / middle_name / name / name_format / picture / short_name
            AWSMobileClient.getInstance().federatedSignIn(
                IdentityProvider.FACEBOOK.toString(),
                accessToken.token,
                object : Callback<UserStateDetails> {
                    override fun onResult(userStateDetails: UserStateDetails) {
                        val request = GraphRequest.newMeRequest(accessToken) { user, _ ->
                            //유저데이터 없으면
                            User.run {
                                id = user.optString(USER_ID)
                                provider = FACEBOOK
                                name = user.optString(USER_NAME)
                                picture = false
                                mapData[NOW_USER] = id
                                localUserListUpdate()
                                localUserDataUpdate()
                            }
                            //등록된 유저인지 확인
                            AWSDB.isSignUp(this@ViewLogin)
                        }
                        Bundle().let {
                            it.putString(
                                "fields",
                                "id,first_name,last_name,middle_name,name,name_format,picture,short_name,email"
                            )
                            request.parameters = it
                            request.executeAsync()
                        }
                    }

                    override fun onError(e: Exception) {
                        Log.d("facebook!!e", e.toString())
                    }
                })
        }
    }

    fun confirmAndSignIn(id: String) {
        User.run {
            this.id = id
            name = AWSMobileClient.getInstance().userAttributes[USER_NAME].toString()
            provider = COGNITO
            picture = false
            mapData[NOW_USER] = this.id
            localUserDataUpdate()
            localUserListUpdate()
        }
        AWSDB.createUserAddAttr(UserProfileData(User))
        startActivityForResult(Intent(this@ViewLogin, ViewSetInfo::class.java), SET_INFO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            //로그인 완료
            SIGN_IN_SUCCESS -> {
                Util.progressVisible(true, progress, this@ViewLogin)
                //회원가입 됐고 서버에 기본 데이터(이름 아이디 공급자 등) 있음 / 로컬에는 모름
                User.id = data!!.getStringExtra(USER_ID)
                User.mapData[NOW_USER] = User.id
                User.provider = COGNITO
                User.localUserListUpdate()
                val file = File(userData())
                //로컬 파일 있음
                if (file.exists()) {
                    //로컬 데이터 불러오고 -> 성별, 나이 데이터 없으면 서버에 확인 -> 서버에 없으면 정보 입력창 ㄲ
                    User.localUserDataRead(file)
                    when {
                        User.name == "" -> AWSDB.isSignUp(this)
                        //유저 정보 데이터 없음 -> 서버 확인
                        User.age == 0L -> AWSDB.isSignUp(this)
                        else -> {
                            signInSuccess()
                        }
                    }
                }
                //로컬 파일 없음
                else {
                    //서버에서 모든 정보 가져와서 -> 나이, 성별 입력 확인 -> 안했으면 입력창 ㄲ 했으면 바로 시작
                    AWSDB.isSignUp(this)
                }
                //로컬에 유저 리스트 데이터 있음
                userDataSetting()
            }
            SIGN_IN_SUCCESS_FOR_CONFIRM -> {
                Util.progressVisible(true, progress, this@ViewLogin)
                val id = data?.getStringExtra(USER_ID)
                val pass = data?.getStringExtra(USER_PASSWORD)
                AWSMobileClient.getInstance()
                    .signIn(id, pass, null, object : Callback<SignInResult> {
                        override fun onResult(result: SignInResult?) {
                            User.run {
                                this.id = id.toString()
                                name =
                                    AWSMobileClient.getInstance().userAttributes[USER_NAME].toString()
                                provider = COGNITO
                                picture = false
                                mapData[NOW_USER] = this.id
                                localUserDataUpdate()
                                localUserListUpdate()
                                AWSDB.createUserAddAttr(UserProfileData(User))
                                startActivityForResult(
                                    Intent(
                                        this@ViewLogin,
                                        ViewSetInfo::class.java
                                    ), SET_INFO
                                )
                                Util.progressVisible(false, progress, this@ViewLogin)
                            }
                        }

                        override fun onError(e: java.lang.Exception?) {
                            runOnUiThread {
                                when {
                                    e.toString().contains("Failed to authenticate user") -> {
                                        //재접속
                                        AWSMobileClient.getInstance().signIn(
                                            id,
                                            pass,
                                            null,
                                            object : Callback<SignInResult> {
                                                override fun onResult(result: SignInResult?) {
                                                    Log.v(
                                                        ViewSignIn::class.java.name,
                                                        "SignInPresenter-in Done"
                                                    )
                                                    confirmAndSignIn(id!!)
                                                    Util.progressVisible(
                                                        false,
                                                        progress,
                                                        this@ViewLogin
                                                    )
                                                }

                                                override fun onError(e: java.lang.Exception?) {
                                                    val intent = Intent(
                                                        this@ViewLogin,
                                                        ViewSignIn::class.java
                                                    )
                                                    intent.putExtra(
                                                        USER_ID,
                                                        data?.getStringExtra(USER_ID)
                                                    )
                                                    startActivityForResult(intent, FIRST_SIGN_IN)
                                                }
                                            })
                                        AWSMobileClient.getInstance().signOut()
                                    }
                                }
                                Log.v(ViewSignIn::class.java.name, "SignInPresenter-in error", e)
                                Util.progressVisible(false, progress, this@ViewLogin)
                            }
                        }
                    })
            }
            //회원가입 후 메일 인증 페이지
            SIGN_UP_SUCCESS -> {
                val intent = Intent(this, ViewConfirm::class.java)
                intent.putExtra(USER_ID, data?.getStringExtra(USER_ID))
                intent.putExtra(USER_PASSWORD, data?.getStringExtra(USER_PASSWORD))
                startActivityForResult(intent, CONFIRM_VIEW)
            }
            //메일 인증 완료 후 재로그인
            CONFIRM_SUCCESS -> {
                val intent = Intent(this, ViewSignIn::class.java)
                intent.putExtra(USER_ID, data?.getStringExtra(USER_ID))
                startActivityForResult(intent, FIRST_SIGN_IN)
            }
            //정보 입력 완료
            DONE_DATA_SET -> {
                if (requestCode == SET_INFO) {
                    startActivityForResult(Intent(this, ViewSetTag::class.java), SET_TAG)
                    User.gender = data!!.getBooleanExtra(StringData.USER_GENDER, false)
                    User.age = data.getLongExtra(USER_BIRTH, 0)
                    AWSDB.createUserAddAttr(UserProfileData(User))
                    User.localUserDataUpdate()
                }
            }
            DONE_TAG_SET -> {
                //태그 선태 끝남
                if (requestCode == SET_TAG) {
                    User.localUserDataUpdate()
                    signInSuccess()
                }
            }
            CANCEL_TAG_SET -> {
                //태그 선택 안함
                if (requestCode == SET_TAG) {
                    signInSuccess()
                }
            }
        }
    }

    /////////////////////////////////////////////////////////
    @SuppressLint("CommitPrefEdits")
    private fun initialize() {
        thread {
            unAuthLoginBool = intent.getBooleanExtra(UNAUTH_USER_LOGIN, false)
            if (unAuthLoginBool)
                btnUnAuth.visibility = View.GONE
            //클라이언트 초기화
            AWSMobileClient.getInstance()
                .initialize(this@ViewLogin, object : Callback<UserStateDetails> {
                    override fun onResult(userStateDetails: UserStateDetails) {
                        if (userStateDetails.userState.name == "SIGNED_IN") {
                            //유저 데이터 가져오기
                            userDataSetting()
                        }
                    }

                    override fun onError(e: Exception) {
                        Log.e("MainView", "Initialization error.", e)
                    }
                })
            // aws s3 TransferUtility (끊긴 업로드 자동 진행)
            applicationContext.startService(Intent(applicationContext, TransferService::class.java))
            FacebookSdk.sdkInitialize(applicationContext)
        }
    }

    private fun setView() {
        //비회원 시작버튼
        btnUnAuth.setOnClickListener {
            unAuthUserDataSave()
        }
        //이메일 로그인버튼
        btnSignIn.setOnClickListener {
            val intent = Intent(this, ViewSignIn::class.java)
            startActivityForResult(intent, SIGN_IN_VIEW)
        }
        //이메일 회원가입 버튼
        btnGoSignUp.setOnClickListener {
            val intent = Intent(this, ViewSignUp::class.java)
            startActivityForResult(intent, SIGN_UP_VIEW)
        }
        //페이스북 로그인버튼
        btnFacebookSignIn.setOnClickListener {
            facebookSignIn.performClick()
        }
        callbackManager = CallbackManager.Factory.create()
        //페북 로그인 콜백
        facebookSignIn.setReadPermissions(listOf("public_profile"))
        facebookSignIn.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Toast.makeText(applicationContext, "facebook_success", Toast.LENGTH_SHORT).show()
                runOnUiThread { Util.progressVisible(true, progress, this@ViewLogin) }
                val accessToken = loginResult.accessToken
                setFacebookSession(accessToken)
            }

            override fun onCancel() {
                Toast.makeText(applicationContext, "Login Cancel", Toast.LENGTH_LONG).show()
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(applicationContext, exception.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}
