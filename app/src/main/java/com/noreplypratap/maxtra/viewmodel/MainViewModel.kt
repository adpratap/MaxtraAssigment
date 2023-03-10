package com.noreplypratap.maxtra.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noreplypratap.maxtra.model.response.ListOfPosts
import com.noreplypratap.maxtra.repository.Repository
import com.noreplypratap.maxtra.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.await
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _getPostsResponse = MutableLiveData<ListOfPosts>()
    val getPostsResponse: LiveData<ListOfPosts>
        get() = _getPostsResponse

    private val _createPostsResponse = MutableLiveData<Boolean>()
    val createPostsResponse: LiveData<Boolean>
        get() = _createPostsResponse

    fun getPosts(userId: Int) = viewModelScope.launch {
        repository.getPosts(userId).let {
            if (it.isSuccessful) {
                _getPostsResponse.postValue(it.body())
            } else {
                Log.d(TAG, "Error ... ")
            }
        }
    }

    fun postData(
        name: String,
        userId: Int,
        postType: Int,
        description: String,
        images: List<File>?,
        videos: File?,
        videoThumb: File?
    ) = viewModelScope.launch {
        try {
            val response = repository.createPostRepo(
                name,
                userId,
                postType,
                description,
                images,
                videos,
                videoThumb
            ).await()

            if (response.data?.name != null){
                _createPostsResponse.postValue(true)
            }else{
                _createPostsResponse.postValue(false)
            }
        }catch (e : Exception){
            _createPostsResponse.postValue(true)
            Log.d(TAG, "Exception Error But Post Created............ ${e.message} ")
        }
    }

}