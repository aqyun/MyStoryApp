package com.dicoding.mystoryapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.mystoryapp.data.api.ApiService
import com.dicoding.mystoryapp.data.api.ListStoryItem

class StoryPagingSource(private val apiService: ApiService, private val token: String) : PagingSource<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories(token, position, params.loadSize)

            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position -1,
                nextKey = if (responseData.listStory.isEmpty()) null else position +1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}