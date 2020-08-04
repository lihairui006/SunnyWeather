package com.example.sunnyweather.logic

import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

//作为仓库层的统一封装类
object Repository {

    //将liveData的线程参数类型指定为Dispatchers.IO ，可以使所有代码都运行在子线程
    fun searchPlaces(query:String)= liveData(Dispatchers.IO){
        val result=try{

            //调用SunnyWeatherNetwork中的searchPlace()函数来来搜索城市数据
            val placeResponse=SunnyWeatherNetwork.searchPlaces(query)
            //如果服务器响应的是ok，就使用kotlin内置的Result.success()方法来包装获取的城市数据列表
            if(placeResponse.status=="ok"){
                val places=placeResponse.places
                Result.success(places)
            }else{   //否则使用Result.failure方法来包装一个异常信息
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        }catch (e:Exception){
            Result.failure<List<Place>>(e)
        }
        //使用emit()老通知数据变化
        emit(result)//编译通过了，但是显示是有红色波浪线
    }
}