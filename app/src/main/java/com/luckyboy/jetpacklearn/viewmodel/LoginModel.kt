package com.luckyboy.jetpacklearn.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.luckyboy.jetpacklearn.common.BaseApplication
import com.luckyboy.jetpacklearn.db.RepositoryProvider
import com.luckyboy.jetpacklearn.db.data.Shoe
import com.luckyboy.jetpacklearn.db.data.User
import com.luckyboy.jetpacklearn.db.repository.UserRepository

// LoginModel主要负责登录逻辑处理和 登录 密码 两个输入框内容改变的时候 数据更新的处理

//class LoginModel constructor(name: String, pwd: String, context: Context) {
class LoginModel constructor(private val repository: UserRepository) : ViewModel() {

    private val TAG: String by lazy {
        this::class.java.simpleName
    }

    val n = MutableLiveData<String>("")
    val p = MutableLiveData<String>("")

    /**
     * 用户名改变回调的函数
     */
    fun onNameChanged(s: CharSequence) {
        n.value = s.toString()
    }

    /**
     * 密码改变的回调函数
     */
    fun onPwdChange(s: CharSequence, start: Int, before: Int, count: Int) {
        p.value = s.toString()
    }

    // 登录查询是否存在这个用户
    fun login(): LiveData<User?>? {
        val pwd = p.value!!
        val account = n.value!!
        return repository.login(account, pwd)
    }

    // 第一次启动的是否使用
    fun onFirstLaunch(): String {
        val context = BaseApplication.context
        context.assets.open("shoes.json").use {
            JsonReader(it.reader()).use {
                val shoeType = object : TypeToken<List<Shoe>>() {}.type
                val shoeList: List<Shoe> = Gson().fromJson(it, shoeType)

                val shoeDao = RepositoryProvider.providerShoeRepository(context)
                shoeDao.insertShoes(shoeList)

                // todo 不清楚干什么用的？？
                for (i in 0..2) {
                    for (shoe in shoeList) {
                        shoe.id += shoeList.size
                    }
                    shoeDao.insertShoes(shoeList)
                }
            }
        }
        Log.e(TAG, "初始化数据成功")
        return "初始化数据成功!"
    }

}
















