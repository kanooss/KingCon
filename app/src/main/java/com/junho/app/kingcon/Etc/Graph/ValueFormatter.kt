package com.junho.app.kingcon.Etc.Graph

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

class ValueFormatter : IValueFormatter {
    private var mFormat : DecimalFormat = DecimalFormat("###,###.#")
    override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?) = mFormat.format(value)!!
}
