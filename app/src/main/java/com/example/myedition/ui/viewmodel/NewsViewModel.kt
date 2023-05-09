package com.example.myedition.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myedition.NewsApllication
import com.example.myedition.models.Article
import com.example.myedition.models.NewsResponse
import com.example.myedition.repository.NewsRepository
import com.example.myedition.utilities.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app : Application,
    val  newsRepository: NewsRepository
):AndroidViewModel(app
) {



    val breakingNews:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
       var brakingnewspage = 1
    var breakingnewsResponse : NewsResponse?=null

    val searchingNews:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchingNewspage =1
    var searchingnewsResponse : NewsResponse?=null

    init {
        getBreakingNews("us" , "business")

    }


    fun getBreakingNews (countrycode:String ,category:String) = viewModelScope.launch {
        safebreakingnewscall(countrycode,category)



    }

    private suspend fun safebreakingnewscall(countrycode: String,category: String)
    {
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasinconnection()){
                breakingNews.postValue(Resource.Loading())
                val response  = newsRepository.getBreakingNews(countrycode,category)
                breakingNews.postValue(handlebreakingnewsresponse(response))}
            else{
                breakingNews.postValue(Resource.Error("internet check"))
            }

        } catch (t:Throwable){
            when(t){
                is IOException -> breakingNews.postValue(Resource.Error("your offline "))
                else -> breakingNews.postValue(Resource.Error("Conversion error"))
            }

        }
    }
    private fun handlebreakingnewsresponse(response: Response<NewsResponse>):Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse->
                  brakingnewspage++
                if (breakingnewsResponse == null){
                    breakingnewsResponse = resultResponse
                }else{
                    val oldarticles = breakingnewsResponse?.articles
                    val newarticles = resultResponse.articles
                    oldarticles?.addAll(newarticles)
                }

                return Resource.Succsess(breakingnewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun hasinconnection ():Boolean{
        val conectivitymanager = getApplication<NewsApllication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            val activenetwork = conectivitymanager.activeNetwork ?: return false
            val capitilitis =  conectivitymanager.getNetworkCapabilities(activenetwork)?: return false
            return when {
                capitilitis.hasTransport(TRANSPORT_WIFI) -> true
                capitilitis.hasTransport(TRANSPORT_CELLULAR) -> true
                capitilitis.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }


        }else
            conectivitymanager.activeNetworkInfo?.run {
                return when (type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false

                }
            }
        return false
    }



    fun searchNews (q:String , from :String,sortby:String)=viewModelScope.launch {
       safesearchnewscall(q,from,sortby)

    }


    private suspend fun safesearchnewscall(q: String,from: String,sortby: String)
    {
        searchingNews.postValue(Resource.Loading())
        try {
            if (hasinconnection()){
                searchingNews.postValue(Resource.Loading())
                val response  = newsRepository.getnews (q, from, sortby  )
                searchingNews.postValue(handlesearchingnewsresponse(response))}
            else{
                searchingNews.postValue(Resource.Error("لطفا اتصال اینترنت بررسی کنید "))
            }

        } catch (t:Throwable){
            when(t){
                is IOException -> searchingNews.postValue(Resource.Error("اتصال اینترنت برقرار نیست "))
                else -> searchingNews.postValue(Resource.Error("Conversion error"))
            }

        }
    }




    private fun handlesearchingnewsresponse(response: Response<NewsResponse>):Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse->
                searchingNewspage++
                if (searchingnewsResponse  == null){
                   searchingnewsResponse = resultResponse
                }else{
                    val oldarticles = searchingnewsResponse?.articles
                    val newarticles = resultResponse.articles
                    oldarticles?.addAll(newarticles)
                }

                return Resource.Succsess(searchingnewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun deletearticle(article: Article)=viewModelScope.launch { newsRepository.deleteArticle(article) }




    fun savearticle(article: Article) {

     viewModelScope.launch {
        newsRepository.upsert(article)
    }




}




    }




