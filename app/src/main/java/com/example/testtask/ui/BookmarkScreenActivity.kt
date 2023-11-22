package com.example.testtask.ui

import BottomNavigationHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testtask.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class BookmarkScreenActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var bottomNavigationHelper: BottomNavigationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        setContentView(R.layout.activity_bookmark_screen)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationHelper = BottomNavigationHelper(this, bottomNavigationView)

    }

}