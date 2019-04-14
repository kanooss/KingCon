package com.junho.app.kingcon.Main.Product.View

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.junho.app.kingcon.Adapter.ProductTagListAdapter
import com.junho.app.kingcon.Adapter.ReviewAdapter
import com.junho.app.kingcon.Dialog.RatingDialog
import com.junho.app.kingcon.Etc.AWSDB
import com.junho.app.kingcon.Etc.CallBack.CallbackAdapter
import com.junho.app.kingcon.Etc.CallBack.CallbackDialog
import com.junho.app.kingcon.Etc.Graph.DayAxisValueFormatter
import com.junho.app.kingcon.Etc.Graph.ValueFormatter
import com.junho.app.kingcon.Etc.StringData
import com.junho.app.kingcon.Etc.StringData.HOME
import com.junho.app.kingcon.Etc.StringData.INTENT_PRODUCT_PICTURE
import com.junho.app.kingcon.Etc.StringData.PRODUCT_DATA
import com.junho.app.kingcon.Etc.StringData.REVIEW_CREATE
import com.junho.app.kingcon.Etc.StringData.REVIEW_CREATE_DONE
import com.junho.app.kingcon.Etc.User
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.Item.ReviewData
import com.junho.app.kingcon.Item.UserProfileData
import com.junho.app.kingcon.Main.Review.ViewReviewDetailFragment
import com.junho.app.kingcon.Main.Review.ViewReviewMoreFragment
import com.junho.app.kingcon.Main.ViewMain
import com.junho.app.kingcon.R
import com.orhanobut.dialogplus.DialogPlus
import kotlinx.android.synthetic.main.fragment_view_product_info.view.*
import kotlin.concurrent.thread

class ViewProductInfoFragment : Fragment(), CallbackAdapter.Tag, CallbackAdapter.Review, CallbackDialog.Rating {
    private val argProductData = "productData"
    private lateinit var productData: ProductData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productData = it.getSerializable(argProductData) as ProductData
        }
    }
    private lateinit var adapterTag: ProductTagListAdapter
    private lateinit var adapterBestReview: ReviewAdapter
    private lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_view_product_info, container, false)

        setInitialize()
        setView()

        return rootView
    }

    fun changeReviewData(reviewData: ReviewData){
        ThreadUtils.runOnUiThread {
            productData.updateReview(reviewData)
            setViewData()
            graphSetting()
            myActSetting()
            adapterBestReview.notifyDataSetChanged()
        }
    }
    /** 별점 그래프 옵션 설정 **/
    private fun graphSetting() {
        rootView.graphRating.apply {
            legend.isEnabled = false
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            description?.isEnabled = false
            setMaxVisibleValueCount(60)
            setPinchZoom(false)
            setScaleEnabled(false)
            isDoubleTapToZoomEnabled = false // 더블탭 줌
            setDrawGridBackground(false)
            axisRight?.isEnabled = false // 우측 바
            axisLeft?.isEnabled = false // 좌측 바
            isHighlightPerTapEnabled = false //클릭시 하이라이트
            isHighlightPerDragEnabled = false // 드래그시 하이라이트
        }
        val charTextSize = 10f
        val xAxisFormatter = DayAxisValueFormatter()
        val xAxis = rootView.graphRating.xAxis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawAxisLine(false)
            granularity = 1f // only int
            valueFormatter = xAxisFormatter
            textColor = Color.GRAY
            textSize = charTextSize
            setDrawLabels(true)
            labelCount = 10
        }
        val yAxisL  = rootView.graphRating.axisLeft //y축 좌측
        yAxisL.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
            setDrawLabels(false)
        }
        val yAxisR  = rootView.graphRating.axisRight //y축 우측
        yAxisR.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
            setDrawLabels(false)
        }
        rootView.graphRating.setFitBars(true)
        rootView.graphRating.animateY(0) // 그래프 에니메이션 시간 (안하면 바로 안뜸)
        rootView.graphRating.visibility = View.VISIBLE
