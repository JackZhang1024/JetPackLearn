package com.luckyboy.jetpacklearn.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.luckyboy.jetpacklearn.db.repository.ShoeRepository
import com.luckyboy.jetpacklearn.viewmodel.ShoeModel

class ShoeModelFactory (private val repository: ShoeRepository):ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ShoeModel(repository) as T
    }
}

