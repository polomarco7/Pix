package com.example.pix.data.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PictureDao {
    @Query("SELECT * FROM pictures WHERE `query` = :query ORDER BY lastUpdated DESC")
    fun getPagingSource(query: String): PagingSource<Int, PictureDbo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PictureDbo>)

    @Query("DELETE FROM pictures WHERE `query` = :query")
    suspend fun clearByQuery(query: String)
}