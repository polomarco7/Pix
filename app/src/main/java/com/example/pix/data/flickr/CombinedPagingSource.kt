package com.example.pix.data.flickr

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pix.data.flickr.dto.PhotoDto
import com.example.pix.data.room.PictureDao
import com.example.pix.data.room.PictureDbo

class CombinedPagingSource(
    private val localSource: PagingSource<Int, PictureDbo>,
    private val remoteSource: PagingSource<Int, PhotoDto>,
    private val dao: PictureDao,
    private val query: String
) : PagingSource<Int, PhotoDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoDto> {
        return try {
            val localResult = localSource.load(params)

            if (localResult is LoadResult.Page && localResult.data.isNotEmpty()) {
                return localResult.map { it.toPhotoDto() } as LoadResult<Int, PhotoDto>
            } else {
                val remoteResult = remoteSource.load(params)

                if (remoteResult is LoadResult.Page) {
                    val entities = remoteResult.data.map {
                        PictureDbo.fromPhotoDto(it, query)
                    }
                    dao.insertPhotos(entities)
                }

                return remoteResult
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoDto>): Int? {
        return remoteSource.getRefreshKey(state)
    }
}