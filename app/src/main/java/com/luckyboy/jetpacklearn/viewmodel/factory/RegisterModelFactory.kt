package com.luckyboy.jetpacklearn.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.luckyboy.jetpacklearn.db.repository.UserRepository
import com.luckyboy.jetpacklearn.viewmodel.RegisterModel

class RegisterModelFactory (private val repository: UserRepository): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegisterModel(repository) as T
    }

}