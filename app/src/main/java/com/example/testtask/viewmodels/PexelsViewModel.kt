package com.example.testtask.viewmodels

import PexelsPhotoAdapter
import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.testtask.model.PexelsImageWrapper
import org.json.JSONException
import org.json.JSONObject

class PexelsViewModel : ViewModel() {

    var pageNumber = 1
    var lst = MutableLiveData<ArrayList<PexelsImageWrapper>>()
    var newlist = arrayListOf<PexelsImageWrapper>()
    var arrayList = ArrayList<PexelsImageWrapper>()

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

    fun fetchPhotosFromApi(
        context: Context,
        isSearch: Boolean,
        searchQuery: String,
        adapter: PexelsPhotoAdapter
    ) {

        var url = String()
        if (isSearch) {
            url =
                "https://api.pexels.com/v1/search/?page=$pageNumber&per_page=80&query=$searchQuery"
        } else {
            url = "https://api.pexels.com/v1/curated/?page=$pageNumber&per_page=30"
        }

        val request: StringRequest = object : StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val jsonArray = jsonObject.getJSONArray("photos")
                    val length = jsonArray.length()

                    if (length != 0) {
                        for (i in 0 until length) {
                            val `object` = jsonArray.getJSONObject(i)
                            val id = `object`.getInt("id")
                            val objectImages = `object`.getJSONObject("src")
                            val objectphotographer = `object`.getString("photographer")
                            val orignalUrl = objectImages.getString("original")
                            val mediumUrl = objectImages.getString("medium")
                            val pexelsImageWrapper =
                                PexelsImageWrapper(id, objectphotographer, orignalUrl, mediumUrl)
                            arrayList.add(pexelsImageWrapper);
                            //newlist.add(pexelsImageWrapper)
                        }
                        //lst.value=newlist
                        adapter.notifyDataSetChanged()
                        pageNumber++
                    } else {
                        val dialog = AlertDialog.Builder(context)
                        dialog.setTitle("No data found")
                        dialog.setNegativeButton(
                            "Cancel"
                        ) { dialogInterface, i ->
                        }
                        dialog.show()
                    }
                } catch (e: JSONException) {
                }
            }, Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "MNmAc04M8gHVwC2sAPybzs4o1E4jnSEBgvo3HmlTYp3mU0mKvbombz7p"
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(request)
    }

}
