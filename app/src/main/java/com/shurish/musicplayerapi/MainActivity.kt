package com.shurish.musicplayerapi

import MyAdapter
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.shurish.musicplayerapi.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var adapter : MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.progressbar.visibility = View.VISIBLE
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InterfaceApi::class.java)

        val retrofitData = retrofitBuilder.getData("eminem")

        retrofitData.enqueue(object : Callback<MyData?> {
            override fun onResponse(p0: Call<MyData?>, response: Response<MyData?>) {
                val data =response.body()?.data
//                val textview = findViewById<TextView>(R.id.testid)
//                textview.text = data.toString()
                Log.d("Onsuccess Tag" ,"Onsuccess " + response.body())



                adapter = MyAdapter(this@MainActivity)
                binding.myRecylerView.adapter = adapter
                adapter.differ.submitList(data)
                binding.progressbar.visibility = View.GONE


            }

            override fun onFailure(p0: Call<MyData?>, p1: Throwable) {
                Log.d("OnFailed Tag" , "OnFaliure Meesage" + p1.message)
            }
        })

    }
}