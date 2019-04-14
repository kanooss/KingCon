package com.junho.app.kingcon.Main.Product.View

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.junho.app.kingcon.Etc.StringData.INTENT_PRODUCT_PICTURE
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.activity_view_photo_detail.*

class ViewPhotoDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_view_photo_detail)

        Util.getPostPicture(this, intent.getStringExtra(INTENT_PRODUCT_PICTURE), userProfileView)
    }
}