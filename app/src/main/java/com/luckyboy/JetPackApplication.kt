package com.luckyboy

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.luckyboy.libnetwork.ApiService
import com.luckyboy.libnetwork.JsonConvert

open class JetPackApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        ApiService.init("http://122.51.191.103:8080/serverdemo", JsonConvert());
//        ApiService.init("http://192.168.31.244:8080/serverdemo", JsonConvert());
//        ApiService.init("http://123.56.232.18:8080/serverdemo", JsonConvert());
        Stetho.initializeWithDefaults(this);
    }

    companion object {
        lateinit var context: Context
    }


}
