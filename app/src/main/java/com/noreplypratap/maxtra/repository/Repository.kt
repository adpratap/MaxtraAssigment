package com.noreplypratap.maxtra.repository

import com.noreplypratap.maxtra.api.PostServices
import okhttp3.MultipartBody
import javax.inject.Inject

class Repository @Inject constructor(private val postServices: PostServices) {

    suspend fun getPosts(userId: Int) = postServices.getPosts(userId)

    suspend fun multiPartPostData(body: MultipartBody) = postServices.multiPartPostData(body)

}
