package com.junho.app.kingcon.Item

import java.io.Serializable

data class ColumnData (val image:String, val title:String, val likeNum:Int, val commentNum : Int,val tag : ArrayList<String>) :
    Serializable {
    constructor() : this("","",0,0, arrayListOf())
}