package br.com.weatherapp.api

import br.com.weatherapp.entity.FindResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("find")
    fun find(
        @Query("q")
        cityName: String,
        @Query("units")
        unit: String,
        @Query("lang")
        lang: String,
        @Query("appid")
        appId: String
    ) : Call<FindResult>

    @GET("group")
    fun findFavorites(
        @Query("id")
        cityName: String,
        @Query("units")
        unit: String,
        @Query("lang")
        lang: String,
        @Query("appid")
        appId: String
    ) : Call<FindResult>

}