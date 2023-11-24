package com.example.testtask.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ImageEntity::class], version = 1)
abstract class ImageDatabase : RoomDatabase() {

    abstract fun imageDao(): ImageDao

    suspend fun deleteImage(originalUrl: String) {
        val imageDao = imageDao()
        imageDao.deleteImageByOriginalUrl(originalUrl)
    }

    companion object {
        private var instance: ImageDatabase? = null

        fun getInstance(context: Context): ImageDatabase {
            if (instance == null) {
                synchronized(ImageDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ImageDatabase::class.java,
                        "app_database"
                    ).build()
                }
            }
            return instance!!
        }
    }
}