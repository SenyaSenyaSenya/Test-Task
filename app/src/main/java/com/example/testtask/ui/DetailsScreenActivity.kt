package com.example.testtask.ui

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.*
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
import kotlin.math.exp

class DetailsScreenActivity : AppCompatActivity() {

    var originalUrl = String()
    var mediumUrl = String()
    lateinit var photoView: ImageView
    lateinit var backButton: ImageView
    lateinit var progress: ProgressBar
    lateinit var titleName: TextView
    private var isBookmarkEnabled = false
    private lateinit var database: ImageDatabase
    lateinit var errorLayout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_screen)
        val downloadButton = findViewById<ImageView>(R.id.downloadButton)
        val bookmarkButton = findViewById<Button>(R.id.button_bookmark)
        bookmarkButton.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.l_gray))
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
        errorLayout = findViewById(R.id.no_image)
        val context = this
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
            val downloadId = downloadManager.enqueue(request)
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val action = intent?.action
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                        val downloadIdCompleted = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                        if (downloadId == downloadIdCompleted) {
                            val query = DownloadManager.Query().setFilterById(downloadId)
                            val cursor = downloadManager.query(query)
                            if (cursor.moveToFirst()) {
                                val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                                if (columnIndex != -1) {
                                    val status = cursor.getInt(columnIndex)
                                    if (status == DownloadManager.STATUS_FAILED) {
                                        showErrorMessage("Failed to download photo")
                                    }
                                } else {
                                    showErrorMessage("Failed to download photo")
                                }
                            } else {
                                showErrorMessage("Failed to download photo")
                            }
                            cursor.close()
                        }
                    }
                }
            }
            val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            registerReceiver(receiver, filter)
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (isImageBookmarked(originalUrl)) {
                    bookmarkButton.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_bookmark_enabled,
                        0
                    )
                    bookmarkButton.compoundDrawablePadding = 0
                } else {
                    bookmarkButton.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_bookmark_disabled,
                        0
                    )
                    bookmarkButton.compoundDrawablePadding = 0
                }
            } catch (e: Exception) {
                runOnUiThread {
                    errorLayout.visibility = View.VISIBLE
                    val explore = findViewById<TextView>(R.id.exploreTextView)
                    explore.setOnClickListener{
                        val intent = Intent(context, HomeScreenActivity::class.java)
                        context.startActivity(intent)
                        if (context is Activity) {
                            context.overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                            context.finish()
                        }
                    }
                }
            }
        }
        bookmarkButton.setOnClickListener {
            val imageEntity = ImageEntity(
                originalUrl = originalUrl,
                photographer = photographer,
                mediumUrl = mediumUrl
            )
            isBookmarkEnabled = !isBookmarkEnabled
            CoroutineScope(Dispatchers.IO).launch {
                val imageDao = database.imageDao()

                if (isImageBookmarked(originalUrl)) {
                    imageDao.deleteImageByOriginalUrl(originalUrl)
                    runOnUiThread {
                        bookmarkButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_bookmark_disabled,
                            0
                        )
                        bookmarkButton.compoundDrawablePadding = 0
                    }
                } else {
                    imageDao.insertImage(imageEntity)
                    runOnUiThread {
                        bookmarkButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_bookmark_enabled,
                            0
                        )
                        bookmarkButton.compoundDrawablePadding = 0
                    }
                }
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }
    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    suspend fun isImageBookmarked(originalUrl: String): Boolean {
        val imageDao = database.imageDao()
        val existingImage = imageDao.getImageByOriginalUrl(originalUrl)
        return existingImage != null
    }
}