package com.dicoding.dicodingeventapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("events")
        fun getEvents(@Query("active") active: Int): Call<EventResponse>

    @GET("events/{id}")
        fun getEventDetail(@Path("id") id: String): Call<DetailResponse>

    @GET("events")
        fun searchEvents(@Query("active") active: Int, @Query("q") keyword: String): Call<EventResponse>
    @GET("events")
    fun limitEventsUpcoming(@Query("active") active: Int, @Query("limit") limit: Int): Call<EventResponse>
    @GET("events")
    fun limitEventsFinished(@Query("active") active: Int, @Query("limit") limit: Int): Call<EventResponse>
}
