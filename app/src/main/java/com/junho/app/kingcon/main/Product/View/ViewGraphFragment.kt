package com.junho.app.kingcon.main.Product.View

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.junho.app.kingcon.Etc.AWSDB
import com.junho.app.kingcon.Etc.Graph.DayAxisValueFormatter
import com.junho.app.kingcon.Etc.Graph.ValueFormatter
import com.junho.app.kingcon.Etc.MyLog
import com.junho.app.kingcon.Etc.Product
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.main.Product.View.ViewProductInfoFragment.Companion.mainProduct
import com.junho.app.kingcon.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.fragment_view_graph.view.*
import kotlin.concurrent.thread

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ViewGraphFragment : androidx.fragment.app.Fragment() {
    private val argProductData = "mainProductData"
    private val argGraphData = "graphData"
    private lateinit var rootView: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_view_graph, container, false)

        setInitialize()
        setView()

        return rootView
    }

    /** 별점 그래프 옵션 설정 **/
    private fun graphSetting(view: BarChart, graph: ArrayList<Int>) {
        view.apply {
            val charTextSize = 10f
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
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                granularity = 1f // only int
                valueFormatter = DayAxisValueFormatter()
                textColor = Color.GRAY
                textSize = charTextSize
                setDrawLabels(true)
                labelCount = 10
            }
            axisLeft.apply {
                //y축 좌측
                isEnabled = false // 좌측 바
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
            }
            axisRight.apply {
                //y축 우측
                isEnabled = false // 우측 바
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
            }
            setFitBars(true)
            animateY(500) // 그래프 에니메이션 시간 (안하면 바로 안뜸)
            visibility = View.VISIBLE
//        rootView.graphProgressBar.visibility = View.GONE  // 프로그래스
            setGraphData(10, this, charTextSize, graph)
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
            arrayListOf<IBarDataSet>(this)
        }.run {
            BarData(this)
        }.run {
            setValueTextSize(charTextSize)
            barWidth = 0.8f
            home_photo_Chart.data = this
        }
    }
    /**------------------------------------------------------------------------------------**/
    companion object {
        @JvmStatic
        fun newInstance(graphData: ArrayList<Int>, productData: ProductData) =
            ViewGraphFragment().apply {
                arguments = Bundle().apply {
                    putIntegerArrayList(argProductData, graphData)
                    putSerializable(argGraphData, productData)
                }
            }
    }

    @SuppressLint("SetTextI18n", "CheckResult")
    private fun setInitialize() {
        Util.progressVisible(true, rootView.progress, this.activity)
        mainProduct.graphInit()
        AWSDB.readGraphData(mainProduct)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe {
                rootView.tvAllUserRating.text = "평점 ${Util.getFloatText(it.point)}(${it.rating}명)"
                rootView.tvMaleUserRating.text =
                    "평점 ${Util.getFloatText(it.malePoint)}(${it.male}명)"
                rootView.tvFemaleUserRating.text =
                    "평점 ${Util.getFloatText(it.femalePoint)}(${it.female}명)"
                graphSetting(rootView.allUserGraph, it.graph)//그래프
                graphSetting(rootView.maleUserGraph, it.maleGraph)
                graphSetting(rootView.femaleUserGraph, it.femaleGraph)
                Util.progressVisible(false, rootView.progress, this.activity)
            }
    }

    @SuppressLint("SetTextI18n")
    private fun setView() {

    }
}