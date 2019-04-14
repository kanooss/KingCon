package com.junho.app.kingcon.Login.View

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.results.SignInResult
import com.amazonaws.mobile.client.results.SignInState
import com.junho.app.kingcon.Etc.StringData.SIGN_IN_LET_UP
import com.junho.app.kingcon.Etc.StringData.SIGN_IN_SUCCESS
import com.junho.app.kingcon.Etc.StringData.SIGN_UP_SUCCESS
import com.junho.app.kingcon.Etc.StringData.USER_ID
import com.junho.app.kingcon.Etc.StringData.USER_PASSWORD
import com.junho.app.kingcon.Etc.User
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.activity_view_sign_in.*
import java.util.regex.Pattern

@Suppress("DEPRECATION")
class ViewSignIn : AppCompatActivity() {
    private var emailCh = false
    private var passCh = false
    private var returnEmail = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_sign_in)
        setInitialize()
        setView()
    }
    //로그인버튼 활성화 확인
    private fun signInButtonEnable(ch1 : Boolean, ch2 : Boolean){
        if(ch1 && ch2){
            btnSignIn.run {
                isEnabled = true
                background = resources.getDrawable(R.drawable.sign_up_success_button)
            }
        }else{
            btnSignIn.run {
                isEnabled = false
                background = resources.getDrawable(R.drawable.sign_up_success_non_button)
            }
        }
    }
    //이메일 양식 확
    private fun checkForm(): Boolean {
        val regex = "^[_a-zA-Z0-9-.]+@[.a-zA-Z0-9-]+\\.[a-zA-Z]+$"
        val p = Pattern.compile(regex)
        val m = p.matcher(etEmail.text.toString())
        return if(!m.matches()){
            Snackbar.make(window.decorView.rootView,"이메일 형식이 잘못되었습니다.", Snackbar.LENGTH_SHORT).show()
            true
        }else if(etEmail.text.toString().isEmpty()){
            Snackbar.make(window.decorView.rootView,"이메일을 입력해 주세요.", Snackbar.LENGTH_SHORT).show()
            false
        }else if(etEmail.text.toString().isEmpty()){
            Snackbar.make(window.decorView.rootView,"비밀번호를 입력해 주세요.", Snackbar.LENGTH_SHORT).show()
            false
        }else true
    }
    //로그인 완료
    private fun signInDone(email: String) {
        val intent = intent
        intent.putExtra(USER_ID, email)
        setResult(SIGN_IN_SUCCESS, intent)
        finish()
    }
    //로그인 버튼 클릭 이벤트
    private fun signInClick(){
        Util.progressVisible(true, progress, this)
        if(checkForm()) {
            val userEmail = etEmail.text.toString()
            val password = etPass.text.toString()
            Log.v(ViewSignIn::class.java.name, "SignInPresenter $userEmail, $password")
            AWSMobileClient.getInstance().signIn(userEmail, password, null, object : Callback<SignInResult> {
                override fun onResult(signInResult: SignInResult) {
                    runOnUiThread {
                        when (signInResult.signInState) {
                            SignInState.DONE -> {
                                Log.v(ViewSignIn::class.java.name, "SignInPresenter-in Done")
                                signInDone(userEmail)
                            }
                            SignInState.SMS_MFA -> Log.v(ViewSignIn::class.java.name, "SignInPresenter-in SMS MFA")
                            SignInState.NEW_PASSWORD_REQUIRED -> Log.v(ViewSignIn::class.java.name, "SignInPresenter-in NEW PASSWORD")
                            else -> Log.v(ViewSignIn::class.java.name, "SignInPresenter-in else ${signInResult.signInState}")
                        }
                        Util.progressVisible(false, progress, this@ViewSignIn)
                    }
                }
                override fun onError(e: Exception) {
                    runOnUiThread {
                        when {
                            e.toString().contains("User is not confirmed") -> {
                                val intent = Intent(applicationContext, ViewConfirm::class.java)
                                intent.putExtra(USER_ID,userEmail)
                                startActivity(intent)
                            }
                            e.toString().contains("Incorrect username or password") -> Snackbar.make(window.decorView.rootView, "아이디 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()
                            e.toString().contains("User does not exist.") -> { Snackbar.make(window.decorView.rootView, "가입되지 않은 회원입니다.", Toast.LENGTH_SHORT).show() }
                            e.toString().contains("Failed to authenticate user") -> {
                                //재접속
                                AWSMobileClient.getInstance().signIn(userEmail, password, null, object : Callback<SignInResult> {
                                    override fun onResult(result: SignInResult?) {
                                        when (result!!.signInState) {
                                            SignInState.DONE -> {
                                                Log.v(ViewSignIn::class.java.name, "SignInPresenter-in Done")
                                                signInDone(userEmail)
                                            }
                                            SignInState.SMS_MFA -> Log.v(ViewSignIn::class.java.name, "SignInPresenter-in SMS MFA")
                                            SignInState.NEW_PASSWORD_REQUIRED -> Log.v(ViewSignIn::class.java.name, "SignInPresenter-in NEW PASSWORD")
                                            else -> Log.v(ViewSignIn::class.java.name, "SignInPresenter-in else ${result.signInState}")
                                        }
                                        Util.progressVisible(false, progress, this@ViewSignIn)
                                    }
                                    override fun onError(e: java.lang.Exception?) {
                                        if(e.toString().contains("Failed to authenticate user"))
                                            Snackbar.make(window.decorView.rootView, "다시 시도해 주세요..", Toast.LENGTH_SHORT).show()
                                    }
                                })
                                AWSMobileClient.getInstance().signOut()}
                        }
                        Log.v(ViewSignIn::class.java.name, "SignInPresenter-in error", e)
                        Util.progressVisible(false, progress, this@ViewSignIn)
                    }
                }
            })
        }
        else Util.progressVisible(false, progress, this@ViewSignIn)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //회원가입하고 리절트 -> 바로 로그인페이지에서 인증페이지로 넘어가기
        if(requestCode == SIGN_IN_LET_UP){
            if(resultCode == SIGN_UP_SUCCESS) {
                val intent = intent
                intent.putExtra(USER_ID, data?.getStringExtra(USER_ID))
                intent.putExtra(USER_PASSWORD, data?.getStringExtra(USER_PASSWORD))
                setResult(SIGN_UP_SUCCESS, intent)
                finish()
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////
    private fun setInitialize() {
        //이메일 인증한 후 이메일 자동 세팅
        returnEmail =
            try { intent.getStringExtra(USER_ID) }
            catch (e: IllegalStateException){ "" }
    }
    private fun setView() {
        //백버튼 로그아웃
        btnBack.setOnClickListener {
            User.logOut()
            finish()
        }
        //인증완료 후 재 로그인시 이메일 세팅
        etEmail.setText(returnEmail)
        //비밀번호 찾기 버튼 클릭
        btnForgotPass.setOnClickListener { startActivity(Intent(this, ViewForgotPass::class.java)) }
        //회원가입 하러가기
        btnGoSignUp.setOnClickListener {
            val intent = Intent(this, ViewSignUp::class.java)
            intent.putExtra(USER_ID, etEmail.text.toString())
            startActivityForResult(intent, SIGN_IN_LET_UP)
        }
        //로그인 버튼 클릭
        btnSignIn.setOnClickListener { signInClick() }
    }
}
