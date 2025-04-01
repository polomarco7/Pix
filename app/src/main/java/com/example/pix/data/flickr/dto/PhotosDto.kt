package com.example.pix.data.flickr.dto

import com.google.gson.annotations.SerializedName

data class PhotosDto(
    val page: Int,
    val pages: Int,
    @SerializedName("perpage")
    val perPage: Int,
    val total: Int,
    val photo: List<PhotoDto>
)
