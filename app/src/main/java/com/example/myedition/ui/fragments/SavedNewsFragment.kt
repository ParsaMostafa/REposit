package com.example.myedition.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myedition.NewsActivity
import com.example.myedition.R
import com.example.myedition.adaptor.NewsAdaptor
import com.example.myedition.databinding.FragmentSavedNewsBinding
import com.example.myedition.ui.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var binding: FragmentSavedNewsBinding
    lateinit var newsAdaptor: NewsAdaptor


    override  fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentSavedNewsBinding.bind(view)
        viewModel = (activity as NewsActivity).viewModel

        setupRecyclerView()
        newsAdaptor.setonItemclicklistener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_savedNewsFragment_to_articleFragment,
                bundle)
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(

            ItemTouchHelper.UP or ItemTouchHelper.DOWN ,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
                val article = newsAdaptor.differ.currentList[position]
                viewModel.deletearticle (article)
                Snackbar.make(view,"Successfully delete article",Snackbar.LENGTH_LONG).apply {
                    setAction("undo"){
                        viewModel.savearticle(article)
                    }
                    show()
                }

            }


        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerViewsavednews)
        }


        viewModel.newsRepository.getBreakingNews("us","business").observe(viewLifecycleOwner, Observer {
            articles-> newsAdaptor.differ.submitList(articles)
        })



            }
    private fun setupRecyclerView(){
                newsAdaptor = NewsAdaptor()

                binding.recyclerViewsavednews.apply {
                    adapter =newsAdaptor
                    layoutManager = LinearLayoutManager(activity)
    }


}}

