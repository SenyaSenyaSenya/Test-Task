package com.example.testtask.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testtask.ui.adapter.PexelsPhotoAdapter
import com.example.testtask.viewmodels.PexelsViewModel
import com.example.testtask.viewmodels.ViewModelFactory
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.testtask.R
import com.example.testtask.util.EditTextUtils
import com.example.testtask.util.NetworkUtils

class HomeScreen : AppCompatActivity() {

    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var adapter: PexelsPhotoAdapter
    private lateinit var recyclerView: RecyclerView
    var isScrolling = false
    var isPhotoSearch = false
    var currentItems = 0
    var totalItems = 0
    var scrollOutItems = 0
    var searchQuery = String()
    private lateinit var viewModel: PexelsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        val application = requireNotNull(this).application
        val factory = ViewModelFactory()
        viewModel = ViewModelProviders.of(this, factory).get(PexelsViewModel::class.java)

        initialiseAdapter()
        requestForData(false, "", false)
        val searchEditText = findViewById<EditText>(R.id.editText)
        EditTextUtils.addClearButton(searchEditText)

        initialiseAdapter()
        requestForData(false, "", false)
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

    // Initialize GridLayoutManager for recyclerView
    private fun initialiseAdapter() {
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        observeData()
    }

    // Initialize adapter for recyclerView
    fun observeData() {
        adapter = PexelsPhotoAdapter(this, viewModel.arrayList)
        recyclerView.adapter = adapter
    }

    // Request for parse data to viewModel
    fun requestForData(isSearch: Boolean, searchQuery: String, isAction: Boolean) {

        if (NetworkUtils.isNetworkAvailable(this))
            viewModel.getPexelsData(this, isSearch, searchQuery, isAction, adapter)
        else
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show()
    }

    @SuppressLint("ResourceType")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.xml.search_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.search) {
            val alert = AlertDialog.Builder(this)
            val editText = EditText(this)
            editText.textAlignment = View.TEXT_ALIGNMENT_CENTER
            alert.setMessage("Enter Category e.g. Nature")
            alert.setTitle("Search Wallpaper")
            alert.setView(editText)
            alert.setPositiveButton(
                "Yes"
            ) { dialogInterface, i ->
                val query = editText.text.toString().toLowerCase()

                if (!query.isNullOrEmpty()) {
                    isPhotoSearch = true
                    searchQuery = query
                    requestForData(isPhotoSearch, searchQuery, true)
                }
            }
            alert.setNegativeButton(
                "No"
            ) { dialogInterface, i -> }
            alert.show()
        } else if (item.itemId == R.id.allPhotoSearch) {
            isPhotoSearch = false
            requestForData(isPhotoSearch, "", true)
        }

        return super.onOptionsItemSelected(item)
    }
}