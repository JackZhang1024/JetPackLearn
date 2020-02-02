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
import androidx.navigation.fragment.findNavController
import com.luckyboy.jetpacklearn.common.BaseConstant
import com.luckyboy.jetpacklearn.databinding.FragmentLoginBinding
import com.luckyboy.jetpacklearn.viewmodel.LoginModel

class LoginFragment:Fragment(){

   lateinit var loginModel: LoginModel

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
        loginModel = LoginModel("", "", context!!);
        binding.model = loginModel
        binding.activity = activity
        return binding.root;
    }
    //        return inflater.inflate(R.layout.fragment_login, container, false);


    lateinit var etUserName:EditText
    lateinit var etUserPassword:EditText
    lateinit var btnLgoin:Button
    lateinit var btnGoBack:Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etUserName = view.findViewById(R.id.et_user_name)
        etUserPassword = view.findViewById(R.id.et_user_pwd)
        btnLgoin = view.findViewById(R.id.btn_login)
        btnGoBack = view.findViewById(R.id.btn_go_back)
        val name = arguments?.getString(BaseConstant.ARG_NAME);
        if (!TextUtils.isEmpty(name)){
            etUserName.setText(name)
        }

        btnLgoin.setOnClickListener{
            Toast.makeText(context, "登录还没完成", Toast.LENGTH_SHORT).show();
        }
        btnGoBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun onSubScribeUi(view:View){

    }




}