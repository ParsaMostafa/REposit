package com.example.myedition.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.myedition.NewsActivity
import com.example.myedition.R
import com.example.myedition.adaptor.NewsAdaptor

import com.example.myedition.databinding.FragmentBreakingNewsBinding
import com.example.myedition.models.Article
import com.example.myedition.ui.viewmodel.NewsViewModel
import com.example.myedition.utilities.Constance.Companion.Query_PAGE_SIZE
import com.example.myedition.utilities.Resource

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var binding: FragmentBreakingNewsBinding
    lateinit var newsAdaptor: NewsAdaptor
    val TAG = "breakingNewsFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentBreakingNewsBinding.bind(view)
        viewModel=(activity as NewsActivity).viewModel

        setupRecyclerView()

        newsAdaptor.setonItemclicklistener {
            val bundle = Bundle().apply {
                putParcelable("article",it)
            }
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment,
            bundle)
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Succsess ->{ hideprogressbar()
                response.Data?.let {newsResponse ->
                newsAdaptor.differ.submitList(newsResponse.articles.toList())
                    val totalpages = newsResponse.totalResults
                    if (isLastPage) {
                        binding.rvBreakingNews.setPadding(0,0,0,0)

                    }

                }
            }
                is Resource.Error ->{
                hideprogressbar()
                    response.message?.let {message ->
                 Toast.makeText(activity,"Error:$message",Toast.LENGTH_LONG).show()

                    }

                }
                is Resource.Loading ->{
                    showprogressbar()
                }
            }
        })
    }

    private fun hideprogressbar() {
      binding.paginationProgressBar.visibility =  View.INVISIBLE
        isLoading = false
    }

    private fun showprogressbar() {
        binding.paginationProgressBar.visibility=View.VISIBLE
        isLoading = true
    }
    var isLoading = false
    var isLastPage = false
    var isScrolling= false

    var scrolllistener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager= recyclerView.layoutManager as LinearLayoutManager
            val firstVisibaleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount= layoutManager.itemCount

            val isnotlodindgandnotlastpage = !isLoading && !isLastPage
            val isatlastitem =firstVisibaleItemPosition + visibleItemCount >= totalItemCount
            val isnotatbegiining = firstVisibaleItemPosition >= 0
            val istotalmorethanvisible = totalItemCount >= Query_PAGE_SIZE
            val shouldpaginate = isnotlodindgandnotlastpage && isatlastitem && isnotatbegiining && istotalmorethanvisible && isScrolling

            if (shouldpaginate){
                viewModel.getBreakingNews("us","business")
                isScrolling = false
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true
        }

    }

    private fun setupRecyclerView(){
        newsAdaptor = NewsAdaptor()

        binding.rvBreakingNews.apply {
            adapter =newsAdaptor
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrolllistener)
        }

    }
}

