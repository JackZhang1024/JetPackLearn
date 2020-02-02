package com.luckyboy.jetpacklearn

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.*

class RegisterFragment:Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    lateinit var etUserName:EditText
    lateinit var btnRegister:Button
    lateinit var btnGoBack:Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // SafeArgs的使用
        val safeVarargs:RegisterFragmentArgs by navArgs()
        val email = safeVarargs.email
        etUserName = view.findViewById(R.id.et_user_name)
        if (!TextUtils.isEmpty(email)){
            etUserName.setText(email)
        }
        btnRegister = view.findViewById(R.id.btn_register)
        btnGoBack = view.findViewById(R.id.btn_go_back)
        btnRegister.setOnClickListener {
            Toast.makeText(context, "注册功能没完成", Toast.LENGTH_SHORT).show()
        }
        btnGoBack.setOnClickListener {
           findNavController().popBackStack()
        }
    }

}