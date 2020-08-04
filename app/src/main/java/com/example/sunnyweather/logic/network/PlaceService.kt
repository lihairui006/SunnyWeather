package com.example.sunnyweather.logic.network

//定义一个用于访问彩云天气城市搜索的Retrofit接口
import com.example.sunnyweather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {

    //申明了@GET注解当调用searchPlace()方法的时候，Retrofit就会自动发送一条GET请求，去访问@GET中配置的地址
    @GET("v2/place?token={SunnyWeatherApplication.TOKEN}&lang=zh_CN")

    /*1.搜索城市数据的API中只有query参数是动态指定的，使用@Query注解的方式来进行实现，另外两个参数是不会变的

    2.searchPlace()方法的返回值被声明成了Call<PlaceResponse>,
    这样Retrofit就会将服务器返回的JSON数据自动解析成PlaceResponse对象了
    */
    fun searchPlaces(@Query("query")query:String):Call<PlaceResponse>

}