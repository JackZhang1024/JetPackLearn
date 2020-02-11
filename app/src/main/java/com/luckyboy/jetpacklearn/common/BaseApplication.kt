package com.luckyboy.jetpacklearn.common

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho

open class BaseApplication :Application(){

    override fun onCreate() {
        super.onCreate()
        context = this
        Stetho.initialize(
            Stetho.newInitializerBuilder(this)
                .enableDumpapp(
                    Stetho.defaultDumperPluginsProvider(this)
                )
                .enableWebKitInspector(
                    Stetho.defaultInspectorModulesProvider(this)
                )
                .build()
        )
    }


    companion object{
        lateinit var context: Context
    }
}