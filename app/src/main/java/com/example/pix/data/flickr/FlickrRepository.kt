package com.example.pix.data.flickr

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.pix.data.flickr.dto.PhotoDto
import com.example.pix.data.room.PictureDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlickrRepository @Inject constructor(
    private val apiService: FlickrApi,
    private  val pictureDao: PictureDao
) {
    fun getPhotosPagingFlow(query: String): Flow<PagingData<PhotoDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false,
                initialLoadSize = 50
            ),
            pagingSourceFactory = {
                CombinedPagingSource(
                    localSource = pictureDao.getPagingSource(query),
                    remoteSource = FlickrPagingSource(apiService, query),
                    dao = pictureDao,
                    query = query
                )
            }
        ).flow
    }
    suspend fun clearCache(query: String) {
        pictureDao.clearByQuery(query)
    }
}