package com.example.pix.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)


    fun isLoading(): Boolean = this is Loading

    fun isSuccess(): Boolean = this is Success

    fun isError(): Boolean = this is Error

    fun getDataOrNull(): T? = when (this) {
        is Success -> data
        is Error -> data
        is Loading -> data
    }

    fun getMessageOrNull(): String? = when (this) {
        is Error -> message
        else -> null
    }

    fun <R> map(transform: (T) -> R): Resource<R> = when (this) {
        is Success -> Success(transform(data as T))
        is Error -> Error(message.toString(), data?.let(transform))
        is Loading -> Loading(data?.let(transform))
    }
}

fun <T> Flow<T>.asResource(): Flow<Resource<T>> = this
    .map<T, Resource<T>> { Resource.Success(it) }
    .onStart { emit(Resource.Loading()) }
    .catch { emit(Resource.Error(it.message ?: "Unknown error")) }