package com.example.myedition.ui.viewmodel

import NewsViewModel
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myedition.repository.NewsRepository

class NewsViewModelProviderFactory(
    val app:Application,
    val newssepository: NewsRepository
):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(app,newssepository) as T
    }
}