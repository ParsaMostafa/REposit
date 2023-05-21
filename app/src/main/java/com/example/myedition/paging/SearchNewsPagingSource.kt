package com.example.myedition.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myedition.api.NewsApi
import com.example.myedition.models.Article

class SearchNewsPagingSource(private val newsApi: NewsApi, private val query: String):PagingSource<Int,Article>() {
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        try {
            val pageNumber = params.key ?: 1
            val response = newsApi.searchForNews(searchquery = query, pageNumber = pageNumber)
            val articles = response.body()?.articles ?: emptyList()

            val prevKey = if (pageNumber > 1) pageNumber - 1 else null
            val nextKey = if (articles.isNotEmpty()) pageNumber + 1 else null

            return LoadResult.Page(
                data = articles,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}