package com.example.pix.data.flickr.dto

data class PhotoDto(
    val id: String,
    val secret: String,
    val server: String,
    val title: String
) {
    fun getImageUrl(quality: String = "q"): String {
        return "https://live.staticflickr.com/${server}/${id}_${secret}_${quality}.jpg"
    }
}