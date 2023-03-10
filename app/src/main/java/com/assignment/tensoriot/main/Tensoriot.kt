package com.assignment.tensoriot.main

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson

class Tensoriot: Application(), Application.ActivityLifecycleCallbacks {


    companion object {
        private lateinit var gson: Gson

        private lateinit var instance: Tensoriot

        @JvmStatic
        fun getInstance() = instance

        @JvmStatic
        fun getGsonInstance() = gson
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        gson = Gson()
        registerActivityLifecycleCallbacks(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    fun isTablet(context: Context): Boolean {
        return ((context.getResources().getConfiguration().screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE)
    }


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        //activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    override fun onActivityStarted(activity: Activity) {
        //use when needed
    }

    override fun onActivityResumed(activity: Activity) {
        //use when needed
    }

    override fun onActivityPaused(activity: Activity) {
        //use when needed
    }

    override fun onActivityStopped(activity: Activity) {
        //use when needed
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        //use when needed
    }

    override fun onActivityDestroyed(activity: Activity) {
        //use when needed
    }
}