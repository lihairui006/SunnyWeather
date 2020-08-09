package com.example.sunnyweather.ui.weather

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sunnyweather.MainActivity
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread


class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy{ViewModelProviders.of(this).get(WeatherViewModel::class.java)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //使背景图和状态栏融合到一起
        val decorView=window.decorView
        decorView.systemUiVisibility=
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor=Color.TRANSPARENT

        setContentView(R.layout.activity_weather)
        //首先取出经纬度坐标的地区名称并赋值到WeatherViewModel的相应变量中
        if(viewModel.locationLng.isEmpty()){
            viewModel.locationLng=intent.getStringExtra("location_lng") ?: ""
        }
        if(viewModel.locationLat.isEmpty()){
            viewModel.locationLat=intent.getStringExtra("location_lat") ?: ""
        }
        if(viewModel.placeName.isEmpty()){
            viewModel.placeName=intent.getStringExtra("place_name") ?: ""
        }

        //对weatherLiveData对象进行观察，获取到服务器返回的天气数据时，就调用showWeatherInfo()方法进行解析与展示
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather=result.getOrNull()
            if(weather!=null){
                showWeatherInfo(weather)
            }else{
                Toast.makeText(this,"无法成功获取天气信息",Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            thread {
                Thread.sleep(2000)
                runOnUiThread {
                    swipeRefresh.isRefreshing=false
                }
            }
        })

        //调用WeatherViewModel的refreshWeather()方法来执行一次刷新天气的请求
        //viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)

        //设置下拉刷新进度条颜色
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        //设置一个监听器，当触发下拉刷新操作时，就在监听器的回调中调用refreshWeather()方法来刷新天气
        swipeRefresh.setOnRefreshListener{
            refreshWeather()
        }


        //切换城市的点击事件
        navBtn.setOnClickListener {
            //打开滑动菜单
            drawerLayout.openDrawer(GravityCompat.START)
        }

        //监听DrawerLayout的状态，在滑动菜单被隐藏的时候，同时也要隐藏输入法
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager=getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })

    }

    fun refreshWeather(){

        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)//刷新天气
        swipeRefresh.isRefreshing =true //让下拉刷新进度条显示
    }

    /*
    从Weather对象中获取数据，然后显示在相应控件上
     */
    private fun showWeatherInfo(weather:Weather){
        placeName.text=viewModel.placeName
        val realtime=weather.realtime
        val daily=weather.daily

        val currentTempText="${realtime.temperature.toInt()}℃"
        currentTemp.text=currentTempText
        currentSky.text= getSky(realtime.skycon).info
        val currentPM25Text="空气指数${realtime.airQuery.aqi.chn.toInt()}"
        currentAQI.text=currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        forecastLayout.removeAllViews()
        val days=daily.skycon.size
        for(i in 0 until days ){
            val skycon =daily.skycon[i]
            val temperature=daily.temperature[i]
            val view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false)
            val dateInfo=view.findViewById(R.id.dateInfo) as TextView
            val skyIcon=view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo=view.findViewById(R.id.skyInfo)as TextView
            val temperatureInfo=view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat=SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
            dateInfo.text=simpleDateFormat.format(skycon.date)
            val sky= getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text=sky.info
            val tempText="${temperature.min.toInt()}~${temperature.max.toInt()} ℃"
            temperatureInfo.text=tempText
            forecastLayout.addView(view)
        }

        val lifeIndex=daily.lifeIndex
        coldRiskText.text=lifeIndex.coldRisk[0].desc
        dressingText.text=lifeIndex.dressing[0].desc
        ultravioletText.text=lifeIndex.ultraviolet[0].desc
        carWashingText.text=lifeIndex.carWashing[0].desc
        weatherLayout.visibility= View.VISIBLE
    }
}