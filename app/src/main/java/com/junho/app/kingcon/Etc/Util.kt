package com.junho.app.kingcon.Etc

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.instacart.library.truetime.TrueTime
import com.junho.app.kingcon.Etc.StringData.AWS_S3_PRODUTC_PICTURE
import com.junho.app.kingcon.Etc.StringData.AWS_S3_USER_PICTURE
import com.junho.app.kingcon.Etc.StringData.COLUMN
import com.junho.app.kingcon.Etc.StringData.HOME
import com.junho.app.kingcon.Etc.StringData.MY
import com.junho.app.kingcon.Etc.StringData.TAG
import com.junho.app.kingcon.Item.ReviewData
import com.junho.app.kingcon.Item.UserData
import com.junho.app.kingcon.Main.ViewMain
import com.junho.app.kingcon.R
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import com.victor.loading.rotate.RotateLoading
import kotlinx.android.synthetic.main.activity_view_main.*
import java.io.ByteArrayOutputStream
import java.util.*

object Util {
    fun progressVisible(visible: Boolean, progress: RotateLoading, view: FragmentActivity?){
        if(view != null)
            if(visible) {
                view.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                progress.start()
            }
            else {
                progress.stop()
                view.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
    }

    //키보드 visible
    fun isSoftKeyView(context: Context, view: EditText, visible: Int){
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        when(visible){
            VISIBLE->{
                imm?.showSoftInput(view, 1)
            }
            INVISIBLE->{
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }
    //등록 시간
    fun regTime(regTime: Long): String{
        val trueTime = getTrueTime()
        val postTime = Calendar.getInstance()
        postTime.timeInMillis = regTime
        val nowTime = Calendar.getInstance()
        nowTime.timeInMillis = trueTime.time
        val difYear = Math.abs(postTime[Calendar.YEAR] - nowTime[Calendar.YEAR])
        val difDate = Math.abs(postTime[Calendar.DATE] - nowTime[Calendar.DATE])
        val difHour = Math.abs(postTime[Calendar.HOUR] - nowTime[Calendar.HOUR])
        val difMin = Math.abs(postTime[Calendar.MINUTE] - nowTime[Calendar.MINUTE])
        return when {
            difYear >= 1 -> "${postTime.get(Calendar.YEAR)}. ${postTime.get(Calendar.MONTH) + 1}. ${postTime.get(Calendar.DAY_OF_MONTH)}"
            difDate > 10 -> "${postTime.get(Calendar.MONTH) + 1}월 ${postTime.get(Calendar.DAY_OF_MONTH)}일"
            difDate in 1..9 -> "${difDate}일전"
            difHour in 1 .. 23 -> "${Math.abs(postTime[Calendar.HOUR] - nowTime[Calendar.HOUR])}시간 전"
            difMin in 1 .. 59 -> "${Math.abs(postTime[Calendar.MINUTE] - nowTime[Calendar.MINUTE])}분 전"
            difMin == 0 -> "${Math.abs(postTime[Calendar.SECOND] - nowTime[Calendar.SECOND])}초 전"
            else ->     "??"
        }
    }
    fun getAge(t: Long): Int {
        val age = Calendar.getInstance()
        val now = Calendar.getInstance()
        age.timeInMillis = AppData.startTime.time
        now.timeInMillis = t
        age.add(Calendar.YEAR, -now[Calendar.YEAR])
        age.add(Calendar.MONTH, -now[Calendar.MONTH])
        age.add(Calendar.DAY_OF_MONTH, -now[Calendar.DAY_OF_MONTH])
        return age[Calendar.YEAR]
    }
    @JvmStatic//액티비티 사진찍기
    fun gallerySelect(activity: Activity){
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                val images = arrayListOf<Image>()
                ImagePicker.with(activity)                         //  Initialize ImagePicker with activity or fragment context
                    .setToolbarColor("#78aaff")         //  Toolbar color
                    .setStatusBarColor("#4188fe")       //  StatusBar color (works with SDK >= 21  )
                    .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
                    .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
                    .setProgressBarColor("#4CAF50")     //  ProgressBar color
                    .setBackgroundColor("#ffffff")      //  Background color
                    .setCameraOnly(false)               //  Camera mode
                    .setMultipleMode(false)              //  Select multiple images or single image
                    .setFolderMode(false)                //  Folder mode
                    .setShowCamera(true)                //  Show camera button
                    .setFolderTitle("앨범")           //  Folder title (works with FolderMode = true)
                    .setImageTitle("갤러리")         //  Image title (works with FolderMode = false)
                    .setDoneTitle("선택")               //  Done button title
                    .setLimitMessage("한개 선택")    // Selection limit message
                    .setMaxSize(1)                     //  Max images can be selected
                    .setSavePath(activity.getString(R.string.app_name))         //  Image capture folder name
                    .setSelectedImages(images)          //  Selected images
                    .setAlwaysShowDoneButton(true)      //  Set always showDeleteDialog done button in multiple mode
                    .setKeepScreenOn(true)              //  Keep screen on when selecting images
                    .start()                            //  Start ImagePicker
            }
            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {}
        }
        TedPermission.with(activity)
            .setPermissionListener(permissionListener)
            .setRationaleMessage("앨범에서 사진을 가져오기위해 권한이 필요합니다.")
            .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }
    fun getImageByte(imageView: ImageView) : ByteArray{
        val stream = ByteArrayOutputStream()
        (imageView.drawable as BitmapDrawable).bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
        return stream.toByteArray()
    }
    //유저 프사 가져오기
    fun <T>getPictureUri(context: Context, view: ImageView, userData: T) {
        val USER_PROFILE_REFRESH_TERM = (System.currentTimeMillis() / 1000 / 60 / 60 / 6).toString()
        val requestOptions = RequestOptions().circleCrop()
            .signature(ObjectKey(USER_PROFILE_REFRESH_TERM)) //유저 프로필 새로고침
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        val requestBuilder =
            Glide.with(context)
                .load(R.drawable.ic_user)
//                        .transition(DrawableTransitionOptions.withCrossFade()) TODO  TransitionDrawable 얘를 옮길 방법을 찾아봐야할듯
                .thumbnail(0.2f)
                .apply(requestOptions)
        val requestListener = object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean { return false }
            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                val stream = ByteArrayOutputStream()
                (resource as BitmapDrawable).bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
                when (userData) {
                    is UserData -> userData.imageByte = stream.toByteArray()
                    is User -> userData.imageByte = stream.toByteArray()
                    is ReviewData -> userData.imageByte = stream.toByteArray()
                }
                return false
            }

        }
        Glide.with(context)
            .load(
                if (userData is UserData) {
                    if (userData.imageByte == null) userData.imageByte
                    else AWS_S3_USER_PICTURE(userData.id)
                } else if (userData is User) {
                    if (userData.imageByte == null) userData.imageByte
                    else AWS_S3_USER_PICTURE(userData.id)
                } else if (userData is ReviewData) {
                    if (userData.imageByte == null) userData.imageByte
                    else AWS_S3_USER_PICTURE(userData.id)
                } else R.drawable.ic_user
            )
            .apply(requestOptions)
            .thumbnail(0.2f)
            .listener(requestListener)
            .error(requestBuilder)
            .into(view)
    }
    fun getPostPicture(context: Context, uri: String, view: ImageView){
        Glide.with(context)
            .load(AWS_S3_PRODUTC_PICTURE(uri))
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions().centerInside())
            .thumbnail(0.1f)
            .into(view)
    }
    fun getFloatText(float: Float) = String.format("%.2f", float)
    fun getTrueTime(): Date =
        if(TrueTime.isInitialized()) TrueTime.now()
        else Date()
    @JvmStatic
    fun fragmentChanger(activity:Activity,fragment : android.support.v4.app.Fragment){
        if(fragment.isAdded){
            (activity as ViewMain).supportFragmentManager.beginTransaction().hide(activity.currentHomeFragment!!).show(fragment).commit()
            activity.currentHomeFragment = fragment
        }
    }
    @JvmStatic
    fun fragmentChanger(activity:Activity, fragment : android.support.v4.app.Fragment, container : String){
        when(container){
            HOME->{
                (activity as ViewMain).supportFragmentManager.beginTransaction()
                    .hide(activity.currentHomeFragment!!).add(R.id.bottomNavPageContainerHome,fragment).commit()
                if(fragment == activity.reviewDetailFragment)
                    activity.bottomNavigation.visibility = View.GONE
                activity.beforeSearchResultFragment = activity.currentHomeFragment == activity.searchResultFragment
                activity.beforeProductInfoFragment = activity.currentHomeFragment == activity.productInfoFragment
                activity.currentHomeFragment = fragment
            }
            TAG->{
                (activity as ViewMain).supportFragmentManager.beginTransaction()
                    .hide(activity.currentTagFragment!!).add(R.id.bottomNavPageContainerTag,fragment).commit()
                activity.currentTagFragment = fragment
            }
            COLUMN->{
                (activity as ViewMain).supportFragmentManager.beginTransaction()
                    .hide(activity.currentColumnFragment!!).add(R.id.bottomNavPageContainerColumn,fragment).commit()
                activity.currentColumnFragment = fragment
            }
            MY->{
                (activity as ViewMain).supportFragmentManager.beginTransaction()
                    .hide(activity.currentMyFragment!!).add(R.id.bottomNavPageContainerMy,fragment).commit()
                activity.currentMyFragment = fragment
            }
        }
    }
}