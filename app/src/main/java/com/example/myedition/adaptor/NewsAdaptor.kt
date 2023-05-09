package com.example.myedition.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myedition.R
import com.example.myedition.models.Article

class NewsAdaptor: RecyclerView.Adapter<NewsAdaptor.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)
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
            LayoutInflater.from(parent.context).inflate(R.layout.item_pre_view,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {

            var artimageview = findViewById<ImageView>(R.id.ivArticleImage)
            Glide.with(this).load(article.urlToImage).into(artimageview)
            var tvsource = findViewById<TextView>(R.id.tvSource)
            tvsource.text = article.source?.name
            var tvtitle = findViewById<TextView>(R.id.tvTitle)
            tvtitle.text = article.title
            var tvdescription = findViewById<TextView>(R.id.tvDescription)
            tvdescription.text =  article.description
            var tvpublished = findViewById<TextView>(R.id.tvPublishedAt)
            tvpublished.text=article.publishedAt
            setOnClickListener {
                onItemClicklistener?.let { it(article) }
            }
        }
    }
    private var onItemClicklistener: ((Article)->Unit)?=null
    fun setonItemclicklistener(Listener: (Article)->Unit){
        onItemClicklistener = Listener
    }
}