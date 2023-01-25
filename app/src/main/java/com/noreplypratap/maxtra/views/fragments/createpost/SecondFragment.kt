package com.noreplypratap.maxtra.views.fragments.createpost

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.noreplypratap.maxtra.R
import com.noreplypratap.maxtra.databinding.FragmentSecondBinding
import com.noreplypratap.maxtra.utils.isOnline
import com.noreplypratap.maxtra.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class SecondFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()
    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!

    private var selectedImageFile = mutableListOf<File>()

    private var selectedVideos: File? = null

    private var selectedVideosThumb: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupVideos()
        setupImagesAndThumb()
        setupUI()

        binding.btnPost.setOnClickListener {
            if (requireContext().isOnline()){
                binding.progressBar2.visibility = View.VISIBLE
                postData()
            }else{
                Toast.makeText(
                    requireContext(),
                    "No Internet!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnSelectVideo.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Selected",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupUI() {
        mainViewModel.createPostsResponse.observe(viewLifecycleOwner) {
            if (it.message == "Success") {
                binding.progressBar2.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Data Posted",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            }else{
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private fun postData() {
        val name = binding.etName.text.toString()
        val desc = binding.etDescription.text.toString()
        if (name.isBlank() && desc.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Please Enter Data ... ",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            mainViewModel.postData(
                name,
                92,
                1,
                desc,
                selectedImageFile,
                selectedVideos,
                selectedVideosThumb
            )
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupImagesAndThumb() {
        val drawable = resources.getDrawable(R.drawable.image1)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val file = File(requireContext().cacheDir, "images.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.close()
        selectedImageFile.add(file)
        selectedImageFile.add(file)
        selectedImageFile.add(file)
        selectedVideosThumb = file
    }

    private fun setupVideos() {
        val inputStream = resources.openRawResource(R.raw.videofor)
        val videoFile = File(requireContext().cacheDir, "video.mp4")
        val vOutputStream = FileOutputStream(videoFile)
        inputStream.use { input ->
            vOutputStream.use { output ->
                input.copyTo(output)
            }
        }
        selectedVideos = videoFile
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
            .addFormDataPart("images[]", images.toString())
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



