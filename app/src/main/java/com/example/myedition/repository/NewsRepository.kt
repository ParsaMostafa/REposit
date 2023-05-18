package com.example.myedition.repository

import com.example.myedition.api.RetrofitInctance
import com.example.myedition.db.ArticleDao
import com.example.myedition.models.Article
import com.example.myedition.models.NewsResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class NewsRepository(private val articleDao: ArticleDao) {

    // API Calls
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponse> {
        return RetrofitInctance.api.getBreakingNews(countryCode, pageNumber)
    }

    suspend fun searchForNews(query: String, pageNumber: Int): Response<NewsResponse> {
        return RetrofitInctance.api.searchForNews(query, pageNumber)
    }

    // Database Operations
    fun getAllArticles(): Flow<List<Article>> {
        return articleDao.getAllArticles()
    }

    suspend fun upsert(article: Article) {
        articleDao.upsert(article)
    }

    suspend fun deleteArticle(article: Article) {
        articleDao.deleteArticle(article)
    }
}


    //I DONT WANT TO USE THESE CODES ANY MORE
  /*  suspend fun getBreakingNews( countrycode:String , pagenumber:Int)=

       RetrofitInctance.api.getBreakingNews(countrycode,pagenumber)

    suspend fun searchNews(searchQuery:String,pageNumber: Int) =

        RetrofitInctance.api.searchForNews(searchQuery ,pageNumber)
        */



 /*   suspend fun upsert(article: Article ) {
        withContext(Dispatchers.IO){

     db.getArticleDao().upsert(article)}}


      fun getSavedNews() =
          db.getArticleDao().getAllArticles()

       suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)*/













