package com.example.testtask.ui.featured

import retrofit2.http.GET

interface PexelsApiService {
    @GET("featured-collections")
    suspend fun getFeaturedCollections(): List<FeaturedCollection>
}