package com.example.myedition.db

import androidx.room.*
import com.example.myedition.models.Article
import kotlinx.coroutines.flow.Flow


@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE )
     suspend fun upsert(article: Article):Long
    @Query("SELECT * FROM articles")
      fun getAllArticles(): Flow<List<Article>>

    @Delete
      suspend  fun deleteArticle(article: Article)

}



