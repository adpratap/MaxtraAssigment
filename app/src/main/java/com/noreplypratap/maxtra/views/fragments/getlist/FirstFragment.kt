package com.noreplypratap.maxtra.views.fragments.getlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.noreplypratap.maxtra.R
import com.noreplypratap.maxtra.databinding.FragmentFirstBinding
import com.noreplypratap.maxtra.utils.isOnline
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val postAdapter = PostAdapter()
        binding.rvPosts.adapter = postAdapter
        setupOnClick(postAdapter)

        getListOfPosts()
        subUI(postAdapter)

        binding.fab.setOnClickListener {
            binding.progressBar.visibility = View.GONE
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun subUI(adapter: PostAdapter) {
        mainViewModel.getPostsResponse.observe(viewLifecycleOwner) {
            it.let { response ->
                response.data?.let { it1 -> adapter.setPostData(it1) }
                binding.progressBar.visibility = View.GONE
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun getListOfPosts() {
        binding.progressBar.visibility = View.VISIBLE
        if (requireContext().isOnline()){
            mainViewModel.getPosts(100)
        }else{
            Toast.makeText(
                requireContext(),
                "No Internet!!",
                Toast.LENGTH_SHORT
            ).show()
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