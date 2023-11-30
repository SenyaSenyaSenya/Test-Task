package com.example.testtask.ui

import BottomNavigationHelper
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var noSavedTextView: TextView
    private lateinit var noSavedLinear: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        setContentView(R.layout.activity_bookmark_screen)
        noSavedLinear = findViewById(R.id.no_saved)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationHelper = BottomNavigationHelper(this, bottomNavigationView)
        recyclerView = findViewById(R.id.recyclerViewDB)
        db = ImageDatabase.getInstance(applicationContext)
        imageAdapter = ImageAdapter(this, db)
        noSavedTextView = findViewById(R.id.noSavedTextView)
        val context: Context = this
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        bottomNavigationView.menu.findItem(R.id.menu_bookmark).isChecked = true
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = imageAdapter
        noSavedTextView = findViewById(R.id.noSavedTextView)
        noSavedTextView.setOnClickListener(navigateToHomeScreen(context))
        initialiseAdapter()
        displayImagesFromDatabase()
    }

    private fun initialiseAdapter() {
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        imageAdapter = ImageAdapter(this, db)
        recyclerView.adapter = imageAdapter
    }

    private fun displayImagesFromDatabase() {
        CoroutineScope(Dispatchers.Main).launch {
            val imageCount = db.imageDao().getImageCount()
            noSavedLinear.visibility =
                if (imageCount == null || imageCount == 0) View.VISIBLE else View.INVISIBLE

            val images = db.imageDao().getAllImages()
            imageAdapter.setImages(images)
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    private fun navigateToHomeScreen(context: Context): View.OnClickListener {
        return View.OnClickListener {
            val intent = Intent(context, HomeScreenActivity::class.java)
            context.startActivity(intent)
            if (context is Activity) {
                context.overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                context.finish()
            }
        }
    }
}