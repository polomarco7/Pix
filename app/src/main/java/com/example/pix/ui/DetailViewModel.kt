package com.example.pix.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pix.data.flickr.FlickrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: FlickrRepository
) : ViewModel() {
    private val _photoUrl = MutableStateFlow<String?>(null)
    val photoUrl: StateFlow<String?> = _photoUrl

    fun loadPhoto(originalUrl: String, photoId: String): StateFlow<String?> {
        viewModelScope.launch {
            // First try to get the latest from repository (which will check cache)
            val photo = repository.getPhotoById(photoId)
            _photoUrl.value = photo?.getLargeUrl() ?: originalUrl
        }
        return photoUrl
    }
}