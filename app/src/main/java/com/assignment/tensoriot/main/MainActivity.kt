package com.assignment.tensoriot.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import com.assignment.tensoriot.databinding.ActivityMainBinding
import com.assignment.tensoriot.utility.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        auth = Firebase.auth

        mainBinding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        mainBinding.btnLogin.setOnClickListener {
            mainBinding.pb.visibility= View.VISIBLE
            loginUser(mainBinding.etEmail.text.toString(), mainBinding.etPassword.text.toString())
        }
        getLocation()

    }


    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    startProfileActivity(user)
                } else {

                    Toast.makeText(
                        baseContext, "Authentication failed.${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                mainBinding.pb.visibility= View.GONE
            }
    }



    private fun startProfileActivity(user: FirebaseUser?) {
        val userString = Tensoriot.getGsonInstance().toJson(user)
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(Constants.EXTRA_PARAM_USER_PROFILE, userString)
        startActivity(intent)
    }





}