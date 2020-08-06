package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

data class PlaceResponse (val status:String,val places:List<Place>)

//使用@SerializedName的注解方式为了让kotlin和JSON字段之间建立映射关系
data class Place(val name:String, val location: Location,
                 @SerializedName("formatted_address")val address:String)

data class Location(val lng:String,val lat:String)