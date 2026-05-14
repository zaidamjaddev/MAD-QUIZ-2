package com.example.quizapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quizapplication.databinding.ItemNewsBinding

class NewsAdapter(private val onItemClick: (Article) -> Unit) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var articles: List<Article> = emptyList()

    fun setArticles(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    inner class NewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.newsTitle.text = article.title
            binding.sourceName.text = article.source.name
            binding.publishedAt.text = article.publishedAt
            
            Glide.with(binding.root.context)
                .load(article.image)
                .placeholder(android.R.drawable.ic_menu_report_image)
                .into(binding.newsImage)

            binding.root.setOnClickListener {
                onItemClick(article)
            }
        }
    }
}