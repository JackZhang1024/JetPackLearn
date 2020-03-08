package com.luckyboy.jetpacklearn

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.luckyboy.jetpacklearn.common.BaseConstant
import com.luckyboy.jetpacklearn.databinding.FragmentLoginBinding
import com.luckyboy.jetpacklearn.ui.HomeActivity
import com.luckyboy.jetpacklearn.ui.HostActivity
import com.luckyboy.jetpacklearn.ui.UserAvatarActivity
import com.luckyboy.jetpacklearn.utils.AppPrefsUtils
import com.luckyboy.jetpacklearn.viewmodel.CustomViewModelProvider
import com.luckyboy.jetpacklearn.viewmodel.LoginModel
import com.luckyboy.jetpacklearn.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private val loginModel: LoginModel by viewModels {
        CustomViewModelProvider.providerLoginModel(requireContext())
    }

    private lateinit var userModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentLoginBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        );
        onSubScribeUi(binding)
//        onObserveUserModel(binding)
        // 判断当前是否是第一次登陆
        var isFirstLaunch  = AppPrefsUtils.getBoolean(BaseConstant.IS_FIRST_LAUNCH)
        if (isFirstLaunch){
            onFirstLaunch()
        }
        return binding.root
    }


    private fun onSubScribeUi(binding: FragmentLoginBinding) {
        binding.model = loginModel
        binding.activity = activity

        binding.btnLogin.setOnClickListener {
            loginModel.login()?.observe(this, Observer { user ->
                user?.let {
                    // let 高阶函数
                    AppPrefsUtils.putLong(BaseConstant.SP_USER_ID, it.id)
                    AppPrefsUtils.putString(BaseConstant.SP_USER_NAME, it.account)
                    val intent = Intent(context, HostActivity::class.java)
                    context!!.startActivity(intent)
                    Toast.makeText(context,"登录成功",  Toast.LENGTH_SHORT).show()
                }
            })
        }
        binding.btnGoBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
        loginModel.p.observe(viewLifecycleOwner, Observer {
            // 保证账号和密码不为空的时候才可以点击按钮 进行登录
            binding.isEnable = it.isNotEmpty() && loginModel.n.value!!.isNotEmpty()
        })
        val name = arguments?.getString(BaseConstant.ARG_NAME);
        if (!TextUtils.isEmpty(name)) {
            loginModel.n.value = name!!
        }
        binding.btnChangeAvatar.setOnClickListener {
            val intent = Intent(context, UserAvatarActivity::class.java)
//            val intent = Intent(context, HostActivity::class.java)
            context!!.startActivity(intent)
        }
    }

    private fun onObserveUserModel(binding: FragmentLoginBinding) {
        // 获取用户 UserViewModel 这个
        userModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        // 创建更新UI的观察者 Observer
        val userNameObserver = Observer<String> { newName ->
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

    // 第一次启动的时候调用
    private fun onFirstLaunch(){
        // GlobalScope
        GlobalScope.launch(Dispatchers.Main) {
            val str = withContext(Dispatchers.IO){
                loginModel.onFirstLaunch()
            }
            Toast.makeText(context!!, str, Toast.LENGTH_SHORT).show()
            AppPrefsUtils.putBoolean(BaseConstant.IS_FIRST_LAUNCH, false)
        }

    }


}