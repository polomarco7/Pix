package com.example.pix.data.flickr

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pix.data.flickr.dto.PhotoDto
import retrofit2.HttpException
import java.io.IOException

class FlickrPagingSource(
    private val apiService: FlickrApi,
    private val query: String
) : PagingSource<Int, PhotoDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoDto> {
        return try {
            val page = params.key ?: 1
            val response = apiService.search(
                query = query,
                page = page,
                perPage = params.loadSize
            )

            if (response.stat != "ok") {
                return LoadResult.Error(Exception(response.message ?: "API error"))
            }

            val photos = response.photos?.photo ?: emptyList()

            LoadResult.Page(
                data = photos,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (photos.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}