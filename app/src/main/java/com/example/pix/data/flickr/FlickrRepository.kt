package com.example.pix.data.flickr

import com.example.pix.domain.entity.Picture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FlickrRepository @Inject constructor(
    private val apiService: FlickrApiService,
    private val flickrPhotoDao: FlickrPhotoDao,
    private val apiKey: String
) {
    suspend fun searchPhotos(query: String = "nature"): Resource<List<FlickrPhoto>> {
        return try {
            withContext(Dispatchers.IO) {
                // First try to get fresh data from network
                val response = apiService.searchPhotos(apiKey, query)
                val photos = response.photos.photo

                // Cache the new photos
                val entities = photos.map { FlickrPhotoEntity.fromFlickrPhoto(it, query) }
                flickrPhotoDao.refreshPhotosForQuery(query, entities)

                Resource.Success(photos)
            }
        } catch (e: Exception) {
            // If network fails, try to get cached data
            val cachedPhotos = flickrPhotoDao.getPhotosByQuery(query)
                .map { entities ->
                    entities.map { it.toFlickrPhoto() }
                }

            if (cachedPhotos != null) {
                Resource.Success(cachedPhotos)
            } else {
                Resource.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun getPhotosStream(query: String = "nature"): Flow<List<FlickrPhoto>> {
        return flickrPhotoDao.getPhotosByQuery(query)
            .map { entities -> entities.map { it.toFlickrPhoto() } }
            .flowOn(Dispatchers.IO)
    }

    suspend fun getPhotoById(photoId: String): FlickrPhoto? {
        return withContext(Dispatchers.IO) {
            flickrPhotoDao.getPhotoById(photoId)?.toFlickrPhoto()
        }
    }
}