package com.assignment.tensoriot.network

import com.assignment.tensoriot.model.weatherData.WeatherData
import com.assignment.tensoriot.utility.Constants.API_HEADER
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @Headers(API_HEADER)
    @GET
    fun getData(@Url url: String): Call<WeatherData>

}