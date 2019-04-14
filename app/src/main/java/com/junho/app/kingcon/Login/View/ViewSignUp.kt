package com.junho.app.kingcon.Login.View

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.results.SignUpResult
import com.junho.app.kingcon.Etc.StringData.COGNITO
import com.junho.app.kingcon.Etc.StringData.SIGN_UP_SUCCESS
import com.junho.app.kingcon.Etc.StringData.USER_ID
import com.junho.app.kingcon.Etc.StringData.USER_PASSWORD
import com.junho.app.kingcon.Etc.User
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.activity_view_sign_up.*
import java.util.regex.Pattern

@Suppress("DEPRECATION")
class ViewSignUp : AppCompatActivity() {
    private var imgCheck: Drawable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_sign_up)

        initialize()
        setView()
    }
    //가입 양식 체크
    private fun checkForm(): Boolean {
        val regex = "^[_a-zA-Z0-9-.]+@[.a-zA-Z0-9-]+\\.[a-zA-Z]+$"
        val p = Pattern.compile(regex)
        val m = p.matcher(etEmail.text.toString())
        Util.progressVisible(false, progress, this@ViewSignUp)
        //이메일 형식 틀릴 시
        return if(etEmail.text.toString().isEmpty()){
            Snackbar.make(window.decorView.rootView, "사용할 이메일을 입력해 주세요.", Snackbar.LENGTH_SHORT).show()
            false
        } else if(etName.text.toString().isEmpty()){
            Snackbar.make(window.decorView.rootView, "사용할 이름을 입력해 주세요.", Snackbar.LENGTH_SHORT).show()
            false
        } else if(etPass.text.toString().isEmpty()){
            Snackbar.make(window.decorView.rootView, "사용할 비밀번호를 입력해 주세요.", Snackbar.LENGTH_SHORT).show()
            false
        } else if(!m.matches()) {
            Snackbar.make(window.decorView.rootView, "이메일 형식이 잘못되었습니다.", Snackbar.LENGTH_SHORT).show()
            false
        } else if(etName.text.toString().length<2) {
            Snackbar.make(window.decorView.rootView,"이름 길이가 짧습니다.",Snackbar.LENGTH_SHORT).show()
            false
        } else if(etPass.text.toString().length<6){
            Snackbar.make(window.decorView.rootView,"비밀번호 길이가 짧습니다. (6자 이상)",Snackbar.LENGTH_SHORT).show()
            false
        } else if(etPass.text.toString() != etPassRe.text.toString()) {
            Snackbar.make(window.decorView.rootView,"비밀번호가 다릅니다.",Snackbar.LENGTH_SHORT).show()
            false
        } else if(!cbSignUp.isChecked) {
            Snackbar.make(window.decorView.rootView,"약관 동의가 필요합니다.",Snackbar.LENGTH_SHORT).show()
            false
        }else true
    }
    //가입 완료
    private fun signUpDone(userEmail: String, userPass: String){
        val intent = intent
        intent.putExtra(USER_ID,userEmail)
        intent.putExtra(USER_PASSWORD,userPass)
        setResult(SIGN_UP_SUCCESS, intent)
        finish()
    }
    ///////////////////////////////////////////////
    private fun initialize(){
        imgCheck = ContextCompat.getDrawable(this, R.drawable.ic_check)
        imgCheck?.setBounds(0,0, imgCheck!!.minimumWidth, imgCheck!!.minimumHeight)
    }
    private fun setView(){
        //백버튼
        btnBack.setOnClickListener { finish() }
        //가입 버튼 클릭
        btnSignUp.setOnClickListener {
            if(checkForm()) {
                Util.progressVisible(true, progress, this@ViewSignUp)
                val userEmail = etEmail.text.toString()
                val userName = etName.text.toString()
                val password = etPass.text.toString()
                val attributes = HashMap<String,String>()
                //속성
                attributes["email"] = userEmail
                attributes["name"] = userName
                User.id = userEmail
                User.name = userName
                User.picture = false
                User.provider = COGNITO
                User.localUserDataUpdate()
                AWSMobileClient.getInstance().signUp(userEmail, password, attributes, null, object : Callback<SignUpResult> {
                        override fun onResult(signUpResult: SignUpResult) {
                            runOnUiThread {
                                if (!signUpResult.confirmationState) {
                                    //인증 페이지 넘어가기
                                    signUpDone(userEmail, password)
                                } else {
                                    Snackbar.make(window.decorView.rootView, "회원가입이 완료되었습니다!", Snackbar.LENGTH_SHORT)
                                        .show()
                                }
                                Util.progressVisible(false, progress, this@ViewSignUp)
                            }
                        }
                        override fun onError(e: Exception) {
                            runOnUiThread {
                                Log.v("SignUpError: ", e.toString())
                                when {
                                    e.toString().contains("User already exists") -> {
                                        Snackbar.make(
                                            window.decorView.rootView,
                                            "이미 가입된 이메일입니다.",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                    e.toString().contains("Member must have length greater than or equal to 6") -> {
                                        Snackbar.make(
                                            window.decorView.rootView,
                                            "비밀번호 길이가 짧습니다.",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                    e.toString().contains("Invalid email address format.") -> {
                                        Snackbar.make(
                                            window.decorView.rootView,
                                            "이메일 형식이 잘못되었습니다.",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                    e.toString().contains("Password did not conform with policy") -> {
                                        Snackbar.make(
                                            window.decorView.rootView,
                                            "비밀번호에 숫자를 포함해 주세요.",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                    e.toString().contains("An account with the given email already exists.") -> {
                                        Snackbar.make(
                                            window.decorView.rootView,
                                            "이미 가입된 이메일입니다.",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                Util.progressVisible(false, progress, this@ViewSignUp)
                            }
                        }
                    })
            }
        }
    }
}
