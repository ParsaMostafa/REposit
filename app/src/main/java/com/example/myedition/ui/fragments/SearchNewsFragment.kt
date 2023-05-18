package com.example.myedition.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myedition.NewsActivity
import com.example.myedition.R
import com.example.myedition.adaptor.NewsAdaptor
import com.example.myedition.databinding.FragmentSearchNewsBinding
import com.example.myedition.ui.viewmodel.NewsViewModel
import com.example.myedition.utilities.Constance
import com.example.myedition.utilities.Constance.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.myedition.utilities.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment() {
    private lateinit var viewModel: NewsViewModel
    private lateinit var binding: FragmentSearchNewsBinding
    private lateinit var newsAdaptor: NewsAdaptor
    private val TAG = "SearchNewsFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_news,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchNewsBinding.bind(view)
        viewModel = (activity as NewsActivity).viewModel

        setupRecyclerView()

        newsAdaptor.setonItemclicklistener {
            val bundle = Bundle().apply {
                putParcelable("article",it)
            }
            findNavController().navigate(R.id.action_searchNewsFragment_to_articleFragment,
                bundle)
        }

        var job : Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                editable?.let {
                    delay(SEARCH_NEWS_TIME_DELAY)
                    if(editable.toString().isNotEmpty()){
                        viewModel.searchNews(editable.toString(),1)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.searchingNews.collect { response ->
                when(response){
                    is Resource.Success ->{
                        hideprogressbar()
                        response.data?.let {newsResponse ->
                            newsAdaptor.differ.submitList(newsResponse.articles.toList())
                            val totalpages = newsResponse.totalResults / Constance.Query_PAGE_SIZE + 2
                            isLastPage = viewModel.searchingNewsPage  == totalpages
                            if (isLastPage){
                                binding.rvSearchNews.setPadding(0, 0 ,0, 0)
                            }
                        }
                    }
                    is Resource.Error ->{
                        hideprogressbar()
                        response.message?.let {message ->
                            Toast.makeText(activity,"Error:$message", Toast.LENGTH_LONG).show()

                        }
                    }
                    is Resource.Loading ->{
                        showprogressbar()
                    }
                }
            }
        }
    }

    private fun hideprogressbar() {
        binding.progressBar2.visibility=View.INVISIBLE
        isLoading = false
    }

    private fun showprogressbar() {
        binding.progressBar2.visibility=View.VISIBLE
        isLoading = true
    }

    private var isLoading = false
    private var isLastPage = false
    private var isScrolling= false

    private val scrolllistener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager= recyclerView.layoutManager as LinearLayoutManager
            val firstVisibaleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount= layoutManager.itemCount

            val isnotlodindgandnotlastpage = !isLastPage && !isLastPage
            val isatlastitem =firstVisibaleItemPosition + visibleItemCount >= totalItemCount
            val isnotatbegiining = firstVisibaleItemPosition >= 0
            val istotalmorethanvisible = totalItemCount >= Constance.Query_PAGE_SIZE
            val shouldpaginate = isnotlodindgandnotlastpage && isatlastitem && isnotatbegiining && istotalmorethanvisible && isScrolling

            if (shouldpaginate){
                viewModel.searchNews(binding.etSearch.text.toString(),1)
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

        binding.rvSearchNews.apply {
            adapter =newsAdaptor
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(scrolllistener)
        }
    }
}