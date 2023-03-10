package com.assignment.tensoriot.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.assignment.tensoriot.R
import com.assignment.tensoriot.databinding.ActivityWeatherDataBinding

class WeatherDataActivity : AppCompatActivity() {
    lateinit var binding:ActivityWeatherDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWeatherDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}