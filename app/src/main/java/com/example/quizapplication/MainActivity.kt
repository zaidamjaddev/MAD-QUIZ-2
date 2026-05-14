package com.example.quizapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NewsAdapter
    private val apiKey = "f6430372c944cc19e9ae9c7435827902"
    
    private val countries = mapOf(
        "United States" to "us",
        "Pakistan" to "pk",
        "United Kingdom" to "gb",
        "India" to "in",
        "Saudi Arabia" to "sa",
        "UAE" to "ae"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSpinner()

        binding.btnRefresh.setOnClickListener {
            val selectedCountry = countries[binding.countrySpinner.selectedItem.toString()] ?: "us"
            fetchNews(selectedCountry)
        }
    }

    private fun setupRecyclerView() {
        adapter = NewsAdapter { article ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("article", article)
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries.keys.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.countrySpinner.adapter = adapter

        binding.countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val countryName = countries.keys.toList()[position]
                val countryCode = countries[countryName] ?: "us"
                fetchNews(countryCode)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun fetchNews(country: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.errorText.visibility = View.GONE

        val retrofit = Retrofit.Builder()
            .baseUrl("https://gnews.io/api/v4/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(NewsApiService::class.java)
        service.getTopHeadlines(country = country, apiKey = apiKey).enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    binding.recyclerView.visibility = View.VISIBLE
                    adapter.setArticles(response.body()!!.articles)
                } else {
                    showError()
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                showError()
            }
        })
    }

    private fun showError() {
        binding.errorText.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }
}