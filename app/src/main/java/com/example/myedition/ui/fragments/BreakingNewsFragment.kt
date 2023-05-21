package com.example.myedition.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myedition.R
import com.example.myedition.adaptor.NewsAdaptor
import com.example.myedition.databinding.FragmentBreakingNewsBinding
import com.example.myedition.ui.viewmodel.NewsViewModel
import com.example.myedition.utilities.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    private lateinit var viewModel: NewsViewModel
    private lateinit var binding: FragmentBreakingNewsBinding
    private lateinit var newsAdapter: NewsAdaptor
    private var isScrolling: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBreakingNewsBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(NewsViewModel::class.java)

        setupRecyclerView()
        observeBreakingNews()
        newsAdapter.setonItemclicklistener { article ->
            val bundle = Bundle().apply {
                putParcelable("article", article)
            }
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment, bundle)
        }

        viewModel.getBreakingNews()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdaptor()
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(scrollListener)
        }
    }

    private fun observeBreakingNews() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.breakingNews.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        hideProgressBar()
                        resource.data.let { pagingData ->
                            newsAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        resource.message?.let { message ->
                            Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_LONG).show()
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }
            }
        }
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            isScrolling = newState == RecyclerView.SCROLL_STATE_DRAGGING
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            // Perform your logic based on scroll position and isScrolling property
            if (isScrolling && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                // Load more data or perform pagination
                // Example: viewModel.loadMoreData()
            }
        }
    }
}
