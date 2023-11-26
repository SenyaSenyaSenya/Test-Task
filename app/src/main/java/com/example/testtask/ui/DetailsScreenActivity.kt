package com.example.testtask.ui

import android.app.DownloadManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.testtask.R
import com.example.testtask.data.ImageDatabase
import com.example.testtask.data.ImageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsScreenActivity : AppCompatActivity() {

    var originalUrl = String()
    var mediumUrl = String()
    lateinit var photoView: ImageView
    lateinit var backButton: ImageView
    lateinit var progress: ProgressBar
    lateinit var titleName: TextView
    private lateinit var database: ImageDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)
        val downloadButton = findViewById<ImageView>(R.id.downloadButton)
        val bookmarkButton = findViewById<ImageView>(R.id.button_bookmark)
        titleName = findViewById(R.id.title_name)
        val intent = intent
        originalUrl = intent.getStringExtra("originalUrl").toString()
        photoView = findViewById(R.id.photoView)
        backButton = findViewById(R.id.back_button)
        progress = findViewById(R.id.progress)
        mediumUrl = intent.getStringExtra("mediumUrl").toString()
        val photographer = intent.getStringExtra("photographer")
        titleName = findViewById(R.id.title_name)
        titleName.text = photographer


        Glide.with(this)
            .load(originalUrl).placeholder(R.drawable.placeholder_image)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    p0: GlideException?,
                    p1: Any?,
                    p2: Target<Drawable>?,
                    p3: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    p0: Drawable?,
                    p1: Any?,
                    p2: Target<Drawable>?,
                    p3: DataSource?,
                    p4: Boolean
                ): Boolean {
                    progress.visibility = View.GONE
                    return false
                }
            })
            .into(photoView)
        database = Room.databaseBuilder(
            applicationContext,
            ImageDatabase::class.java,
            "app_database"
        ).build()
        downloadButton.setOnClickListener {
            val url = originalUrl

            val request = DownloadManager.Request(Uri.parse(url))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle("Downloading Photo")
                .setDescription("Downloading photo from $url")
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_PICTURES,
                    "Photo.jpg"
                )

            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
        }
        bookmarkButton.setOnClickListener {
            val imageEntity = ImageEntity(
                originalUrl = originalUrl,
                photographer = photographer,
                mediumUrl = mediumUrl
            )

            CoroutineScope(Dispatchers.IO).launch {
                val imageDao = database.imageDao()

                val existingImage = imageDao.getImageByOriginalUrl(originalUrl)
                if (existingImage != null) {
                    imageDao.deleteImageByOriginalUrl(originalUrl)
                } else {
                    imageDao.insertImage(imageEntity)
                }
            }
        }
        backButton.setOnClickListener {
            finish()
        }
    }
}