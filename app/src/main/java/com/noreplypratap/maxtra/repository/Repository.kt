package com.noreplypratap.maxtra.repository

import com.noreplypratap.maxtra.api.PostServices
import com.noreplypratap.maxtra.model.request.CreatePost
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File
import javax.inject.Inject

class Repository @Inject constructor(private val postServices: PostServices) {

    suspend fun getPosts(userId: Int) = postServices.getPosts(userId)

    fun createPostRepo(
        name: String,
        userId: Int,
        postType: Int,
        description: String,
        images: List<File>?,
        videos: File?,
        videoThumb: File?
    ): Call<CreatePost> {

        val nameBody = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
        val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId.toString())
        val postTypeBody = RequestBody.create("text/plain".toMediaTypeOrNull(), postType.toString())
        val descriptionBody = RequestBody.create("text/plain".toMediaTypeOrNull(), description)


        val thumbFile = File(videoThumb!!.path)
        val thumbRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), thumbFile)
        val thumbPart =
            MultipartBody.Part.createFormData("video_thumbnail", thumbFile.name, thumbRequestBody)

        val vFile = File(videos!!.path)
        val vRequestBody = RequestBody.create("videos/*".toMediaTypeOrNull(), vFile)
        val videoPart = MultipartBody.Part.createFormData("videos", vFile.name, vRequestBody)


        val imagesParts = images?.map {
            val file = File(it.path)
            val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("images[]", file.name, requestBody)
        }

        return postServices.createPost(
            nameBody,
            userIdBody,
            postTypeBody,
            descriptionBody,
            imagesParts,
            videoPart,
            thumbPart
        )
    }

}
