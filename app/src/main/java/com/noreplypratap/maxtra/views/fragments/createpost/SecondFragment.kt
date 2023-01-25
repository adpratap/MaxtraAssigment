package com.noreplypratap.maxtra.views.fragments.createpost

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.noreplypratap.maxtra.R
import com.noreplypratap.maxtra.databinding.FragmentSecondBinding
import com.noreplypratap.maxtra.utils.TAG
import com.noreplypratap.maxtra.utils.isOnline
import com.noreplypratap.maxtra.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
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

        setupUI()

        binding.btnPost.setOnClickListener {
            createPost()
        }

        binding.btnSelectVideo.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Selected",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createPost() {
        val name = binding.etName.text.toString()
        val desc = binding.etDescription.text.toString()

        val pattern = "[a-zA-Z ]*"
        val matcher = Regex(pattern).matches(name)
        if (!matcher) {
            Toast.makeText(
                requireContext(),
                "Enter Valid Name .. ",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (name.isBlank() && desc.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Please Enter Data ... ",
                Toast.LENGTH_SHORT
            ).show()
            return
        }else{
            if (requireContext().isOnline()) {
                moreData()
                showProgressBar()
                postData(name,desc)
            } else {
                Toast.makeText(
                    requireContext(),
                    "No Internet!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun moreData() {
        Toast.makeText(
            requireContext(),
            "Wait Processing ... ",
            Toast.LENGTH_SHORT
        ).show()
        runBlocking {
            launch(Dispatchers.IO) {
                setupImages()
                setupVideos()
            }
        }
        Toast.makeText(
            requireContext(),
            "Selected",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showProgressBar() {
        binding.progressBar2.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar2.visibility = View.GONE
    }

    private fun setupUI() {
        mainViewModel.createPostsResponse.observe(viewLifecycleOwner) {
            Log.d(TAG, it.toString())
            if (it) {
                hideProgressBar()
                Toast.makeText(
                    requireContext(),
                    "Data Posted",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error .. ",
                    Toast.LENGTH_SHORT
                ).show()
                hideProgressBar()
            }
        }
    }

    private fun postData(name : String , desc : String) {
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

    private fun setupImages() {
        selectedImageFile.apply {
            add(imageToFile(R.drawable.image1))
            add(imageToFile(R.drawable.image2))
        }
        selectedVideosThumb = imageToFile(R.drawable.image2)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun imageToFile(image: Int): File {
        val drawable = resources.getDrawable(image)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val file = File(requireContext().cacheDir, "images.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.close()

        return file
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
        binding.progressBar2.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



