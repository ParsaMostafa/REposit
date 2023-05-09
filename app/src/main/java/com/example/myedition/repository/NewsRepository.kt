package com.example.myedition.repository

import com.example.myedition.api.RetrofitInctance
import com.example.myedition.db.ArticleDatabase
import com.example.myedition.models.Article

class NewsRepository(val db :ArticleDatabase) {




       suspend fun getBreakingNews(county:String , category:String) =

           RetrofitInctance.api.getBreakingNews(county,category)

       suspend fun getnews (q:String,from:String,sortby:String) =
           RetrofitInctance.api.searchnews(q,from,sortby)

       fun getAllArticles()= db.getArticleDao().getAllArticles()

       suspend fun upsert(article:Article) = db.getArticleDao().upsert(article)

       suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

}

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













