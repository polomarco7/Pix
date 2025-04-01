package com.example.pix.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pix.data.flickr.FlickrRepository
import com.example.pix.data.flickr.dto.PhotoDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: FlickrRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("nature")
    private val _photos = MutableStateFlow<Flow<PagingData<PhotoDto>>?>(null)
    val photos: StateFlow<Flow<PagingData<PhotoDto>>?> = _photos

    init {
        searchPhotos("nature")
    }

    fun searchPhotos(query: String) {
        _searchQuery.value = query
        _photos.value = repository.getPhotosPagingFlow(query)
            .cachedIn(viewModelScope)
    }

    suspend fun clearCache(query: String) {
        repository.clearCache(query)
    }
}