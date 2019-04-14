package com.junho.app.kingcon.Main.Product.View

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
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
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.fragment_view_graph.view.*
import kotlin.concurrent.thread

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ViewGraphFragment : Fragment() {
    private val argProductData = "productData"
    private val argGraphData = "graphData"
    private lateinit var  graphData: ArrayList<Int>
    private lateinit var  productData: ProductData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productData = it.getSerializable(argGraphData) as ProductData
            graphData = it.getIntegerArrayList(argProductData)
        }
    }
    private lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_view_graph, container, false)

        setInitialize()
        setView()

        return rootView
    }

    /** 별점 그래프 옵션 설정 **/
    private fun graphSetting(view: BarChart, graph: ArrayList<Int>) {
        view.apply {
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
        val xAxis = view.xAxis
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
        val yAxisL  = view.axisLeft //y축 좌측
        yAxisL.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
            setDrawLabels(false)
        }
        val yAxisR  = view.axisRight //y축 우측
        yAxisR.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
            setDrawLabels(false)
        }
        view.setFitBars(true)
        view.animateY(500) // 그래프 에니메이션 시간 (안하면 바로 안뜸)
        view.visibility = View.VISIBLE
//        rootView.graphProgressBar.visibility = View.GONE  // 프로그래스
        setGraphData(10, view, charTextSize, graph)
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
    @SuppressLint("SetTextI18n")
    private fun setInitialize() {
        thread {
            AWSDB.readGraphData(productData)
            ThreadUtils.runOnUiThread {
                rootView.tvAllUserRating.text = "평점 ${Util.getFloatText(productData.point)}(${productData.rating}명)"
                rootView.tvMaleUserRating.text = "평점 ${Util.getFloatText(productData.malePoint)}(${productData.male}명)"
                rootView.tvFemaleUserRating.text = "평점 ${Util.getFloatText(productData.femalePoint)}(${productData.female}명)"
                graphSetting(rootView.allUserGraph, productData.graph)//그래프
                graphSetting(rootView.maleUserGraph, productData.maleGraph)
                graphSetting(rootView.femaleUserGraph, productData.femaleGraph)
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setView() {

    }
}