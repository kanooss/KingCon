package com.junho.app.kingcon.main.Product.View

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.jakewharton.rxbinding3.view.clicks
import com.junho.app.kingcon.Adapter.ProductTagListAdapter
import com.junho.app.kingcon.Adapter.ReviewAdapter
import com.junho.app.kingcon.Dialog.RatingDialog
import com.junho.app.kingcon.Etc.*
import com.junho.app.kingcon.Etc.CallBack.CallbackAdapter
import com.junho.app.kingcon.Etc.CallBack.CallbackDialog
import com.junho.app.kingcon.Etc.Graph.DayAxisValueFormatter
import com.junho.app.kingcon.Etc.Graph.ValueFormatter
import com.junho.app.kingcon.Etc.StringData.HOME
import com.junho.app.kingcon.Etc.StringData.INTENT_PRODUCT_PICTURE
import com.junho.app.kingcon.Etc.StringData.PRODUCT_DATA
import com.junho.app.kingcon.Etc.StringData.REVIEW_CREATE
import com.junho.app.kingcon.Etc.StringData.REVIEW_CREATE_DONE
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.Item.ReviewData
import com.junho.app.kingcon.Item.UserData
import com.junho.app.kingcon.Item.UserProfileData
import com.junho.app.kingcon.R
import com.junho.app.kingcon.main.Review.ViewReviewDetailFragment
import com.junho.app.kingcon.main.Review.ViewReviewMoreFragment
import com.junho.app.kingcon.main.ViewMain
import com.orhanobut.dialogplus.DialogPlus
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_view_product_info.view.*
import kotlin.concurrent.thread

