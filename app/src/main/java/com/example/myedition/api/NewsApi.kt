package com.example.myedition.api

import com.example.myedition.models.NewsResponse
import com.example.myedition.utilities.Constance.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {


 @GET("v2/top-headlines")
    suspend fun getBreakingNews (
        @Query("country")
        countrycode:String ="us" ,
        @Query("pagenumber")
        pageNumber:Int = 1,
        @Query("apikey")
      apikey:String = API_KEY
   ):Response<NewsResponse>

   @GET("v2/everything")
  suspend fun searchForNews (
       @Query("q")
       searchquery:String
       ,   @Query("page")
        pageNumber:Int = 1
        ,    @Query("apikey")
        apikey:String = API_KEY
   ):Response<NewsResponse>
}