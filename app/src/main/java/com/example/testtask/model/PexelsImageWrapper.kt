package com.example.testtask.model

import com.google.gson.annotations.SerializedName

data class PexelsImageWrapper(
    val id: Int,
    val photographer: String?,
    @SerializedName("src") val source: PexelsImageSource?
) {
    val originalUrl: String?
        get() = source?.original

    val mediumUrl: String?
        get() = source?.medium
}

