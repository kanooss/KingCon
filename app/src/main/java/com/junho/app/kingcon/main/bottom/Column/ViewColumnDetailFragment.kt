package com.junho.app.kingcon.main.bottom.Column

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.junho.app.kingcon.Item.ColumnData
import com.junho.app.kingcon.R

class ViewColumnDetailFragment : androidx.fragment.app.Fragment() {
    private val argColumn = "column"
    private lateinit var  columnData: ColumnData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnData = it.getSerializable(argColumn) as ColumnData
        }
    }

    private lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_view_column_detail, container, false)

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(columnData: ColumnData) =
            ViewColumnDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(argColumn, columnData)
                }
            }
    }
}
