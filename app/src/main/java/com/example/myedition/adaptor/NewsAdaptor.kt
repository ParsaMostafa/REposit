package com.example.myedition.adaptor

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myedition.databinding.ItemPreViewBinding
import com.example.myedition.models.Article

class NewsAdaptor: RecyclerView.Adapter<NewsAdaptor.ArticleViewHolder>() {

    inner class ArticleViewHolder(val binding:ItemPreViewBinding) : RecyclerView.ViewHolder(binding.root)



    private val differcallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url==newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem==newItem
        }

    }
    val differ = AsyncListDiffer(this,differcallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
       return ArticleViewHolder(
           ItemPreViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        val binding = holder.binding // get the binding instance from the view holder

        // Use View Binding to access the views in the item layout
        binding.ivArticleImage.let {
            Glide.with(it).load(article.urlToImage).into(it)
        }
        binding.tvSource.text = article.source?.name
        binding.tvTitle.text = article.title
        binding.tvDescription.text =  article.description
        binding.tvPublishedAt.text = article.publishedAt
            binding.root.setOnClickListener {
                onItemClicklistener?.let { it(article) }
            }

        }
    private var onItemClicklistener: ((Article)->Unit)?=null
    fun setonItemclicklistener(Listener: (Article)->Unit){
        onItemClicklistener = Listener
    }

}
