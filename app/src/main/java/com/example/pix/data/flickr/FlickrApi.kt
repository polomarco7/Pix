package com.example.pix.data.flickr

import com.example.pix.data.flickr.dto.FlickrResult
import retrofit2.http.GET
import retrofit2.http.Query

// https://www.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=da9d38d3dee82ec8dda8bb0763bf5d9c&format=json&nojsoncallback=1

// https://www.flickr.com/services/api/flickr.photos.search.html
interface FlickrApi {
    @GET(SEARCH_URL)
    suspend fun search(
        @Query("text") text: String = "cats",
        @Query("page") page: Int = 1,
        @Query("per_page") count: Int = 100,
    ): FlickrResult

    companion object {
        private const val SEARCH_URL = "/services/rest/?method=flickr.photos.getRecent&api_key=da9d38d3dee82ec8dda8bb0763bf5d9c&format=json&nojsoncallback=1"
    }
}