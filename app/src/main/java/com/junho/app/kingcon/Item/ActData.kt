package com.junho.app.kingcon.Item

import java.io.Serializable

data class ActData(var rating: Int, var wish: Int, var review: Int) : Serializable {
    constructor():this(0,0,0)
    override fun toString() = "rating: $rating, wish: $wish, review: $review"
}