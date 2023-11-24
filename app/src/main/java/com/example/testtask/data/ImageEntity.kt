package com.example.testtask.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalUrl: String?,
    val photographer: String?,
    val mediumUrl: String?
)