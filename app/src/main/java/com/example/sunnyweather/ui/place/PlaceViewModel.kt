package com.example.sunnyweather.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Place

class PlaceViewModel: ViewModel() {

    /*
    定义了searchPlaces()方法，但没有直接调用仓库层的searchPlaces()方法，而是将传入的搜索参数赋值给了
    searchLiveData对象，并调用Transformations的switchMap()方法来观察这个对象，否则仓库返回的LiveDta
    对象将无法进行观察。

    每当searchPlace()函数被调用时，switchMap()方法所对应的转换函数就会执行，然后在转换函数中，我们只需要调用仓库层
    中定义的searchPlaces()方法就可以发起网络请求，同时将仓库层返回的LiveData对象转换为一个可供Activity观察的LiveData
    对象
     */
    private val searchLiveData= MutableLiveData<String>()

    //定义了placeList集合，用于对界面上显示的城市数据进行缓存，可以保证他们在手机屏幕发生旋转时不会丢失
    val placeList=ArrayList<Place>()

    val placeLiveData= Transformations.switchMap(searchLiveData){query->
        Repository.searchPlaces(query)
    }

    fun searchPlaces(query:String){
        searchLiveData.value=query
    }

    fun savedPlace(place:Place)=Repository.savePlace(place)

    fun getSavePlace()=Repository.getSavePlace()

    fun isPlaceSaved()=Repository.isPlaceDaved()
}