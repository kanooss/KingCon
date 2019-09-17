package com.junho.app.kingcon.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.junho.app.kingcon.Etc.AWSDB
import com.junho.app.kingcon.Etc.AWSS3
import com.junho.app.kingcon.Etc.StringData
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.R
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import kotlinx.android.synthetic.main.activity_create_post.*

class CreatePost : AppCompatActivity() {
    private var path = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        setView()
    }
    //정보 세팅
    private fun infoSet(): HashMap<String, String> {
        val info = hashMapOf<String, String>()
        info["Coun"] = tvCountry.text.toString()
        info["Ingr"] = tvIngredient.text.toString()
        info["Char"] = tvCharacteristic.text.toString()
        return info
    }
    //태그 세팅
    private fun tagSet(): ArrayList<String> {
        val tag = arrayListOf<String>()
        if(etTag1.text.toString() != "")
            tag.add(etTag1.text.toString())
        if(etTag2.text.toString() != "")
            tag.add(etTag2.text.toString())
        if(etTag3.text.toString() != "")
            tag.add(etTag3.text.toString())
        if(etTag4.text.toString() != "")
            tag.add(etTag4.text.toString())
        if(etTag5.text.toString() != "")
            tag.add(etTag5.text.toString())
        if(etTag6.text.toString() != "")
            tag.add(etTag6.text.toString())
        return tag
    }

    //사진 선택시 이미지뷰 사진 적용
    private fun setImageView(path: String){
        Glide.with(this)
            .load(path)
            .into(ivPicture)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            path = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)[0].path
            //gif아닌지 확인
            setImageView(path)
//            if(images[images.length-1]!='f')
//            else Toast.makeText(applicationContext,"gif", Toast.LENGTH_SHORT).showDeleteDialog()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun setView(){
        val typeArr = arrayListOf(
            StringData.CONDOM_TYPE,
            StringData.GEL_TYPE
        )
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeArr)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter
        //제품 사진 추가
        ivPicture.setOnClickListener {
            Util.gallerySelect(this)
        }
        //제품 목록 생성
        btnCreate.setOnClickListener {
            val tag = tagSet()
            val info = infoSet()
            val id = etId.text.toString()
            val product = ProductData(spinnerType.selectedItem.toString(), id, etName.text.toString(), path, etCompany.text.toString(),
                tag,0f, 0f, 0, 0, 0, arrayListOf(0,0,0,0,0,0,0,0,0,0), arrayListOf(0,0,0,0,0,0,0,0,0,0),
                arrayListOf(0,0,0,0,0,0,0,0,0,0), 0, 0 ,
                info)
            AWSS3.uploadPicture(this, id, path)
            AWSDB.createProduct(this, product)
        }
    }

}
