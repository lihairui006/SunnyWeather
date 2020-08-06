package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName


//获取实时天气信息接口数据模型
data class RealtimeResponse(val status:String,val result: Result){

    data class Result(val realtime:Realtime)

    data class Realtime(val skycon:String,val temperature:Float,
                        @SerializedName("air_quality") val airQuery:AirQuality)

    data class AirQuality(val aqi:AQI)

    data class AQI(val chn:Float)
}