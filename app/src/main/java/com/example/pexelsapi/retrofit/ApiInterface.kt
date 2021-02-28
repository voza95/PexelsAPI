package com.example.pexelsapi.retrofit

import com.example.pexelsapi.retrofit.response.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiInterface {

    @GET("search")
    fun getSearchList(
        @Query("query") query:String?="nature",
        @Query("per_page") per_page:String?="1"
    ): Call<SearchListResponse>
}