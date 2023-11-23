@file:Suppress("DEPRECATION")

package com.example.testtask.ui

import BottomNavigationHelper
import PexelsPhotoAdapter
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.testtask.R
import com.example.testtask.util.EditTextUtils
import com.example.testtask.util.NetworkUtils
import com.example.testtask.viewmodels.PexelsViewModel
import com.example.testtask.viewmodels.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var adapter: PexelsPhotoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    var isScrolling = false
    var isPhotoSearch = false
    var currentItems = 0
    var totalItems = 0
    var scrollOutItems = 0
    var searchQuery = String()
    private lateinit var tryAgainTextView: TextView
    private lateinit var viewModel: PexelsViewModel
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var bottomNavigationHelper: BottomNavigationHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        setContentView(R.layout.activity_home_screen)
        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        val factory = ViewModelFactory()
        viewModel = ViewModelProviders.of(this, factory).get(PexelsViewModel::class.java)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationHelper = BottomNavigationHelper(this, bottomNavigationView)
        tryAgainTextView = findViewById<TextView>(R.id.tryAgainTextView)
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

    private fun performSearch(query: String) {
        if (query.isNotEmpty()) {
            isPhotoSearch = true
            searchQuery = query
            requestForData(isPhotoSearch, searchQuery, true)
        } else {
            Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show()
        }
    }


    private fun initialiseAdapter() {
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        observeData()
    }

    private fun observeData() {
        adapter = PexelsPhotoAdapter(this, viewModel.arrayList)
        recyclerView.adapter = adapter

        val noResultsLayout = findViewById<LinearLayout>(R.id.no_results)
        if (viewModel.arrayList.isEmpty()) {
            noResultsLayout.visibility = View.VISIBLE
        } else {
            noResultsLayout.visibility = View.GONE
        }
    }

    private fun requestForData(isSearch: Boolean, searchQuery: String, isAction: Boolean) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            try {
                viewModel.getPexelsData(this, isSearch, searchQuery, isAction, adapter)
                tryAgainTextView.visibility = View.GONE // Скрываем tryAgainTextView, если интернет-соединение восстановлено
            } catch (e: Exception) {
                val errorMessage = "Network request failed: ${e.message}"
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            tryAgainTextView.visibility = View.VISIBLE
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show()
        }
    }
    fun clearSearch(view: View) {
        searchEditText.text.clear()
    }
    @SuppressLint("ResourceType")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.xml.search_menu, menu)
        return true
    }
}