package com.example.pix.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PictureDao {
    @Dao
    interface FlickrPhotoDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertPhotos(photos: List<FlickrPhotoEntity>)

        @Query("SELECT * FROM flickr_photos WHERE query = :query ORDER BY lastUpdated DESC")
        fun getPhotosByQuery(query: String): Flow<List<FlickrPhotoEntity>>

        @Query("DELETE FROM flickr_photos WHERE query = :query")
        suspend fun deletePhotosForQuery(query: String)

        @Transaction
        suspend fun refreshPhotosForQuery(query: String, photos: List<FlickrPhotoEntity>) {
            deletePhotosForQuery(query)
            insertPhotos(photos)
        }

        @Query("SELECT * FROM flickr_photos WHERE id = :photoId LIMIT 1")
        suspend fun getPhotoById(photoId: String): FlickrPhotoEntity?
    }
}