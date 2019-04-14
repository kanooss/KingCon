package com.junho.app.kingcon.Login.View

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.results.ForgotPasswordResult
import com.amazonaws.mobile.client.results.ForgotPasswordState
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.activity_view_forgot_pass.*


class ViewForgotPass : AppCompatActivity() {
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_forgot_pass)

        setView()
    }

    //비밀번호 및 인증번호 입력 폼 확인
    private fun checkFor(newPw: String, confirmCode: String): Boolean {
        when {
            newPw.length < 6 -> Snackbar.make(window.decorView.rootView, "비밀번호길이가 짧습니다. (6자 이상)", Snackbar.LENGTH_SHORT).show()
            confirmCode.length < 6 -> Snackbar.make(window.decorView.rootView, "인증번호가 틀렸습니다.", Snackbar.LENGTH_SHORT).show()
            else -> return true
        }
        Util.progressVisible(false, progress, this@ViewForgotPass)
        return false
    }
    ////////////////////////////////////
    private fun setView() {
        //백버튼
        btnBack.setOnClickListener { finish() }
        //이메일 패턴 체크
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val emailCh =
                    if(s!!.matches(emailPattern.toRegex()) && s.isNotEmpty()){
                        etEmail.setCompoundDrawables(null,null,resources.getDrawable(R.drawable.ic_check),null)
                        true
                    }else{
                        etEmail.setCompoundDrawables(null,null,null,null)
                        false
                    }
                if(emailCh){ //비밀번호 보내기 버튼 활성화
                    btnPassSend.isEnabled = true
                    btnPassSend.background = resources.getDrawable(R.drawable.sign_up_success_button) // 비밀번호 보내기 버튼 활성화 이미지
                }else{ //비밀번호 보내기 버튼 비활성화
                    btnPassSend.isEnabled = false
                    btnPassSend.background = resources.getDrawable(R.drawable.sign_up_success_non_button) //비밀번호 보내기 버튼 비활성화 이미지
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //임시 비밀번호 전송
        btnPassSend.setOnClickListener {
            Util.progressVisible(true, progress, this)
            //인증번호 받기
            if(btnPassSend.text.toString() != "비밀번호 변경") {
                AWSMobileClient.getInstance()
                    .forgotPassword(etEmail.text.toString(), object : Callback<ForgotPasswordResult> {
                        override fun onResult(result: ForgotPasswordResult) {
                            runOnUiThread {
                                Log.d("TAG!!@#", "forgot password state: " + result.state)
                                when (result.state) {
                                    ForgotPasswordState.CONFIRMATION_CODE -> {
                                        btnPassSend.text = "비밀번호 변경"
                                        etEmail.isEnabled = false
                                        layoutConfirm.visibility = View.VISIBLE
                                        Snackbar.make(window.decorView.rootView, "임시비밀번호가 메일로 발송되었습니다.", Snackbar.LENGTH_SHORT).show()
                                    }
                                    else -> Log.e("TAG!!@#", "un-supported forgot password state")
                                }
                                Util.progressVisible(false, progress, this@ViewForgotPass)
                            }
                        }
                        override fun onError(e: Exception) {
                            if(e.toString().contains("Username/client partition combination not found."))
                                Snackbar.make(window.decorView.rootView, "가입되지않은 이메일입니다.", Snackbar.LENGTH_SHORT).show()
                            else
                                Snackbar.make(window.decorView.rootView, "다시 시도해 주세요.", Snackbar.LENGTH_SHORT).show()
                            Util.progressVisible(false, progress, this@ViewForgotPass)
                        }
                    })
            }
            //비밀번호 변경
            else{
                val newPw = etNewPw.text.toString()
                val confirmCode = etConfirmCode.text.toString()
                if(checkFor(newPw, confirmCode))
                    AWSMobileClient.getInstance().confirmForgotPassword(newPw, confirmCode,
                        object : Callback<ForgotPasswordResult> {
                            override fun onResult(result: ForgotPasswordResult) {
                                runOnUiThread {
                                    when (result.state) {
                                        ForgotPasswordState.DONE -> {
                                            Snackbar.make(window.decorView.rootView, "비밀번호가 변경되었습니다.", Snackbar.LENGTH_SHORT).show()
                                        }
                                        else -> Log.e("TAG!!@#", "un-supported forgot password state")
                                    }
                                    Util.progressVisible(false, progress, this@ViewForgotPass)
                                }
                            }
                            override fun onError(e: Exception) {
                                if(e.toString().contains("Invalid verification code provided, please try again."))
                                    Snackbar.make(window.decorView.rootView, "인증번호가 틀렸습니다. 다시 확인해 주세요.", Snackbar.LENGTH_SHORT).show()
                                else
                                    Snackbar.make(window.decorView.rootView, "다시 확인해 주세요.", Snackbar.LENGTH_SHORT).show()
                                Log.e("TAG!!@#", "forgot password error", e)
                                Util.progressVisible(false, progress, this@ViewForgotPass)
                            }
                        })
            }
        }
    }
}
