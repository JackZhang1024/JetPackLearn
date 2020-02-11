package com.luckyboy.jetpacklearn.viewmodel.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.luckyboy.jetpacklearn.db.repository.UserRepository
import com.luckyboy.jetpacklearn.viewmodel.LoginModel

class LoginModelFactory (private val repository: UserRepository,
                         private val context: Context
                         ):ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginModel(repository) as T
    }
}