package com.luckyboy.jetpacklearn.ui.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.luckyboy.jetpacklearn.R
import com.luckyboy.jetpacklearn.databinding.FragmentShoesBinding
import com.luckyboy.jetpacklearn.ui.adapter.ShoeAdapter
import com.luckyboy.jetpacklearn.viewmodel.CustomViewModelProvider
import com.luckyboy.jetpacklearn.viewmodel.ShoeModel

// 鞋子页面
class ShoeFragment : Fragment(){

    private val TAG by lazy {
        this::class.java.simpleName
    }

    lateinit var shoesBinding: FragmentShoesBinding

    private val viewModel:ShoeModel by viewModels {
        CustomViewModelProvider.providerShoeModel(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "onCreateView")
        shoesBinding =  DataBindingUtil.inflate(inflater, R.layout.fragment_shoes, container, false)
        val adapter = ShoeAdapter(context!!)
        shoesBinding.recycler.adapter = adapter
        onSubScribeUi(adapter, shoesBinding)
        return shoesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated")
    }

    // 鞋子数据更新的通知
    private fun onSubScribeUi(adapter: ShoeAdapter, binding: FragmentShoesBinding){
        viewModel.shoes.observe(this, Observer {
            if (it!= null){
                Log.e(TAG, "返回的数据条数 ${it.size}")
                adapter.submitList(it)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG, "onDestroyView")
    }



}