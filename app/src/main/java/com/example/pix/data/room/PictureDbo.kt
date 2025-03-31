package com.example.pix.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pictures")
data class PictureDbo (
    @PrimaryKey
    val id: String,
    val title: String,
    val url: String,
    val label: String,
)