package com.luckyboy.jetpacklearn.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.luckyboy.jetpacklearn.common.BaseConstant
import com.luckyboy.jetpacklearn.ui.HomeActivity

// LoginModel主要负责登录逻辑处理和 登录 密码 两个输入框内容改变的时候 数据更新的处理

//class LoginModel constructor(name: String, pwd: String, context: Context) {
class LoginModel constructor(name: String, pwd: String, context: Context):ViewModel(){

    val n = ObservableField<String>(name)
    val p = ObservableField<String>(pwd)

    var context: Context = context


    /**
     * 用户名改变回调的函数
     */
    fun onNameChanged(s:CharSequence){
        n.set(s.toString())
    }

    /**
     * 密码改变的回调函数
     */
    fun onPwdChange(s:CharSequence, start:Int, before:Int, count:Int){
        p.set(s.toString())
    }

    fun login() {
        if (n.get().equals(BaseConstant.USER_NAME)
            && p.get().equals(BaseConstant.USER_PWD)
        ) {
            Toast.makeText(context, "账号密码正确", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "账号或密码不正确", Toast.LENGTH_SHORT).show();
        }
    }


}
















