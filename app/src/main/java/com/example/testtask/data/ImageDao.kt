package com.example.testtask.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface ImageDao {
    @Query("SELECT * FROM images")
    suspend fun getAllImages(): List<ImageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Query("DELETE FROM images WHERE originalUrl = :originalUrl")
    suspend fun deleteImageByOriginalUrl(originalUrl: String)

    @Query("SELECT * FROM images WHERE originalUrl = :originalUrl LIMIT 1")
    suspend fun getImageByOriginalUrl(originalUrl: String): ImageEntity?

    @Query("SELECT COUNT(*) FROM images")
    suspend fun getImageCount(): Int
}