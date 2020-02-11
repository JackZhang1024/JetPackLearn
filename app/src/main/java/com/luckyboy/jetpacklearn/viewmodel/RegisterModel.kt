package com.luckyboy.jetpacklearn.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luckyboy.jetpacklearn.db.repository.UserRepository
import kotlinx.coroutines.launch


class RegisterModel constructor(private val repository: UserRepository):ViewModel(){

    private val TAG:String by lazy {
        this::class.java.simpleName
    }

    // MutableLiveData 是可变数据
    val n = MutableLiveData<String>("")
    val p = MutableLiveData<String>("")
    val mail = MutableLiveData<String>("")

    // 用户名改变回调的函数
    fun onNameChanged(s:CharSequence){
        n.value = s.toString()
    }


    // 密码改变的回调函数
    fun onPwdChanged(s:CharSequence){
        p.value = s.toString()
    }

    // 邮箱改变的时候
    fun onEmailChanged(s:CharSequence){
        mail.value = s.toString()
    }

    fun register(){
        viewModelScope.launch {
            Log.e(TAG, "mail:${mail.value}  name:${n.value} pwd:${p.value}")
            repository.register(mail.value!!, n.value!!, p.value!!)
        }
    }
}

