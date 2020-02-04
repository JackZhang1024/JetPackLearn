package com.luckyboy.jetpacklearn.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel:ViewModel(){

    // Create a LiveData with a String
    val currentName:MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    // Rest of the ViewModel


    override fun toString(): String {
        return "${currentName}"
    }


}