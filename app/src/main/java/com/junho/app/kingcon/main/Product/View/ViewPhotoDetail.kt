package com.junho.app.kingcon.main.Product.View

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_view_photo_detail)

        val img = intent.getStringExtra(INTENT_PRODUCT_PICTURE)

        Util.getPostPicture(this, img, userProfileView)
    }
}