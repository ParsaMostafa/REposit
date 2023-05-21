package com.example.myedition.models

import androidx.paging.PagingData
import com.example.myedition.models.Article

data class NewsResponse(
    val articles: PagingData<Article>,
    val status: String,
    val totalResults: Int
)