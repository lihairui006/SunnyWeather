package com.example.sunnyweather.logic.network

import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.DailyResponse
import com.example.sunnyweather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
/*
使用@GET注解来获取要访问的API接口，并且还使用@Path注解来向请求接口中动态传入经纬度的坐标
这两个方法的返回值分别被声明成了Call<RealTimeWeather>和Call<DailyResponse>
 */
interface WeatherService {

    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    //getRealtimeWeather()方法用于获取实时的天气信息
    fun getRealtimeWeather(@Path("lng")lng:String,@Path("lat")lat:String):
            Call<RealtimeResponse>

    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    //getDailyWeather()方法用于获取未来的天气信息
    fun getDailyWeather(@Path("lng")lng:String,@Path("lat")lat:String):
            Call<DailyResponse>
}