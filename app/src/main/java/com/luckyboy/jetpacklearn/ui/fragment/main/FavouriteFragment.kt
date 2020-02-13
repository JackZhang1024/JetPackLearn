package com.luckyboy.jetpacklearn.ui.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.luckyboy.jetpacklearn.R
import com.luckyboy.jetpacklearn.databinding.FragmentFavouriteBinding

class FavouriteFragment : Fragment() {

    private val TAG by lazy {
        this::class.java.simpleName
    }

    private lateinit var favouriteBinding: FragmentFavouriteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "onCreateView")
        favouriteBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_favourite, container, false)
        return favouriteBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated")
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