package com.luckyboy.jetpacklearn

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.luckyboy.jetpacklearn.common.BaseConstant

class LoginFragment:Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    lateinit var etUserName:EditText;
    lateinit var etUserPassword:EditText;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etUserName = view.findViewById(R.id.et_user_name)
        etUserPassword = view.findViewById(R.id.et_user_pwd)
        val name = arguments?.getString(BaseConstant.ARG_NAME);
        if (!TextUtils.isEmpty(name)){
            etUserName.setText(name)
        }
    }

    private fun onSubScribeUi(view:View){

    }




}