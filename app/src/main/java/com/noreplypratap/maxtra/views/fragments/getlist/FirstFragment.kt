package com.noreplypratap.maxtra.views.fragments.getlist

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.noreplypratap.maxtra.R
import com.noreplypratap.maxtra.databinding.FragmentFirstBinding
import com.noreplypratap.maxtra.viewmodel.MainViewModel
import com.noreplypratap.maxtra.views.adapter.PostAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val postAdapter = PostAdapter()
        binding.rvPosts.adapter = postAdapter
        setupOnClick(postAdapter)
        binding.progressBar.visibility = View.VISIBLE

        mainViewModel.getPosts(100)
        mainViewModel.getPostsResponse.observe(viewLifecycleOwner) {

            it.let { response ->
                response.data?.let { it1 -> postAdapter.setPostData(it1) }
                binding.progressBar.visibility = View.GONE
                postAdapter.notifyDataSetChanged()
            }
        }

        binding.fab.setOnClickListener {
            binding.progressBar.visibility = View.GONE
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    private fun setupOnClick(postAdapter: PostAdapter) {
        postAdapter.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("nextData" , Gson().toJson(it))
            findNavController().navigate(R.id.action_FirstFragment_to_detailsFragment,bundle)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}