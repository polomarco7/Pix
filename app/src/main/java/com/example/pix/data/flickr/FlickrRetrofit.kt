package com.example.pix.data.flickr

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FlickrRetrofit {
    val api = lazy {
        Retrofit.Builder()
            .baseUrl("https://www.flickr.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FlickrApi::class.java)
    }
}