@SuppressLint("CheckResult")
class ViewProductInfoFragment : androidx.fragment.app.Fragment(), CallbackAdapter.Tag,
    CallbackAdapter.Review, CallbackDialog.Rating {
    private val argProductData = "mainProductData"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mainProduct = it.getSerializable(argProductData) as ProductData
        }
    }

    private lateinit var adapterTag: ProductTagListAdapter
    private lateinit var adapterBestReview: ReviewAdapter
    private lateinit var rootView: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_view_product_info, container, false)

        setInitialize()
        setView()

        return rootView
    }

    fun changeReviewData() {
        ThreadUtils.runOnUiThread {
            setViewData()
            graphSetting()
            myActSetting()
            adapterBestReview.notifyDataSetChanged()
        }
    }

    /** 별점 그래프 옵션 설정 **/
    private fun graphSetting() {
        rootView.graphRating.run {
            legend.isEnabled = false
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            description?.isEnabled = false
            setMaxVisibleValueCount(60)
            setPinchZoom(false)
            setScaleEnabled(false)
            isDoubleTapToZoomEnabled = false // 더블탭 줌
            setDrawGridBackground(false)
            isHighlightPerTapEnabled = false //클릭시 하이라이트
            isHighlightPerDragEnabled = false // 드래그시 하이라이트
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                granularity = 1f // only int
                valueFormatter = DayAxisValueFormatter()
                textColor = Color.GRAY
                textSize = 10f
                setDrawLabels(true)
                labelCount = 10
            }
            axisLeft.run {
                isEnabled = false // 좌측 바
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
            }
            axisRight.run {
                isEnabled = false // 우측 바
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
            }
            setFitBars(true)
            animateY(0) // 그래프 에니메이션 시간 (안하면 바로 안뜸)
            visibility = View.VISIBLE
            setGraphData(10, this, 10f, mainProduct.graph)
//        rootView.graphProgressBar.visibility = View.GONE  // 프로그래스
        }
    }

    private fun setGraphData(
        count: Int,
        home_photo_Chart: BarChart,
        charTextSize: Float,
        ratingArray: ArrayList<Int>
    ) {
        arrayListOf<BarEntry>().run {
            for (i in 0 until count) {
                if (ratingArray[i] == 0 && ratingArray.max() != 0)
                    this.add(BarEntry(i.toFloat(), ratingArray.max()!!.toFloat() / 50))
                else
                    this.add(BarEntry(i.toFloat(), ratingArray[i].toFloat()))
            }
            BarDataSet(this, null)
        }.run {
            valueFormatter = ValueFormatter()
            setDrawIcons(false)
            setColors(Color.argb(255, 0x52, 0xb3, 0xd9)) // 그래프 바 색
            valueTextColor = Color.WHITE
            BarData(arrayListOf<IBarDataSet>(this))
        }.run {
            setValueTextSize(charTextSize)
            barWidth = 0.8f
            home_photo_Chart.data = this
        }
    }

    /** 제품 태그 클릭 -> 태그 검색창 **/
    override fun tagClick(title: String, position: Int) {

    }

    /** 리뷰 댓글 클릭, 자세히 보기 **/
    override fun onReviewDetail(reviewData: ReviewData, position: Int) {
        reviewData.id = mainProduct.id
        (activity as ViewMain).reviewDetailFragment =
            ViewReviewDetailFragment.newInstance(reviewData)
        Util.fragmentChanger(
            (activity as ViewMain),
            (activity as ViewMain).reviewDetailFragment,
            HOME
        )
    }

    /** 리뷰 좋아요 **/
    override fun onLikeClick(reviewData: ReviewData, position: Int) {
        thread {
            AWSDB.updateReviewLike(reviewData)
            ThreadUtils.runOnUiThread { adapterBestReview.notifyItemChanged(position) }
        }
    }

    /** 리뷰 신고 **/
    override fun optionDeclaration() {
    }

    /** 리뷰 수정 **/
    override fun optionReviewCustom(reviewData: ReviewData) {
        Intent(rootView.context, ViewCreateReview::class.java).run {
            putExtra(PRODUCT_DATA, mainProduct)
            startActivityForResult(this, REVIEW_CREATE)
        }
    }

    /** 리뷰 삭제 */
    override fun optionReviewDelete(reviewData: ReviewData) {
        AlertDialog.Builder(rootView.context).run {
            setTitle("리뷰 삭제")
            setMessage("리뷰를 삭제하시겠습니까?")
            setPositiveButton("삭제") { _, _ ->
                thread {
                    AWSDB.deleteReview(reviewData, mainProduct)
                    ThreadUtils.runOnUiThread {
                        Glide.with(this@ViewProductInfoFragment)
                            .load(R.drawable.ic_review)
                            .into(rootView.ivReview)
                        rootView.tvReview.visibility = View.VISIBLE
                        rootView.layerUserComment.collapse()
                    }
                    mainProduct.myReview.removeReview()
                    val index = mainProduct.checkInBestReview(mainProduct.myReview)
                    if (index != -1)
                        ThreadUtils.runOnUiThread {
                            mainProduct.bestReview.removeAt(index)
                            adapterBestReview.notifyDataSetChanged()
                        }
                }
            }
            setNegativeButton("취소") { _, _ -> }
            show()
        }
    }

    /** 평가 변경 및 평가 **/
    override fun onRating(point: Float) {
        if (mainProduct.myReview.point != point) {
            thread {
                ThreadUtils.runOnUiThread { dialog.dismiss() }
                AWSDB.createRating1(mainProduct, point)
                mainProduct.myReview.point = point
                transactionBool = true
                ThreadUtils.runOnUiThread { myRatingSetting() }
            }
        }
    }

    /** 기본 뷰 세팅 **/
    private fun setViewData() {
        rootView.tvProductName.text = mainProduct.name
        rootView.tvProductCompany.text = mainProduct.company  //제품 회사
        rootView.tvProductName.text = mainProduct.name        //제품 이름
        //제품 태그
        rootView.tvTag.adapter = adapterTag
        rootView.tvTag.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        rootView.ratingBar.rating = mainProduct.point //별점 바

        Observable.just(
            "${getString(R.string.ratingComment1)} ${String.format(
                "%.2f",
                mainProduct.point
            )} (${mainProduct.rating}${getString(R.string.ratingComment2)}"
        ).subscribe {
            rootView.tvRatingPoint.text = it        //제품 평점
            rootView.tvGraphRatingPoint.text = it   //그래프 평점
        }
    }

    /** 점수 **/
    private fun myRatingSetting() {
        if (mainProduct.myReview.point != 0f) { //평가함
            rootView.tvMyRatingPoint.visibility = View.VISIBLE
            rootView.tvMyRatingPoint.text = mainProduct.myReview.point.toString()
            rootView.ivRating.visibility = View.GONE
            rootView.tvRating.visibility = View.GONE
        } else {
            rootView.tvMyRatingPoint.visibility = View.GONE
            rootView.ivRating.visibility = View.VISIBLE
            rootView.tvRating.visibility = View.VISIBLE
        }
    }

    /** 위시 **/
    private fun myWishSetting() {
        if (mainProduct.myReview.wish) {  //위시 등록
            Glide.with(this)
                .load(R.drawable.ic_wish_ok)
                .into(rootView.ivWish)
            rootView.tvWish.visibility = View.GONE
        } else {
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
        if (mainProduct.myReview.content != "") { //리뷰남김
            Glide.with(this)
                .load(R.drawable.ic_review_ok)
                .into(rootView.ivReview)
            rootView.tvReview.visibility = View.GONE
            rootView.layerUserComment.expand()
            Util.getPictureUri(rootView.context, rootView.ivUserCommentProfile, User)     //프사
            rootView.tvUserComment.text = mainProduct.myReview.content           //리뷰 내용
            rootView.tvReviewRemove.setOnClickListener { optionReviewDelete(mainProduct.myReview) } //리뷰 삭제
            rootView.tvReviewCustom.setOnClickListener { optionReviewCustom(mainProduct.myReview) } //리뷰 수정
        } else {
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
        if (requestCode == REVIEW_CREATE && resultCode == REVIEW_CREATE_DONE) {
            mainProduct = data?.getSerializableExtra(PRODUCT_DATA) as ProductData
            val index = mainProduct.checkInBestReview(mainProduct.myReview)
            if (mainProduct.bestReview.size < 2 && index == -1)
                mainProduct.bestReview.add(mainProduct.myReview)
            if (index != -1)
                mainProduct.bestReview[index] = mainProduct.myReview
            setViewData()
            graphSetting()
            myActSetting()
            adapterBestReview.notifyDataSetChanged()
        }
    }

    /** 베댓 3개 표시 **/
    private fun bestReviewSetting() {
        rootView.rvReview.adapter = adapterBestReview
        rootView.rvReview.layoutManager = LinearLayoutManager(
            rootView.context,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    /** ///////////////////////////////////////////////////////////////////////////////////////////////////////// **/
    private fun setInitialize() {
        adapterTag = ProductTagListAdapter(rootView.context, mainProduct.tag)
        adapterBestReview =
            ReviewAdapter(rootView.context, mainProduct.bestReview, this@ViewProductInfoFragment)
        //유저 제품 평가 활동 정보
        if (mainProduct.info["Coun"] ?: "" == "" && mainProduct.myReviewIsNull()) {
            rootView.rotateLoading.start()
            AWSDB.readProductAct(mainProduct)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe ({
                    try {
                        myActSetting()
                        rootView.tvCountry.text = mainProduct.info["Coun"] ?: ""
                        rootView.tvIngredient.text = mainProduct.info["Ingr"] ?: ""
                        rootView.tvCharacteristic.text = mainProduct.info["Char"] ?: ""
                        graphSetting()//그래프
                        rootView.rotateLoading.stop()
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    }
                }){
                    MyLog.e(it.toString())
                }
        } else {
            myActSetting()
            rootView.tvCountry.text = mainProduct.info["Coun"] ?: ""
            rootView.tvIngredient.text = mainProduct.info["Ingr"] ?: ""
            rootView.tvCharacteristic.text = mainProduct.info["Char"] ?: ""
            graphSetting()//그래프
            rootView.rotateLoading.stop()
        }
        //베스트 리뷰 3개
        AWSDB.readProductBestReview(mainProduct)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe ({
                bestReviewSetting()//베스트 리뷰 세팅
            },{
                MyLog.e(it.toString())
            })
    }

    private var transactionBool = true
    private lateinit var dialog: DialogPlus
    @SuppressLint("CheckResult")
    private fun setView() {
        //제품 사진
        Util.getPostPicture(rootView.context, mainProduct.id, rootView.ivProduct)

        Glide.with(rootView.context)
            .load(StringData.AWS_S3_PRODUTC_PICTURE(mainProduct.id))
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions().centerCrop())
            .thumbnail(0.1f)
            .into(rootView.prodImageSecondary)

        rootView.ivProduct.clicks()
            .map {
                val intent = Intent(context, ViewPhotoDetail::class.java)
                intent.putExtra(INTENT_PRODUCT_PICTURE, mainProduct.id)
            }
            .subscribe { startActivity(it) }

        rootView.ivProduct.clicks()
            .map {
                val intent = Intent(context, ViewPhotoDetail::class.java)
                intent.putExtra(INTENT_PRODUCT_PICTURE, mainProduct.id)
            }
            .subscribe { startActivity(it) }
        //기본 뷰 세팅
        setViewData()
        //평가하기 버튼
        rootView.backgroundRating.setOnClickListener {
            if (transactionBool) {
                val adapter = RatingDialog(rootView.context, mainProduct, this)
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
        var clickFlag = true
        rootView.backgroundWish.setOnClickListener {
            if (clickFlag) {
                clickFlag = false
                thread {
                    AWSDB.createWish(mainProduct)
                    ThreadUtils.runOnUiThread {
                        myWishSetting()
                    }
                    User.localUserDataUpdate()
                    clickFlag = true
                }
            }
        }
        //리뷰남기기 버튼
        rootView.backgroundReview.setOnClickListener {
            Intent(rootView.context, ViewCreateReview::class.java).run {
                putExtra(PRODUCT_DATA, mainProduct)
                startActivityForResult(this, REVIEW_CREATE)
            }
        }
        //구매하기 버튼
        val buy = View.OnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://coupa.ng/bgWoai")))
        }
        rootView.tvBuy.setOnClickListener(buy)     // 쿠팡연결
        rootView.btnBuy.setOnClickListener(buy)    // 쿠팡연결
        rootView.ivCoupang.setOnClickListener(buy) // 쿠팡연결
        //공유하기 버튼
        View.OnClickListener {
            Intent().run {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "최저가! 지금 구매하면 내일 아침7시전 도착!\n")
                putExtra(Intent.EXTRA_TEXT, "https://coupa.ng/bgWoai")
                startActivity(Intent.createChooser(this, "친구에게 공유해주기!"))
            }
        }.let {
            rootView.ivShare.setOnClickListener(it)  //공유
            rootView.tvShare.setOnClickListener(it)  //공유
            rootView.btnShare.setOnClickListener(it) //공유
        }
        //내 댓글 자세히보기 ㄲ
        rootView.tvUserComment.setOnClickListener {
            mainProduct.myReview.userData = UserData(User)
            (activity as ViewMain).reviewDetailFragment =
                ViewReviewDetailFragment.newInstance(mainProduct.myReview)
            Util.fragmentChanger(
                (activity as ViewMain),
                (activity as ViewMain).reviewDetailFragment,
                HOME
            )
        }
        //분포도 더보기
        rootView.tvGraphMore.setOnClickListener {
            (activity as ViewMain).graphFragment =
                ViewGraphFragment.newInstance(mainProduct.graph, mainProduct)
            Util.fragmentChanger((activity as ViewMain), (activity as ViewMain).graphFragment, HOME)
        }
        //제품 정보 더보기
        rootView.tvProductInfoMore.setOnClickListener {
            (activity as ViewMain).productInfoMoreFragment =
                ViewInfoMoreFragment.newInstance(mainProduct)
            Util.fragmentChanger(
                (activity as ViewMain),
                (activity as ViewMain).productInfoMoreFragment,
                HOME
            )
        }
        //제품 리뷰 더보기
        rootView.tvReviewMore.setOnClickListener {
            (activity as ViewMain).reviewMoreFragment = ViewReviewMoreFragment.newInstance()
            Util.fragmentChanger(
                (activity as ViewMain),
                (activity as ViewMain).reviewMoreFragment,
                HOME
            )
        }
    }

    companion object {
        lateinit var mainProduct: ProductData
        @JvmStatic
        fun productInfo(productData: ProductData) =
            ViewProductInfoFragment().apply {
                mainProduct = productData
            }
    }
}