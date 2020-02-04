package com.luckyboy.jetpacklearn

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.luckyboy.jetpacklearn.common.BaseConstant
import com.luckyboy.jetpacklearn.databinding.FragmentLoginBinding
import com.luckyboy.jetpacklearn.viewmodel.LoginModel
import com.luckyboy.jetpacklearn.viewmodel.UserViewModel

class LoginFragment:Fragment(){

    lateinit var loginModel: LoginModel

    private lateinit var userModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding:FragmentLoginBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        );
        onSubScribeUi(binding)
        onObserveUserModel(binding)
        return binding.root;
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun onSubScribeUi(binding: FragmentLoginBinding){
        loginModel = LoginModel("111", "11", context!!);
        binding.model = loginModel
        binding.activity = activity

        binding.btnLogin.setOnClickListener{
            Toast.makeText(context, "登录还没完成", Toast.LENGTH_SHORT).show();
        }
        binding.btnGoBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        val name = arguments?.getString(BaseConstant.ARG_NAME);
        if (!TextUtils.isEmpty(name)){
            loginModel.n.set(name!!)
        }

    }

    private fun onObserveUserModel(binding: FragmentLoginBinding){
        // 获取用户 UserViewModel 这个
        userModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        // 创建更新UI的观察者 Observer
        val userNameObserver = Observer<String>{
            newName->
            // 更新UI 更新TextView上的文字
            binding.tvUserName.setText(newName)
            // 这块的例子表明只要我数据更新了 在这个页面上我就可以随意处置怎么使用
            println(userModel.currentName.value)
        }
        // 观察 LiveData
        userModel.currentName.observe(this, userNameObserver)
        binding.btnChangeName.setOnClickListener {
            userModel.currentName.setValue("JackLala")
        }
    }




}