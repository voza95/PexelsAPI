package com.example.pexelsapi.retrofit

import com.example.pexelsapi.retrofit.response.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiInterface {

    @GET("search")
    fun getSearchList(
        @Query("query") query:String?="",
        @Query("per_page") per_page:Int?=9,
        @Query("page") page:Int?=1
    ): Call<SearchListResponse>
}