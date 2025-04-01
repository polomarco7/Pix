package com.example.pix.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pix.data.flickr.dto.PhotoDto

@Entity(tableName = "pictures")
data class PictureDbo (
    @PrimaryKey
    val id: String,
    val title: String,
    val query: String,
    val secret: String,
    val server: String,
    val lastUpdated: Long = System.currentTimeMillis()
){
    fun toPhotoDto(): PhotoDto {
        return PhotoDto(
            id = id,
            title = title,
            secret = secret,
            server = server
        )
    }

    companion object {
        fun fromPhotoDto(photo: PhotoDto, query: String): PictureDbo {
            return PictureDbo(
                id = photo.id,
                title = photo.title,
                query = query,
                secret = photo.secret,
                server = photo.server,
            )
        }
    }
}