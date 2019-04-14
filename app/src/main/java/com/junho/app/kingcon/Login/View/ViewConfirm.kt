package com.junho.app.kingcon.Login.View

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.results.SignInResult
import com.amazonaws.mobile.client.results.SignUpResult
import com.junho.app.kingcon.Etc.StringData.CONFIRM_SUCCESS
import com.junho.app.kingcon.Etc.StringData.SIGN_IN_SUCCESS
import com.junho.app.kingcon.Etc.StringData.SIGN_IN_SUCCESS_FOR_CONFIRM
import com.junho.app.kingcon.Etc.StringData.USER_ID
import com.junho.app.kingcon.Etc.StringData.USER_PASSWORD
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.activity_view_confirm.*
import java.lang.NullPointerException

class ViewConfirm : AppCompatActivity() {
    private var userEmail = ""
    private var userPass = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_confirm)

        initialize()
        setView()
    }
    //인증완료 및 로그인
    private fun signInDone(result: Int) {
        val intent = intent
        intent.putExtra(USER_ID, userEmail)
        intent.putExtra(USER_PASSWORD, userPass)
        setResult(result, intent)
        finish()
    }
    private fun initialize() {
        userEmail = intent.getStringExtra(USER_ID)
        userPass =
            try { intent.getStringExtra(USER_PASSWORD) }
            catch (e: NullPointerException) { "" }
    }
    @SuppressLint("SetTextI18n")
    private fun setView() {
        btnBack.setOnClickListener {
            finish()
        }
        //인증 설명
        tvExplanation.text = "$userEmail 로 인증코드 메일을 보냈습니다."
        tvIdGuideLabel.text = "인증코드를 입력해 주세요."
        //인증 버튼
        btnConfirm.setOnClickListener {
            Util.progressVisible(true, progress, this)
            AWSMobileClient.getInstance().confirmSignUp(userEmail, tvConfirm.text.toString(), object : Callback<SignUpResult> {
                override fun onResult(signUpResult: SignUpResult) {
                    runOnUiThread {
                        if (!signUpResult.confirmationState) {
                            val details = signUpResult.userCodeDeliveryDetails
                            Toast.makeText(applicationContext,"Confirm sign-up with: " + details.destination, Toast.LENGTH_SHORT).show()
                            signInDone(CONFIRM_SUCCESS)
                            runOnUiThread { Util.progressVisible(true, progress, this@ViewConfirm) }
                        } else {
                            Snackbar.make(window.decorView.rootView,"회원가입이 완료되었습니다!", Snackbar.LENGTH_SHORT).show()
                            if(userPass != "") signInDone(SIGN_IN_SUCCESS_FOR_CONFIRM) //바로 로그인 ㄲ
                            runOnUiThread { Util.progressVisible(true, progress, this@ViewConfirm) }
                        }
                    }
                }
                override fun onError(e: Exception) {
                    Log.e("confirm_error",e.toString())
                    runOnUiThread {
                        if(tvConfirm.text.toString().isEmpty())
                            Snackbar.make(window.decorView.rootView,"인증번호를 입력해 주세요.", Snackbar.LENGTH_SHORT).show()
                        else
                            Snackbar.make(window.decorView.rootView,"인증번호가 틀렸습니다.", Snackbar.LENGTH_SHORT).show()
                        Util.progressVisible(true, progress, this@ViewConfirm)
                    }
                }
            })
        }
    }
}
