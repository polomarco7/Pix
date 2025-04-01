package com.example.pix.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pix.data.flickr.FlickrRepository
import com.example.pix.data.flickr.dto.PhotoDto
import com.example.pix.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: FlickrRepository
) : ViewModel() {

    private val _photo = MutableStateFlow<PhotoDto?>(null)
    val photo: StateFlow<PhotoDto?> = _photo

    private val _imageLoadingState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val imageLoadingState: StateFlow<Resource<Unit>> = _imageLoadingState

    fun setPhoto(photo: PhotoDto) {
        _photo.value = photo
    }

    fun loadPhoto(photoId: String) {
        viewModelScope.launch {
            _imageLoadingState.value = Resource.Loading()
            try {
                _imageLoadingState.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _imageLoadingState.value = Resource.Error(e.message ?: "Error loading photo details")
            }
        }
    }

    fun setImageLoadingState(state: Resource<Unit>) {
        _imageLoadingState.value = state
    }
}