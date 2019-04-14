package com.junho.app.kingcon.Etc.Graph

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter

class DayAxisValueFormatter: IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        //        하단 단위
        return if(value.toInt()%2==1) ((value+1) /2).toInt().toString()
        else ""
    }
}