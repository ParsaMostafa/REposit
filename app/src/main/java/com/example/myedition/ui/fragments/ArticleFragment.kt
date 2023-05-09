package com.example.myedition.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.example.myedition.NewsActivity
import com.example.myedition.R
import com.example.myedition.databinding.FragmentArticleBinding
import com.example.myedition.models.Article
import com.example.myedition.ui.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar


class ArticleFragment : Fragment(R.layout.fragment_article) {
    lateinit var viewModel: NewsViewModel
    lateinit var binding: FragmentArticleBinding
     val args : ArticleFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentArticleBinding.bind(view)
        viewModel = (activity as NewsActivity).viewModel
        var article = args.article
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }
        binding.floatingActionButton.setOnClickListener { viewModel.savearticle(article)
        Snackbar.make(view ,"article saved succsefuly",Snackbar.LENGTH_SHORT).show()


        }


    }

}

