package com.luckyboy.jetpacklearn.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.luckyboy.jetpacklearn.db.repository.ShoeRepository
import com.luckyboy.jetpacklearn.viewmodel.FavouriteModel

class FavouriteModelFactory(private val repository: ShoeRepository, private val userId: Long) :

    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FavouriteModel(repository, userId) as T
    }

}