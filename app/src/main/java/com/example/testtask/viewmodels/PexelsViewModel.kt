import android.app.AlertDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.testtask.model.PexelsImageSource
import com.example.testtask.model.PexelsImageWrapper
import com.example.testtask.ui.featured.CollectionTitle
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url

class PexelsViewModel : ViewModel() {

    private var pageNumber = 1
    internal var arrayList = ArrayList<PexelsImageWrapper>()
    private val apiKey = "MNmAc04M8gHVwC2sAPybzs4o1E4jnSEBgvo3HmlTYp3mU0mKvbombz7p"

    fun getPexelsData(
        context: Context,
        isSearch: Boolean,
        searchQuery: String,
        isAction: Boolean,
        adapter: PexelsPhotoAdapter
    ) {
        if (isAction) {
            arrayList.clear()
            pageNumber = 1
        }
        fetchPhotosFromApi(context, isSearch, searchQuery, adapter)
    }

    private fun fetchPhotosFromApi(
        context: Context,
        isSearch: Boolean,
        searchQuery: String,
        adapter: PexelsPhotoAdapter
    ) {
        val baseUrl = "https://api.pexels.com/v1/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()

        val service = retrofit.create(PexelsService::class.java)

        val call: Call<PexelsApiResponse> = if (isSearch) {
            service.searchPhotos(searchQuery, pageNumber, apiKey)
        } else {
            service.getCuratedPhotos(pageNumber, 30, apiKey)
        }

        call.enqueue(object : Callback<PexelsApiResponse> {
            override fun onResponse(
                call: Call<PexelsApiResponse>,
                response: Response<PexelsApiResponse>
            ) {
                if (response.isSuccessful) {
                    val pexelsApiResponse = response.body()
                    if (pexelsApiResponse != null && pexelsApiResponse.photos != null) {
                        val photos = pexelsApiResponse.photos
                        if (photos.isNotEmpty()) {
                            for (photo in photos) {
                                val id = photo.id
                                val photographer = photo.photographer
                                val source = photo.source
                                val pexelsImageWrapper =
                                    PexelsImageWrapper(id, photographer, source)
                                arrayList.add(pexelsImageWrapper)
                            }
                            adapter.notifyDataSetChanged()
                            pageNumber++
                        } else {
                            showNoDataFoundDialog(context)
                        }
                    } else {
                        showNoDataFoundDialog(context)
                    }
                } else {
                    handleRequestError()
                }
            }

            override fun onFailure(call: Call<PexelsApiResponse>, t: Throwable) {
                t.printStackTrace()
                handleRequestError()
            }
        })
    }

    private fun showNoDataFoundDialog(context: Context) {
        val dialog = AlertDialog.Builder(context)
            .setTitle("No data found")
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun handleRequestError() {
        println("Error occurred while executing the request")
    }

    interface PexelsService {
        @GET
        fun getPhotos(
            @Url url: String,
            @Header("Authorization") apiKey: String
        ): Call<PexelsApiResponse>

        @GET("curated")
        fun getCuratedPhotos(
            @Query("page") page: Int,
            @Query("per_page") perPage: Int,
            @Header("Authorization") apiKey: String
        ): Call<PexelsApiResponse>
        @GET("featured_collections")
        fun getFeaturedCollections(): Call<List<CollectionTitle>>
        @GET("search/")
        fun searchPhotos(
            @Query("query") query: String,
            @Query("page") page: Int,
            @Header("Authorization") apiKey: String
        ): Call<PexelsApiResponse>
    }

    data class PexelsApiResponse(
        @SerializedName("photos")
        val photos: List<PexelsImageWrapper>
    )
}