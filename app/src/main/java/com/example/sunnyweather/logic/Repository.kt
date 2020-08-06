package com.example.sunnyweather.logic

import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.dao.PlaceDao
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

//作为仓库层的统一封装类
object Repository {

    //提供refreshWeather()用来刷新天气信息
    fun refreshWeather(lng: String, lat: String)= fire(Dispatchers.IO){
            coroutineScope {

                /*
                分别在两个async函数中发起网络请求，然后再分别调用他们的await()方法就可以保证只有在两个网络请求
                都成功响应之后，才会进一步执行程序。

                由于async函数必须在协程作用域内才能调用，所以使用coroutineScope函数创建了一个协程作用域
                 */
                val deferredRealtime=async {
                    SunnyWeatherNetwork.getRealtimeWeather(lng,lat)
                }
                val deferredDaily=async {
                    SunnyWeatherNetwork.getDailyWeather(lng,lat)
                }
                val realtimeResponse=deferredRealtime.await()
                val dailyResponse=deferredDaily.await()

                /*
                在同时获取到RealtimeResponse和dailyResponse之后，如果他们的响应状态都是ok，那么就将
                Realtime和daily对象取出并封装到一个Weather对象中，然后使用Result.success()的方法来
                包装Weather对象，否则就使用Result.failure()来包装一个异常信息，最后调用emit()方法将包
                装的结果发射出去
                 */
                if(realtimeResponse.status=="ok"&&dailyResponse.status=="ok"){
                    val weather = Weather(realtimeResponse.result.realtime,
                        dailyResponse.result.daily)
                    Result.success(weather)
                }else{
                    Result.failure(
                        RuntimeException(
                            "realtime response status is ${realtimeResponse.status}"+
                            "daily response status is ${dailyResponse.status}"
                        )
                    )
                }
            }
    }




    //将liveData的线程参数类型指定为Dispatchers.IO ，可以使所有代码都运行在子线程
    fun searchPlaces(query:String)= fire(Dispatchers.IO){

        //调用SunnyWeatherNetwork中的searchPlace()函数来来搜索城市数据
        val placeResponse=SunnyWeatherNetwork.searchPlaces(query)
        //如果服务器响应的是ok，就使用kotlin内置的Result.success()方法来包装获取的城市数据列表
        if(placeResponse.status=="ok"){
            val places=placeResponse.places
            Result.success(places)
        }else{   //否则使用Result.failure方法来包装一个异常信息
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    //在仓库中每为每个网络请求都进行try catch 处理，编写一个统一的入口函数进行封装
    //新增的fire()函数按照liveData()函数的参数接收标准定义的一个高阶函数
    //suspend关键字表示所有传入Lambda表达式中的代码也是拥有挂起函数上下文的
    private fun <T> fire(context:CoroutineContext,block:suspend()->Result<T>)=

        //先调用liveData函数，在liveData函数中统一进行了try catch处理
        liveData<Result<T>> {

            //在try中调用传入的Lambda表达式中的代码，最终获取Lambda表达式的执行结果并调用emit()方法发射出去
            val result =try{
                block()
            }catch (e:Exception){
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun savePlace(place:Place)=PlaceDao.savePlace(place)

    fun getSavePlace()=PlaceDao.getSavedPlace()

    fun isPlaceDaved()=PlaceDao.isPlaceSaved()
}