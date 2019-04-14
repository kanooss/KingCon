package com.junho.app.kingcon.Item

import java.io.Serializable

class GraphData (private val ratingData : ArrayList<Int>) : Serializable {
    constructor() : this(arrayListOf(0,0,0,0,0,0,0,0,0,0))

    fun noneZero(){
        for(i in 0 until ratingData.size){
            if(ratingData[i]<0)
                ratingData[i] = 0
        }
    }
}