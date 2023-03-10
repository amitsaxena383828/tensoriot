package com.assignment.tensoriot.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.tensoriot.model.weatherData.WeatherData
import com.assignment.tensoriot.network.ApiInterface
import com.assignment.tensoriot.network.RequestResult
import com.assignment.tensoriot.utility.Constants
import com.terasol.ublamp.network.BasicAuthClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherDetailVM :ViewModel(){
    var stringMutableLiveData: MutableLiveData<RequestResult<Any?>>? = null
    fun getData(latitude: String, longitude: String): LiveData<RequestResult<Any?>> {
        if (stringMutableLiveData == null) {
            stringMutableLiveData = MutableLiveData<RequestResult<Any?>>()
            loadData(latitude,longitude)
        }
        return stringMutableLiveData as MutableLiveData<RequestResult<Any?>>
    }

    fun loadData(latitude: String, longitude: String) {
        viewModelScope.launch {
            val call =
                BasicAuthClient<ApiInterface>().create(ApiInterface::class.java)
                    .getData(Constants.BASE_URL+"weather?lat="+latitude+"&lon="+longitude+"&appid="+Constants.API_KEY)
            call.enqueue(object : Callback<WeatherData> {
                override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                    Log.e("Amit", t.message, t)
                }

                override fun onResponse(
                    call: Call<WeatherData>,
                    response: Response<WeatherData>
                ) {
                    if (response.isSuccessful) {
                        Log.i("Amit", "Loaded.")
                        stringMutableLiveData?.postValue(RequestResult.success(response.body()))
                    } else {
                        Log.e("Amit", "Error: ${response.code()} ${response.message()}")
                        stringMutableLiveData?.postValue(RequestResult.error(response.message() + "\t" + response.code()))
                    }
                }
            })
        }
    }
}