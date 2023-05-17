package com.example.myedition.repository

import com.example.myedition.api.RetrofitInctance
import com.example.myedition.db.ArticleDatabase
import com.example.myedition.models.Article

class NewsRepository(val db :ArticleDatabase) {



       // Api Calls 
       suspend fun getBreakingNews (countryCode:String , pageNumber:Int)=
           RetrofitInctance.api.getBreakingNews(countryCode,pageNumber)
       suspend fun getNews (q:String,pageNumber:Int) =
           RetrofitInctance.api.searchForNews(q,pageNumber)

       fun getAllArticles()= db.getArticleDao().getAllArticles()

       suspend fun upsert(article:Article) = db.getArticleDao().upsert(article)

       suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

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













