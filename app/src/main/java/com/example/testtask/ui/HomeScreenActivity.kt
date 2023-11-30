@file:Suppress("DEPRECATION")

package com.example.testtask.ui

import BottomNavigationHelper
import FeaturedCollectionsAdapter
import PexelsPhotoAdapter
import PexelsViewModel
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.testtask.R
import com.example.testtask.ui.featured.CollectionTitle
import com.example.testtask.util.EditTextUtils
import com.example.testtask.util.NetworkUtils
import com.example.testtask.viewmodels.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var pexelsService: PexelsViewModel.PexelsService
    private lateinit var featuredCollectionsRecyclerView: RecyclerView

    private lateinit var adapter: PexelsPhotoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private var isScrolling = false
    private var isPhotoSearch = false
    private var currentItems = 0
    private var totalItems = 0
    private var scrollOutItems = 0
    private var searchQuery = ""
    private lateinit var tryAgainTextView: TextView
    private lateinit var noResultsTextView: TextView
    private lateinit var noResultsLayout: LinearLayout
    private lateinit var viewModel: PexelsViewModel
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var bottomNavigationHelper: BottomNavigationHelper
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        setContentView(R.layout.activity_home_screen)
        recyclerView = findViewById(R.id.recyclerView)
        val factory = ViewModelFactory()
        viewModel = ViewModelProviders.of(this, factory).get(PexelsViewModel::class.java)
        adapter = PexelsPhotoAdapter(this, viewModel.arrayList)
        recyclerView.adapter = adapter

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        progressBar = findViewById(R.id.horizontalProgressBar)
        progressBar.visibility = View.VISIBLE
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationHelper = BottomNavigationHelper(this, bottomNavigationView)
        tryAgainTextView = findViewById<TextView>(R.id.tryAgainTextView)
        noResultsTextView = findViewById(R.id.noResultsTextView)
        noResultsLayout = findViewById(R.id.no_results)
        noResultsTextView.setOnClickListener {
            searchEditText.text.clear()
            loadPopularPhotos()
        }
        tryAgainTextView.setOnClickListener {
            performSearch("")
        }
        observeData()
        initialiseAdapter()
        requestForData(false, "", false)
        searchEditText = findViewById<EditText>(R.id.editText)
        EditTextUtils.addClearButton(searchEditText)
        tryAgainTextView.setOnClickListener {
            if (isPhotoSearch) {
                requestForData(true, searchQuery, false)
            } else {
                requestForData(false, "", false)
            }
        }
        featuredCollectionsRecyclerView = findViewById(R.id.featuredCollectionsRecyclerView)
        featuredCollectionsRecyclerView.layoutManager = LinearLayoutManager(this)
        featuredCollectionsRecyclerView.setHasFixedSize(true)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.pexels.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        pexelsService = retrofit.create(PexelsViewModel.PexelsService::class.java)

        val call = pexelsService.getFeaturedCollections()
        call.enqueue(object : Callback<List<CollectionTitle>> {
            override fun onResponse(
                call: Call<List<CollectionTitle>>,
                response: Response<List<CollectionTitle>>
            ) {
                Log.d("response", response.isSuccessful.toString())
                if (response.isSuccessful) {
                    val collectionTitles = response.body()?.map { it.title } ?: emptyList()
                    val titleAdapter = FeaturedCollectionsAdapter(collectionTitles)
                    featuredCollectionsRecyclerView.adapter = titleAdapter
                    titleAdapter.notifyDataSetChanged()
                    for (title in collectionTitles) {
                        Log.i("Title is", title)
                    }
                } else {
                    Log.e(TAG, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<CollectionTitle>>, t: Throwable) {
                Log.e(TAG, "Request execution error: ${t.message}")
            }
        })

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                performSearch(query)
            }
        })
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                currentItems = layoutManager.childCount
                totalItems = layoutManager.itemCount
                scrollOutItems = layoutManager.findFirstVisibleItemPositions(null)[0]

                if (isScrolling && currentItems + scrollOutItems == totalItems) {
                    isScrolling = false

                    if (isPhotoSearch) {
                        requestForData(true, searchQuery, false)
                    } else {
                        requestForData(false, "", false)
                    }
                }
            }
        })
    }


    private fun initialiseAdapter() {
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun observeData() {
        adapter.notifyDataSetChanged()
    }

    private fun requestForData(isSearch: Boolean, searchQuery: String, isAction: Boolean) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            progressBar.visibility = View.VISIBLE

            try {
                viewModel.getPexelsData(this, isSearch, searchQuery, isAction, adapter, noResultsLayout)
                tryAgainTextView.visibility = View.GONE
            } catch (e: Exception) {
                val errorMessage = "Network request failed: ${e.message}"
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        } else {
            tryAgainTextView.visibility = View.VISIBLE
            noResultsLayout.visibility = View.GONE
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadPopularPhotos() {
        isPhotoSearch = false
        searchQuery = ""
        requestForData(isPhotoSearch, searchQuery, true)
    }

    private fun performSearch(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isNotEmpty()) {
            isPhotoSearch = true
            searchQuery = trimmedQuery
            requestForData(isPhotoSearch, searchQuery, true)
        } else {
            loadPopularPhotos()
        }
    }

    @SuppressLint("ResourceType")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.xml.search_menu, menu)
        return true
    }
}