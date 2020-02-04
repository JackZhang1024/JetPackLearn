package com.luckyboy.jetpacklearn

import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

// 关于位置信息的观察者
class LocationObserver:LifecycleObserver{


//    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//    fun onResume(){
//        println("onResume")
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//    fun onPause(){
//        println("onPause")
//    }
//
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun onStop(){
//        println("onStop")
//    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onLocationConnect(){
        println("onLocationConnect")
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onLocationDisconnect(){
        println("onLocationDisconnect")
    }




}