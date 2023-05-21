package com.example.myedition.ui.viewmodel
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.myedition.models.Article
import com.example.myedition.models.NewsResponse
import com.example.myedition.repository.NewsRepository
import com.example.myedition.utilities.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    private val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    private val _breakingNews = MutableStateFlow<Resource<PagingData<Article>>>(Resource.Loading)
    val breakingNews: StateFlow<Resource<PagingData<Article>>> = _breakingNews

    private val _searchingNews = MutableStateFlow<Resource<PagingData<Article>>>(Resource.Loading)
    val searchingNews: StateFlow<Resource<PagingData<Article>>> = _searchingNews

    // Checking Connection
    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    init {
        getBreakingNews(countryCode = "us")
    }

    fun getBreakingNews(): Flow<PagingData<Article>> {
        return newsRepository.getBreakingNews()
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        _breakingNews.value = Resource.Loading
        try {
            if (hasInternetConnection()) {
                newsRepository.getBreakingNews().collect { pagingData ->
                    _breakingNews.value = Resource.Success(pagingData)
                }
            } else {
                _breakingNews.value = Resource.Error("مشکل برقراری اتصال با شبکه")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _breakingNews.value = Resource.Error("شما آفلاین هستید")
                else -> _breakingNews.value = Resource.Error("Conversion error")
            }
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<PagingData<Article>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse.articles)
            }
        }
        return Resource.Error(response.message())
    }

    // Search News Api Calls
    fun searchNews(query: String) = viewModelScope.launch {
        safeSearchNewsCall(query)
    }

    private suspend fun safeSearchNewsCall(query: String) {
        _searchingNews.value = Resource.Loading
        try {
            if (hasInternetConnection()) {
                newsRepository.searchForNews(query).collect { pagingData ->
                    _searchingNews.value = Resource.Success(pagingData)
                }
            } else {
                _searchingNews.value = Resource.Error("لطفا اتصال را بررسی کنید")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _searchingNews.value = Resource.Error("شما آفلاین هستین")
                else -> _searchingNews.value = Resource.Error("Conversion error")
            }
        }
    }

    private fun handleSearchingNewsResponse(response: Response<NewsResponse>): Resource<PagingData<Article>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse.articles)
            }
        }
        return Resource.Error(response.message())
    }

    // Database functions
    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsertArticle(article)
    }

    val savedArticles: Flow<List<Article>> = newsRepository.getAllArticles()
}
