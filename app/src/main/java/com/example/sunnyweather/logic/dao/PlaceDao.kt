package com.example.sunnyweather.logic.dao

import android.content.Context
import android.provider.Settings.Global.putString
import androidx.core.content.edit
import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.Place
import com.google.gson.Gson

object PlaceDao {
    /*
    用于将Place对象存储到SharedPreferences文件中，先通过GSON将Place对象转化成一个JSON字符串，
    然后就可以用字符串存储的方式保存数据了
     */
    fun savePlace(place: Place){
        sharedPreferences().edit{
            putString("place",Gson().toJson(place))
        }
    }

    /*
    在getSavedPlace方法中，我们先将JSON字符串从sharedPreferences文件中读取出来，然后通过GSON将JSON字符串解析成
    Place对象并返回
     */
    fun getSavedPlace():Place{
        val placeJson=sharedPreferences().getString("place","")
        return Gson().fromJson(placeJson,Place::class.java)
    }

    //判断是否有数据已经被存储
    fun isPlaceSaved ()= sharedPreferences().contains("place")

    private fun sharedPreferences()=SunnyWeatherApplication.context.
        getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)
}