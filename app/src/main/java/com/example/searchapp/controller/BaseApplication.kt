package com.example.searchapp.controller

import android.app.Application
import com.example.searchapp.utils.Constants.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory


/**
 * Created by  vanikjain on 18/09/21
 */

class BaseApplication : Application() {

  private lateinit var mRetrofit: Retrofit

  companion object {
    lateinit var instance: BaseApplication
  }

  override fun onCreate() {
    super.onCreate()
    instance = this
  }

  fun getRetrofitObject() = if (!::mRetrofit.isInitialized) {
    mRetrofit =
      Retrofit.Builder().baseUrl(BASE_URL).addCallAdapterFactory(RxJava3CallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().build()).build()
    mRetrofit
  } else {
    mRetrofit
  }
}