package com.example.myedition.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myedition.NewsApllication
import com.example.myedition.models.Article
import com.example.myedition.models.NewsResponse
import com.example.myedition.repository.NewsRepository
import com.example.myedition.utilities.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    private val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    private val _breakingNews = MutableStateFlow<Resource<NewsResponse>>(Resource.Loading())
    val breakingNews: StateFlow<Resource<NewsResponse>> = _breakingNews

    private val _searchingNews = MutableStateFlow<Resource<NewsResponse>>(Resource.Loading())
    val searchingNews: StateFlow<Resource<NewsResponse>> = _searchingNews

    var breakingNewsPage = 1
    private var breakingNewsResponse: NewsResponse? = null

    private var searchingNewsPage = 1
    private var searchingNewsResponse: NewsResponse? = null



    // Checking Connection
    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<NewsApllication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    //Breaking News Api Calls
    init {
        getBreakingNews(countryCode = "us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        _breakingNews.value = Resource.Loading()
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                _breakingNews.emit( handleBreakingNewsResponse(response))
            } else {
                _breakingNews.emit(Resource.Error("مشکل برقراری اتصال با شبکه "))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _breakingNews.value = Resource.Error("شما آفلاین هستید")
                else -> _breakingNews.value = Resource.Error("Conversion error")
            }
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }



    // Search News Api Calls
    fun searchNews(q: String, pageNumber: Int) = viewModelScope.launch {
        safeSearchNewsCall(q, pageNumber)
    }

    private suspend fun safeSearchNewsCall(q: String, pageNumber: Int) {
        _searchingNews.value = Resource.Loading()
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getNews(q, pageNumber)
                _searchingNews.value = handleSearchingNewsResponse(response)
            } else {
                _searchingNews.value = Resource.Error("internet check")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _searchingNews.value = Resource.Error("your offline ")
                else -> _searchingNews.value = Resource.Error("Conversion error")
            }
        }
    }

    private fun handleSearchingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchingNewsPage++
                if (searchingNewsResponse == null) {
                    searchingNewsResponse = resultResponse
                } else {
                    val oldArticles = searchingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }




    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }



    init {
        viewModelScope.launch {
            newsRepository.getAllArticles().collect { articles ->
                // Emit the list of articles as a new value of StateFlow.
                _savedArticles.value = articles
            }
        }
    }

    private val _savedArticles = MutableStateFlow<List<Article>>(emptyList())
    val savedArticles: StateFlow<List<Article>> = _savedArticles
}




