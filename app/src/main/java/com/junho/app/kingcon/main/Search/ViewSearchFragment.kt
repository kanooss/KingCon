package com.junho.app.kingcon.main.Search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.junho.app.kingcon.R

class ViewSearchFragment : androidx.fragment.app.Fragment() {
    private lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_view_search, container, false)

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ViewSearchFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}