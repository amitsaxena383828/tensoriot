package com.assignment.tensoriot.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.assignment.tensoriot.databinding.ActivityProfileBinding
import com.assignment.tensoriot.model.user.User
import com.assignment.tensoriot.model.weatherData.WeatherData
import com.assignment.tensoriot.network.RequestResult
import com.assignment.tensoriot.utility.Constants
import com.assignment.tensoriot.viewModel.WeatherDetailVM
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.util.*


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var weatherDetailVM: WeatherDetailVM
    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getUser()
        getLocation()

    }

    fun getUser() {
        val userString: String? = intent.getStringExtra(Constants.EXTRA_PARAM_USER_PROFILE)
        val user =
            Tensoriot.getGsonInstance()
                .fromJson(userString, Firebase.auth.currentUser!!::class.java)
        getUserDetails(user?.uid)
        Log.d("ParamData", "User$user")
    }

    fun getUserDetails(uid: String?) {
        val rootRef = FirebaseDatabase.getInstance().reference
        val userRef = rootRef.child("users")
        userRef.orderByChild("userFirebaseID").equalTo(uid)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.child(uid.toString()).getValue(User::class.java)
                binding.tvUserName.text = user?.username
                binding.tvUserEmail.text = user?.email
                binding.pb.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        userRef.addListenerForSingleValueEvent(eventListener);
    }


    fun hitApi() {
        weatherDetailVM = ViewModelProvider(this)[WeatherDetailVM::class.java]
        weatherDetailVM.getData(latitude, longitude)
            .observe(this, { response ->
                if (response.status == RequestResult.Status.SUCCESS) {
                    val dataa = response.data as WeatherData
                    Log.d("Amit", "Weather$dataa")
                    binding.tvWeatherData.text =
                        "Weather Data\n$dataa"
                } else {
                    Log.d(
                        "this",
                        "Error while retrieving data.\n\n\n\nResponse==\n\n\n" + response.message
                    )

                }
            })

    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        binding.apply {

                            latitude = list[0].latitude.toString()
                            longitude = list[0].longitude.toString()
                            Log.d("AmitLocation", "Latitude\n${list[0].latitude}")
                            Log.d("AmitLocation", "Longitude\n${list[0].longitude}")
                            Log.d("AmitLocation", "Country Name\\n${list[0].countryName}")
                            Log.d("AmitLocation", "Locality\\n${list[0].locality}")
                            Log.d("AmitLocation", "Address\\n${list[0].getAddressLine(0)}")
                            hitApi()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()

            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Debug.getLocation()
            }
        }
    }
}