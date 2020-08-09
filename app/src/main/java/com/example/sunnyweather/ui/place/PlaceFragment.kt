package com.example.sunnyweather.ui.place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sunnyweather.MainActivity
import com.example.sunnyweather.R
import com.example.sunnyweather.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.fragment_place.*

class PlaceFragment: Fragment() {

    //使用懒加载技术获取PlaceViewModel的实例
    val viewModel by lazy { ViewModelProviders.of(this).get(PlaceViewModel::class.java) }

    private lateinit var  adapter : PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //加载编写的fragment_place的布局
        return inflater.inflate(R.layout.fragment_place,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //对存储的状态经行判断和读取
        //如果当前已有存储的数据，就获取已存储的数据并解析成Place对象，然后使用它的经纬度坐标和城市名直接跳转
        //传递给WeatherActivity
        //再加一层判断，只有当PlaceFragment被嵌入MainActivity中，并且之前已经存在选中的城市，此时才会直接跳转到WeatherActivity中，解决无线循环问题
        if(activity is MainActivity && viewModel.isPlaceSaved()){
            val place=viewModel.getSavePlace()
            val intent= Intent(context,WeatherActivity::class.java).apply {
                putExtra("location_lng",place.location.lng)
                putExtra("location_lat",place.location.lat)
                putExtra("place_name",place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        //给RecyclerView设置了LayoutManager
        val layoutManager= LinearLayoutManager(activity)
        recyclerView.layoutManager=layoutManager

        //给RecyclerView设置了适配器并使用PlaceViewModel中的placeList集合作为数据源
        adapter=PlaceAdapter(this,viewModel.placeList)
        recyclerView.adapter=adapter

        /*
        调用EditText的addTextChangeListener()方法来搜索框内容变化情况，
        每当搜索框中的内容发生了变化，我们就获取新的内容，然后传给PlaceViewModel的searchPlace()方法
        这样就可以搜索城市数据的网络请求了。

        而当搜索框为空的时候，我们就将RecyclerView隐藏起来，同时将那张仅用于美观的背景图片显示出来
         */
        searchPlaceEdit.addTextChangedListener { editable->
            val content=editable.toString()
            if(content.isNotEmpty()){
                viewModel.searchPlaces(content)
            }else{
                recyclerView.visibility=View.GONE
                bgImageView.visibility=View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        /*
        对PlaceViewModel中的placeLiveData对象经行观察，当有任何数据变化时，就会回调到传入的Observer接口实现中
        然后我们会对回调函数进行判断：如果数据不为空，那么就将这些数据加到PlaceViewModel的placeList集合中，并通知
        PlaceAdapter刷新界面；如果数据为空，则说明发生了异常，此时弹出一个Toast的提示
         */
        viewModel.placeLiveData.observe(this, Observer { result->
            val places=result.getOrNull()
            if(places!=null){
                recyclerView.visibility=View.VISIBLE
                bgImageView.visibility=View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            }else{
                Toast.makeText(activity,"未能查到任何地点",Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }
}