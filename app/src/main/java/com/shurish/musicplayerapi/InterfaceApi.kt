package com.shurish.musicplayerapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface InterfaceApi {


    @Headers("X-RapidAPI-Key: ab509e10fbmsh127a83d2b4d6b0fp1ed04ejsn7e20981f80b6" ,
        "X-RapidAPI-Host: deezerdevs-deezer.p.rapidapi.com")

    @GET("search")
    fun getData(@Query("q") query : String) : Call<MyData>

}