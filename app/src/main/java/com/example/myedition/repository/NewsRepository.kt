package com.example.myedition.repository
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.myedition.api.NewsApi
import com.example.myedition.db.ArticleDao
import com.example.myedition.models.Article
import com.example.myedition.paging.NewsPagingSource
import com.example.myedition.paging.SearchNewsPagingSource
import kotlinx.coroutines.flow.Flow

class NewsRepository(private val newsApi: NewsApi, private val articleDao: ArticleDao) {

    fun getBreakingNews(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { NewsPagingSource(newsApi) }
        ).flow
    }

    fun searchForNews(query: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { SearchNewsPagingSource(newsApi, query) }
        ).flow
    }

    suspend fun upsertArticle(article: Article) {
        articleDao.upsert(article)
    }

    suspend fun deleteArticle(article: Article) {
        articleDao.deleteArticle(article)
    }
    fun getAllArticles(): Flow<List<Article>> {
        return articleDao.getAllArticles()
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}

/*class NewsRepository(val db: ArticleDatabase) {

    // API Calls
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponse> {
        return RetrofitInctance.api.getBreakingNews(countryCode, pageNumber)
    }

    suspend fun searchForNews(query: String, pageNumber: Int): Response<NewsResponse> {
        return RetrofitInctance.api.searchForNews(query, pageNumber)
    }

    // Database Operations
    fun getAllArticles(): Flow<List<Article>> {
        return db.getArticleDao().getAllArticles()
    }

    suspend fun upsert(article: Article):Long {
        return db.getArticleDao().upsert(article)
    }

    suspend fun deleteArticle(article: Article) {
        db.getArticleDao().deleteArticle(article)
    }
}        */












