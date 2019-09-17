package com.junho.app.kingcon.Login.View

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.RadioGroup
import com.amazonaws.mobile.client.AWSMobileClient
import com.facebook.login.LoginManager
import com.junho.app.kingcon.Etc.StringData
import com.junho.app.kingcon.Etc.StringData.DONE_DATA_SET
import com.junho.app.kingcon.Etc.StringData.FACEBOOK
import com.junho.app.kingcon.Etc.StringData.USER_BIRTH
import com.junho.app.kingcon.Etc.StringData.USER_GENDER
import com.junho.app.kingcon.Etc.User
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.activity_view_set_info.*
import java.util.*

class ViewSetInfo : AppCompatActivity() {

    private var boolDatePicker = true
    private var bBirth = false
    private var bGender = false
    private var gender = false
    private val c = Calendar.getInstance()
    private var userBirthYear = c.get(Calendar.YEAR)-19
    private var userBirthMonth = c.get(Calendar.MONTH)
    private var userBirthDay = c.get(Calendar.DAY_OF_MONTH)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_set_info)
        initialize()
        setView()
    }
    //로그아웃
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("로그아웃")
        builder.setMessage("로그아웃 하겠습니까?")
        builder.setPositiveButton("예") { dialog, which ->
            AWSMobileClient.getInstance().signOut()
            if(User.provider == FACEBOOK)
                LoginManager.getInstance().logOut()
            finish()
        }
        builder.setNegativeButton("아니오") { _, _ -> }
        builder.show()
    }
    ///////////////////////////////////////
    private fun initialize() {
    }
    @SuppressLint("SetTextI18n")
    private fun setView() {
        //백버튼
//        btnBack.setOnClickListener { onBackPressed() }
        //생일 에디트텍스트 클릭
        etBirth.setOnClickListener {
            if (boolDatePicker) {
                boolDatePicker = false
                val dialog = DatePickerDialog(
                    this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        //데이트피커 셋 이벤트
                        userBirthYear = year
                        userBirthMonth = monthOfYear
                        userBirthDay = dayOfMonth
                        c.set(year, monthOfYear, dayOfMonth)
                        etBirth.setText("$userBirthYear. ${userBirthMonth + 1}. $userBirthDay")
                        bBirth = true
                    },
                    userBirthYear, userBirthMonth, userBirthDay
                )
                dialog.datePicker.calendarViewShown = false
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.datePicker.maxDate = c.timeInMillis
                dialog.show()
                val mHandler = @SuppressLint("HandlerLeak")
                object : Handler() { override fun handleMessage(msg: Message) { boolDatePicker = true } }
                mHandler.sendEmptyMessageDelayed(0, 500)
            }
        }
        //체크박스 선택
        cbGroup.setOnCheckedChangeListener{ _: RadioGroup, i: Int ->
            bGender = true
            if(i == cbFemale.id)
                gender = true
            else if(i == cbMale.id)
                gender = false
        }
        //다음 버튼 클릭
        btnDone.setOnClickListener {
            //성별 선택 안됨
            if(!bGender)
                Snackbar.make(window.decorView.rootView, "성별을 선택해 주세요.", Snackbar.LENGTH_SHORT).show()
            //생일 선택 안됨
            else if(!bBirth)
                Snackbar.make(window.decorView.rootView, "생일을 선택해 주세요.", Snackbar.LENGTH_SHORT).show()
            else {
                val intent = intent.run {
                    putExtra(USER_GENDER, gender)
                    putExtra(USER_BIRTH, c.timeInMillis)
                }
                setResult(DONE_DATA_SET, intent)
                finish()
            }
        }
    }
}
