package com.example.pix.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pix.data.flickr.FlickrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: FlickrRepository
) : ViewModel() {
    private val _photos = MutableStateFlow<Resource<List<FlickrPhoto>>>(Resource.Loading())
    val photos: StateFlow<Resource<List<FlickrPhoto>>> = _photos

    private val _cachedPhotos = repository.getPhotosStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val cachedPhotos: StateFlow<List<FlickrPhoto>> = _cachedPhotos

    init {
        fetchPhotos()
    }

    fun fetchPhotos(query: String = "nature") {
        viewModelScope.launch {
            _photos.value = Resource.Loading()
            _photos.value = repository.searchPhotos(query)
        }
    }
}