package com.noreplypratap.maxtra.api

import com.noreplypratap.maxtra.model.request.CreatePost
import com.noreplypratap.maxtra.model.response.ListOfPosts
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface PostServices {

    @POST("post_listing")
    suspend fun getPosts(
        @Query("user_id") userId : Int
    ) : Response<ListOfPosts>

    @POST("create_post")
    suspend fun multiPartPostData(@Body body: MultipartBody): Call<CreatePost>


    @Multipart
    @POST("create_post")
    fun createPost(
        @Part("name") name: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part("post_type") postType: RequestBody,
        @Part("discription") description: RequestBody,
        @Part images: List<MultipartBody.Part>?,
        @Part videos: MultipartBody.Part?,
        @Part videoThumbnails: MultipartBody.Part?
    ): Call<CreatePost>

}