package com.luckyboy.jetpacklearn.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.luckyboy.jetpacklearn.R
import com.luckyboy.JetPackApplication.Companion.context
import com.luckyboy.jetpacklearn.databinding.ActivityHomeBinding
import com.luckyboy.jetpacklearn.ui.adapter.ShoeAdapter
import com.luckyboy.jetpacklearn.viewmodel.CustomViewModelProvider
import com.luckyboy.jetpacklearn.viewmodel.ShoeModel

class HomeActivity : AppCompatActivity() {

    private val TAG:String by lazy {
        HomeActivity::class.java.simpleName
    }

    private val viewModel:ShoeModel by viewModels{
        CustomViewModelProvider.providerShoeModel(context)
    }

    lateinit var homeBinding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_home)
        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val adapter = ShoeAdapter(context)
        homeBinding.recycler.adapter = adapter
        onSubScribeUi(adapter, homeBinding)
    }


    // 鞋子数据更新的通知
    private fun onSubScribeUi(adapter: ShoeAdapter, binding: ActivityHomeBinding){
        viewModel.shoes.observe(this, Observer {
            if (it!=null){
                Log.e(TAG, "返回的数据条数  ${it.size}")
                adapter.submitList(it)
            }
        })
    }




































}