package com.example.sunnyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class SunnyWeatherApplication:Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        const val TOKEN="填入你申请到的令牌值"
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context=applicationContext
    }
}