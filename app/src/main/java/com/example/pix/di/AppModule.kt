package com.example.pix.di

import android.content.Context
import androidx.room.Room
import com.example.pix.data.flickr.FlickrApi
import com.example.pix.data.flickr.FlickrRepository
import com.example.pix.data.room.PictureDao
import com.example.pix.data.room.PictureDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "https://api.flickr.com/"
    private const val API_KEY = "da9d38d3dee82ec8dda8bb0763bf5d9c"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFlickrApiService(retrofit: Retrofit): FlickrApi {
        return retrofit.create(FlickrApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): PictureDatabase {
        return Room.databaseBuilder(
            appContext,
            PictureDatabase::class.java,
            PictureDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideFlickrPhotoDao(database: PictureDatabase): PictureDao {
        return database.pictureDao()
    }

    @Provides
    @Named("apiKey")
    fun provideApiKey(): String = API_KEY

    @Provides
    @Singleton
    fun provideFlickrRepository(
        apiService: FlickrApi,
        pictureDao: PictureDao
    ): FlickrRepository {
        return FlickrRepository(apiService, pictureDao)
    }
}