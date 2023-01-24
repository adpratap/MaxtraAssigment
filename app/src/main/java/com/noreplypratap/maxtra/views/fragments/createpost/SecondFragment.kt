package com.noreplypratap.maxtra.views.fragments.createpost

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.noreplypratap.maxtra.databinding.FragmentSecondBinding
import com.noreplypratap.maxtra.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class SecondFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()
    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!
    private val REQUEST_SELECT_VIDEO = 1

    private lateinit var selectedImages: Uri

    private var sVideoUri: Uri? = null

    private val FILE_MANAGER_REQUEST_CODE = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPost.setOnClickListener {

            val name = binding.etName.text.toString()
            val desc = binding.etDescription.text.toString()
            val video: MultipartBody.Part? = setupVideo()
            val images: MultipartBody.Part? = setupImages()
            val videoThumbImages: MultipartBody.Part? = setupThumbImages()

            val status : Boolean = validation(video,images,videoThumbImages)

            if (status){
                val multipartBody = createPost(name, 92, 1, desc, images, video, videoThumbImages)
                sendPost(multipartBody)
            }else{
                val multipartBody = createPost(name, 92, 1, desc, null, null, null)
                sendPost(multipartBody)
            }

        }

        binding.btnSelectVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_SELECT_VIDEO)
        }

        binding.btnSelectImages.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, FILE_MANAGER_REQUEST_CODE)
        }

        mainViewModel.createPostsResponse.observe(viewLifecycleOwner) {
            if (it.message == "Success") {
                Toast.makeText(
                    requireContext(),
                    "Done Post Created... ${it.message} ",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error Post Created... ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun validation(
        video: MultipartBody.Part?,
        images: MultipartBody.Part?,
        videoThumbImages: MultipartBody.Part?
    ): Boolean {
        if (video != null && images != null && videoThumbImages != null){
            return true
        }
        return false
    }

    private fun setupThumbImages(): MultipartBody.Part? {
        return null
    }

    private fun setupImages(): MultipartBody.Part? {

        val imageFile = getRealPathFromUri(selectedImages)
        val imageFile2 = selectedImages.path?.let { File(it) }
        val imageRequestBody = imageFile?.let {
            RequestBody.create(
                "image/*".toMediaTypeOrNull(),
                it
            )
        }
        if (imageRequestBody != null) {
            if (imageFile2 != null) {
                return MultipartBody.Part.createFormData(
                    "images[]",
                    imageFile2.name,
                    imageRequestBody
                )
            }
        }

        return null
    }

    private fun setupVideo(): MultipartBody.Part? {

        val videoFile = sVideoUri?.let { getRealPathFromUri(it) }
        val videoFile2 = sVideoUri?.path!!.let { File(it) }
        val videoRequestBody = videoFile?.let {
            RequestBody.create(
                "video/*".toMediaTypeOrNull(),
                it
            )
        }
        videoRequestBody?.let {
            return MultipartBody.Part.createFormData(
                "video", videoFile2.name,
                it
            )
        }

        return null
    }

    fun getRealPathFromUri(uri: Uri): String? {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = requireContext().contentResolver.query(uri, proj, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }


    private fun sendPost(multipartBody: MultipartBody) {
        mainViewModel.multiPartPostData(multipartBody)
        //findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_MANAGER_REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedVideoUri = data?.data
            sVideoUri = selectedVideoUri
            Toast.makeText(requireContext(), "Video Selected..", Toast.LENGTH_SHORT).show()
        }

        if (requestCode == FILE_MANAGER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data?.data != null) {
                selectedImages = data.data!!
                Toast.makeText(requireContext(), "Image Selected..", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createPost(
        name: String,
        userId: Int = 92,
        postType: Int = 1,
        description: String,
        images: MultipartBody.Part?,
        video: MultipartBody.Part?,
        videoThumbnail: MultipartBody.Part?
    ): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("name", name)
            .addFormDataPart("user_id", userId.toString())
            .addFormDataPart("post_type", postType.toString())
            .addFormDataPart("discription", description)
            .apply {
                video?.let {
                    addPart(video)
                }
            }
            .apply {
                videoThumbnail?.let {
                    addPart(videoThumbnail)
                }
            }
            .apply {
                images?.let {
                    addPart(it)
                }
            }.build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}