package com.example.sunnyweather.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/*
定义一个统一的网络数据源接口，对所有网络请求的API0进行封装
 */

/*
当外部调用SunnyWeatherNetwork的searchPlace()函数时，Retrofit就会立即发起网络请求，同时当前的协程也会被阻塞住。
直到服务器相应我们的请求之后，await()函数会将解析出来的数据模型对象取出并返回，同时恢复当前协程的执行，searchPlaces()
函数在得到await()函数的返回值后会将该数据再返回到上一层
 */
object SunnyWeatherNetwork {

    //对WeatherService接口进行封装
    private val weatherService=ServiceCreator.create(WeatherService::class.java)

    suspend fun getDailyWeather(lng:String,lat:String)=
        weatherService.getDailyWeather(lng,lat).await()

    suspend fun getRealtimeWeather(lng:String,lat: String)=
        weatherService.getRealtimeWeather(lng,lat).await()
    

    //使用ServiceCreator创建了一个PlaceService接口的动态代理对象
    private val placeService=ServiceCreator.create(PlaceService::class.java)

    //定义一个searchPlace()函数，调用在PlaceService中定义的searchPlace()方法，以发起搜索城市数据请求
    suspend fun searchPlaces(query:String)=placeService.searchPlaces(query).await()

    private suspend fun <T> Call<T>.await():T{
        return suspendCoroutine {continuation ->
            enqueue(object : Callback<T>{
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body=response.body()
                    if(body!=null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}