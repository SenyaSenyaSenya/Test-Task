package com.example.testtask.ui

import BottomNavigationHelper
import PexelsPhotoAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.testtask.R
import com.example.testtask.data.ImageDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BookmarkScreenActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var bottomNavigationHelper: BottomNavigationHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var db: ImageDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        setContentView(R.layout.activity_bookmark_screen)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationHelper = BottomNavigationHelper(this, bottomNavigationView)
        recyclerView = findViewById(R.id.recyclerViewDB)
        imageAdapter = ImageAdapter()

        db = ImageDatabase.getInstance(applicationContext)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = imageAdapter
        initialiseAdapter()
        displayImagesFromDatabase()
    }
    private fun initialiseAdapter() {
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager


    }
    private fun displayImagesFromDatabase() {
        CoroutineScope(Dispatchers.Main).launch {
            val images = db.imageDao().getAllImages()
            imageAdapter.setImages(images)
        }
    }
}