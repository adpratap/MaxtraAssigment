package com.noreplypratap.maxtra.views.fragments.details

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.noreplypratap.maxtra.databinding.FragmentDetailsBinding
import com.noreplypratap.maxtra.model.response.Data
import com.noreplypratap.maxtra.utils.TAG
import com.noreplypratap.maxtra.utils.isOnline

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private var postsData: Data? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (requireContext().isOnline()){
            getData()
        }else{
            Toast.makeText(
                requireContext(),
                "No Internet!!",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun getData() {
        val data = arguments?.getString("nextData")
        if (data != null) {
            postsData = Gson().fromJson(data, Data::class.java)
            setView()
        } else {
            Log.d(TAG, "No Data........")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setView() {
        //Set Views
        Toast.makeText(requireContext(), postsData!!.name, Toast.LENGTH_SHORT).show()
        binding.name.text = "Name : ${postsData!!.name}"
        binding.desc.text = "Desc : ${ postsData!!.discription }"
        binding.postType.text ="Post Type : ${ postsData!!.post_type }"
        binding.userid.text = "User ID : ${ postsData!!.user_id.toString() }"
        binding.likes.text = "Likes : ${ postsData!!.like_count.toString() }"
        // add More Data

        if (postsData?.videos != null){
            Toast.makeText(
                requireContext(),
                "Video Loading ... ",
                Toast.LENGTH_SHORT
            ).show()
            videoPlayer()
        }

        if (postsData?.images != null){
            setupImage()
        }

    }

    private fun setupImage() {
        Glide.with(requireContext()).load(postsData?.imagesAll?.get(1))
            .into(binding.ivImage1)
        Glide.with(requireContext()).load(postsData?.profile_image)
            .into(binding.ivImage2)
    }

    private fun videoPlayer() {
        val player = binding.videoView
        val url = postsData?.videos
        player.setVideoURI(Uri.parse(url))

        player.start()
        Toast.makeText(
            requireContext(),
            "Video playing ... ",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}