package com.example.myedition.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myedition.repository.NewsRepository

class NewsViewModelProviderFactory(
    val app:Application,
    val newsepository: NewsRepository
):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(app,newsepository) as T
    }
}