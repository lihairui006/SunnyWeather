package com.example.sunnyweather.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.Location
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.ui.place.PlaceViewModel
import com.example.sunnyweather.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.activity_weather.*

//import com.example

//为RecyclerView准备的适配器
class PlaceAdapter(private val fragment: PlaceFragment,private val placeList:List<Place>)
    : RecyclerView.Adapter<PlaceAdapter.ViewHolder>(){

    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val placeName: TextView =view.findViewById(R.id.placeName)
        val placeAddress:TextView=view.findViewById(R.id.placeAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.place_item,
            parent,false)

        val holder=ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position=holder.adapterPosition
            val place=placeList[position]
            val activity=fragment.activity

            /*
            如果在WeatherActivity中，就关闭滑动菜单，给WeatherViewModel赋值新的经纬度坐标和地区名称，
            然后刷新天气信息，而如果在MainActivity中，保持之前的逻辑不变即可
             */
            if(activity is WeatherActivity){
                activity.drawerLayout.closeDrawers()
                activity.viewModel.locationLng=place.location.lng
                activity.viewModel.locationLat=place.location.lat
                activity.viewModel.placeName=place.name
                activity.refreshWeather()
            }else{
                val intent = Intent(parent.context, WeatherActivity::class.java).apply {
                    putExtra("location_lng",place.location.lng)
                    putExtra("location_lat",place.location.lat)
                    putExtra("place_name",place.name)
                }
                fragment.startActivity(intent)
                activity?.finish()
            }

            //点击任何子布局时，在跳转到WeatherActivity之前，先调用PlaceViewModel的savePlace()方法来存储选中城市
            fragment.viewModel.savedPlace(place)
            //fragment.activity?.finish()
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place=placeList[position]
        holder.placeName.text=place.name
        holder.placeAddress.text=place.address
    }

    override fun getItemCount()=placeList.size
}