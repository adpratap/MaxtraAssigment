package com.noreplypratap.maxtra.views.fragments.details

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

        getData()
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

    private fun setView() {
        //Set Views
        Toast.makeText(requireContext(), postsData!!.name, Toast.LENGTH_SHORT).show()
        binding.name.text = postsData!!.name
        binding.desc.text = postsData!!.discription
        binding.postType.text = postsData!!.post_type
        binding.userid.text = postsData!!.user_id.toString()
        binding.likes.text = postsData!!.like_count.toString()
        // add More Data

        if (postsData?.videos != null){
            videoPlayer()
        }

        if (postsData?.images != null){
            setupImage()
        }

    }

    private fun setupImage() {
        Glide.with(this).load(postsData?.imagesAll?.get(1))
            .into(binding.ivImage)
    }

    private fun videoPlayer() {
        val player = binding.videoView
        val url = postsData?.videos
        player.setVideoURI(Uri.parse(url))

        player.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}