//        rootView.graphProgressBar.visibility = View.GONE  // 프로그래스
        setGraphData(10,rootView.graphRating, charTextSize, productData.graph)
    }
    private fun setGraphData(count: Int, home_photo_Chart: BarChart, charTextSize: Float, ratingArray: ArrayList<Int>) {
        val yVals1 = java.util.ArrayList<BarEntry>()
        var max = 0
        ratingArray.forEach {//최대 값
            if(it>max) max = it
        }
        for( i in 0 until count){
            if(ratingArray[i] == 0 && max!=0)
                yVals1.add(BarEntry(i.toFloat(), max.toFloat() / 50))
            else
                yVals1.add(BarEntry(i.toFloat(), ratingArray[i].toFloat()))
        }

        val set1 = BarDataSet(yVals1,null)
        set1.valueFormatter = ValueFormatter()
        set1.setDrawIcons(false)
        set1.setColors(Color.argb(255,0x52,0xb3,0xd9)) // 그래프 바 색
        set1.valueTextColor = Color.WHITE

        val dataSets = java.util.ArrayList<IBarDataSet>()
        dataSets.add(set1)

        val data = BarData(dataSets)
        data.setValueTextSize(charTextSize)
        data.barWidth = 0.8f

        home_photo_Chart.data = data
    }
    /** 제품 태그 클릭 -> 태그 검색창 **/
    override fun tagClick(title: String, position: Int) {

    }
    /** 리뷰 댓글 클릭, 자세히 보기 **/
    override fun onReviewDetail(reviewData: ReviewData, position: Int) {
        (activity as ViewMain).reviewDetailFragment = ViewReviewDetailFragment.newInstance(reviewData, productData)
        Util.fragmentChanger((activity as ViewMain),(activity as ViewMain).reviewDetailFragment, HOME)
    }
    /** 리뷰 좋아요 **/
    override fun onLikeClick(reviewData: ReviewData, position: Int) {
        thread {
            AWSDB.updateReviewLike(reviewData)
            if(position <= 3)
                ThreadUtils.runOnUiThread { adapterBestReview.notifyItemChanged(position) }
            else
                ThreadUtils.runOnUiThread {
                    //내댓글 좋아요 댓글 표시 할때 쓰기
//                    rootView.tvLikeNum.text = productData.myReview.like.toString()       //좋아요 개수
//                    if(productData.myReview.reviewLike)
//                        Glide.with(this)
//                            .load(R.drawable.ic_like_ok)
//                            .into(rootView.bntLike)
//                    else
//                        Glide.with(this)
//                            .load(R.drawable.ic_like)
//                            .into(rootView.bntLike)
                }
        }
    }
    /** 리뷰 신고 **/
    override fun optionDeclaration() {
    }
    /** 리뷰 수정 **/
    override fun optionReviewCustom(reviewData: ReviewData) {
        val intent = Intent(rootView.context, ViewCreateReview::class.java)
        intent.putExtra(PRODUCT_DATA,productData)
        startActivityForResult(intent, REVIEW_CREATE)
    }
    /** 리뷰 삭제 */
    override fun optionReviewDelete(reviewData: ReviewData) {
        val builder = AlertDialog.Builder(rootView.context)
        builder.setTitle("리뷰 삭제")
        builder.setMessage("리뷰를 삭제하시겠습니까?")
        builder.setPositiveButton("삭제") { _, _ ->
            thread {
                AWSDB.deleteReview(reviewData, productData)
                ThreadUtils.runOnUiThread {
                    Glide.with(this)
                        .load(R.drawable.ic_review)
                        .into(rootView.ivReview)
                    rootView.tvReview.visibility = View.VISIBLE
                    rootView.layerUserComment.collapse()
                }
                productData.myReview.removeReview()
                val index = productData.checkInBestReview(productData.myReview)
                if(index != -1)
                    ThreadUtils.runOnUiThread {
                        productData.bestReview.removeAt(index)
                        adapterBestReview.notifyDataSetChanged()
                    }
            }
        }
        builder.setNegativeButton("취소") { _, _ -> }
        builder.show()
    }
    /** 평가 변경 및 평가 **/
    override fun onRating(point: Float) {
        if(productData.myReview.point != point ) {
            thread {
                ThreadUtils.runOnUiThread { dialog.dismiss()}
                AWSDB.createRating(productData, point)
                productData.myReview.point = point
                transactionBool = true
                ThreadUtils.runOnUiThread { myRatingSetting() }
            }
        }
    }
    /** 기본 뷰 세팅 **/
    private fun setViewData(){
        //제품 이름
        rootView.tvProductName.text = productData.name
        //제품 이름
        rootView.tvProductCompany.text = productData.company
        //제품 이름
        rootView.tvProductName.text = productData.name
        //제품 태그
        rootView.tvTag.adapter = adapterTag
        rootView.tvTag.layoutManager = LinearLayoutManager(rootView.context, LinearLayoutManager.HORIZONTAL, false)
        //별점 바
        rootView.ratingBar.rating = productData.point
        //제품 평점
        val point = "${getString(R.string.ratingComment1)} ${String.format("%.2f", productData.point)} (${productData.rating}${getString(R.string.ratingComment2)}"
        rootView.tvRatingPoint.text = point
        //그래프 평점
        rootView.tvGraphRatingPoint.text = point
    }
    /** 점수 **/
    private fun myRatingSetting(){
        if(productData.myReview.point != 0f){ //평가함
            rootView.tvMyRatingPoint.visibility = View.VISIBLE
            rootView.tvMyRatingPoint.text = productData.myReview.point.toString()
            rootView.ivRating.visibility = View.GONE
            rootView.tvRating.visibility = View.GONE
        }else{
            rootView.tvMyRatingPoint.visibility = View.GONE
            rootView.ivRating.visibility = View.VISIBLE
            rootView.tvRating.visibility = View.VISIBLE
        }
    }
    /** 위시 **/
    private fun myWishSetting(){
        if(productData.myReview.wish){  //위시 등록
            Glide.with(this)
                .load(R.drawable.ic_wish_ok)
                .into(rootView.ivWish)
            rootView.tvWish.visibility = View.GONE
        } else{
            Glide.with(this)
                .load(R.drawable.ic_wish)
                .into(rootView.ivWish)
            rootView.tvWish.visibility = View.VISIBLE
        }
    }
    /** 내 활동 세팅 **/
    @SuppressLint("SetTextI18n")
    private fun myActSetting() {
        myRatingSetting()   //점수 세팅
        myWishSetting()     //위시 세팅
        if(productData.myReview.content != ""){ //리뷰남김
            Glide.with(this)
                .load(R.drawable.ic_review_ok)
                .into(rootView.ivReview)
            rootView.tvReview.visibility = View.GONE
            rootView.layerUserComment.expand()
            Util.getPictureUri(rootView.context, rootView.ivUserCommentProfile, User)     //프사
            rootView.tvUserComment.text = productData.myReview.content           //리뷰 내용
            rootView.tvReviewRemove.setOnClickListener { optionReviewDelete(productData.myReview) } //리뷰 삭제
            rootView.tvReviewCustom.setOnClickListener { optionReviewCustom(productData.myReview) } //리뷰 수정
        }else{
            Glide.with(this)
                .load(R.drawable.ic_review)
                .into(rootView.ivReview)
            rootView.tvReview.visibility = View.VISIBLE
            rootView.layerUserComment.collapse()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //리뷰 생성
        if(requestCode == REVIEW_CREATE  && resultCode == REVIEW_CREATE_DONE){
            productData = data?.getSerializableExtra(PRODUCT_DATA) as ProductData
            val index = productData.checkInBestReview(productData.myReview)
            if(productData.bestReview.size<2 && index == -1)
                productData.bestReview.add(productData.myReview)
            if(index != -1)
                productData.bestReview[index] = productData.myReview
            setViewData()
            graphSetting()
            myActSetting()
            adapterBestReview.notifyDataSetChanged()
        }
    }
    /** 베댓 3개 표시 **/
    private fun bestReviewSetting() {
        rootView.rvReview.adapter = adapterBestReview
        rootView.rvReview.layoutManager = LinearLayoutManager(rootView.context, LinearLayoutManager.VERTICAL, false)
    }
    /** ///////////////////////////////////////////////////////////////////////////////////////////////////////// **/
    private fun setInitialize(){
        rootView.rotateLoading.start()
        adapterTag = ProductTagListAdapter(rootView.context, productData.tag)
        adapterBestReview = ReviewAdapter(rootView.context, productData.bestReview, this)
        thread {
            if(productData.info["Coun"] ?: "" == "")
                AWSDB.readProductInfo(productData)   //제품 정보
            AWSDB.readProductAct(productData)        //유저 제품 평가 활동 정보
            AWSDB.readProductBestReview(productData) //베스트 리뷰 3개
            ThreadUtils.runOnUiThread {
                //제품 정보
                rootView.tvCountry.text = productData.info["Coun"] ?: ""
                rootView.tvIngredient.text = productData.info["Ingr"] ?: ""
                rootView.tvCharacteristic.text = productData.info["Char"] ?: ""
                graphSetting()//그래프
                myActSetting()//내 활동 정보 세팅
                bestReviewSetting()//베스트 리뷰 세팅
                rootView.rotateLoading.stop()
            }
        }
    }
    private var transactionBool = true
    private lateinit var dialog: DialogPlus
    private fun setView(){

        //제품 사진
        Util.getPostPicture(rootView.context, productData.id, rootView.ivProduct)
        Glide.with(rootView.context)
            .load(StringData.AWS_S3_PRODUTC_PICTURE(productData.id))
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions().centerCrop())
            .thumbnail(0.1f)
            .into(rootView.prodImageSecondary)
        rootView.ivProduct.setOnClickListener {
            val intent = Intent(context, ViewPhotoDetail::class.java)
            intent.putExtra(INTENT_PRODUCT_PICTURE,productData.id)
            startActivity(intent)
        }
        //기본 뷰 세팅
        setViewData()
        //평가하기 버튼
        rootView.backgroundRating.setOnClickListener {
            if(transactionBool) {
                val adapter = RatingDialog(rootView.context, productData, this)
                dialog = DialogPlus.newDialog(rootView.context)
                    .setAdapter(adapter)
                    .setExpanded(false, 600)
                    .setOnCancelListener {
                        transactionBool = true
                    } //다이얼로그 외부 클릭시 종료
                    .create()
                dialog.show()
                transactionBool = false
            }
        }
        //위시리스트 버튼
        rootView.backgroundWish.setOnClickListener {
            thread {
                AWSDB.createWish(productData)
                ThreadUtils.runOnUiThread { myWishSetting() }
            }
        }
        //리뷰남기기 버튼
        rootView.backgroundReview.setOnClickListener {
            val intent = Intent(rootView.context, ViewCreateReview::class.java)
            intent.putExtra(PRODUCT_DATA,productData)
            startActivityForResult(intent, REVIEW_CREATE)
        }
        //구매하기 버튼
        val buy = View.OnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://coupa.ng/bgWoai"))
            startActivity(intent)
        }
        rootView.tvBuy.setOnClickListener (buy)     // 쿠팡연결
        rootView.btnBuy.setOnClickListener (buy)    // 쿠팡연결
        rootView.ivCoupang.setOnClickListener (buy) // 쿠팡연결
        //공유하기 버튼
        val share = View.OnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "최저가! 지금 구매하면 내일 아침7시전 도착!\n")
            intent.putExtra(Intent.EXTRA_TEXT, "https://coupa.ng/bgWoai")
            val chooser = Intent.createChooser(intent, "친구에게 공유해주기!")
            startActivity(chooser)
        }
        rootView.ivShare.setOnClickListener(share)  //공유
        rootView.tvShare.setOnClickListener(share)  //공유
        rootView.btnShare.setOnClickListener(share) //공유
        //내 댓글 자세히보기 ㄲ
        rootView.tvUserComment.setOnClickListener {
            productData.myReview.userData = UserProfileData(User)
            (activity as ViewMain).reviewDetailFragment = ViewReviewDetailFragment.newInstance(productData.myReview, productData)
            Util.fragmentChanger((activity as ViewMain),(activity as ViewMain).reviewDetailFragment, HOME)
        }
        //분포도 더보기
        rootView.tvGraphMore.setOnClickListener {
            (activity as ViewMain).graphFragment = ViewGraphFragment.newInstance(productData.graph, productData)
            Util.fragmentChanger((activity as ViewMain),(activity as ViewMain).graphFragment, HOME)
        }
        //제품 정보 더보기
        rootView.tvProductInfoMore.setOnClickListener {
            (activity as ViewMain).productInfoMoreFragment = ViewInfoMoreFragment.newInstance(productData)
            Util.fragmentChanger((activity as ViewMain),(activity as ViewMain).productInfoMoreFragment, HOME)
        }
        //제품 리뷰 더보기
        rootView.tvReviewMore.setOnClickListener {
            (activity as ViewMain).reviewMoreFragment = ViewReviewMoreFragment.newInstance(productData)
            Util.fragmentChanger((activity as ViewMain),(activity as ViewMain).reviewMoreFragment, HOME)
        }
    }
    companion object {
        @JvmStatic
        fun productInfo(productData: ProductData) =
            ViewProductInfoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(argProductData, productData)
                }
            }
    }
}