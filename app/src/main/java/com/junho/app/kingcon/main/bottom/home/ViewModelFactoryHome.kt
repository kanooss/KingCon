package com.junho.app.kingcon.main.bottom.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

object ViewModelFactoryHome: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelHome::class.java)) {
            return ViewModelHome() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}