package com.luckyboy.jetpacklearn

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs

class RegisterFragment:Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    lateinit var etUserName:EditText;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // SafeArgs的使用
        val safeVarargs:RegisterFragmentArgs by navArgs()
        val email = safeVarargs.email
        etUserName = view.findViewById(R.id.et_user_name)
        if (!TextUtils.isEmpty(email)){
            etUserName.setText(email)
        }
    }

}