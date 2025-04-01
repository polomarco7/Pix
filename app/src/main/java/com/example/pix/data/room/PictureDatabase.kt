package com.example.pix.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PictureDbo::class],
    version = 1
)
abstract class PictureDatabase : RoomDatabase() {
    abstract fun pictureDao(): PictureDao

    companion object {
        const val DATABASE_NAME = "flickr_db"
    }
}