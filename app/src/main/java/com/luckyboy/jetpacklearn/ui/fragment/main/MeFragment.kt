package com.luckyboy.jetpacklearn.ui.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.luckyboy.jetpacklearn.R
import com.luckyboy.jetpacklearn.databinding.FragmentMeBinding

class MeFragment : Fragment() {

    private val TAG by lazy {
        this::class.java.simpleName
    }

    private lateinit var meBinding: FragmentMeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "onCreateView")
        meBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_me, container, false)
        return meBinding.root
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated")
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