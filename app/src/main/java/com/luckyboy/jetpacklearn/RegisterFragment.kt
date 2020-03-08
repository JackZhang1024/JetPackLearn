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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.luckyboy.jetpacklearn.databinding.FragmentRegisterBinding
import com.luckyboy.jetpacklearn.viewmodel.CustomViewModelProvider
import com.luckyboy.jetpacklearn.viewmodel.RegisterModel
import kotlinx.android.synthetic.*

class RegisterFragment : Fragment() {

    private var isEnable: Boolean = false
    private val registerModel: RegisterModel by viewModels {
        CustomViewModelProvider.providerRegisterModel(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentRegisterBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        initData(binding)
        onSubscribeUi(binding)
        return binding.root
    }

    private fun initData(binding: FragmentRegisterBinding) {
        val safeVarargs: RegisterFragmentArgs by navArgs()
        val email = safeVarargs.email
        binding.model?.mail?.value = email

        binding.model = registerModel
        binding.isEnable = isEnable
        binding.activity = activity
    }

    private fun onSubscribeUi(binding: FragmentRegisterBinding) {
        binding.btnRegister.setOnClickListener {
            registerModel.register()
//            var bundle
            // 进入到主页HomeActivity

        }
        binding.btnGoBack.setOnClickListener {
            findNavController().popBackStack()
        }
        registerModel.p.observe(viewLifecycleOwner, Observer {
            binding.isEnable = it.isNotEmpty()
                    && registerModel.n.value!!.isNotEmpty()
                    && registerModel.mail.value!!.isNotEmpty()
        })
        registerModel.n.observe(viewLifecycleOwner, Observer {
            binding.isEnable = it.isNotEmpty()
                    && registerModel.p.value!!.isNotEmpty()
                    && registerModel.mail.value!!.isNotEmpty()
        })
        registerModel.mail.observe(viewLifecycleOwner, Observer {
            binding.isEnable = it.isNotEmpty()
                    && registerModel.n.value!!.isNotEmpty()
                    && registerModel.p.value!!.isNotEmpty()
        })
    }

}













































