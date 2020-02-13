package com.luckyboy.jetpacklearn.ui.fragment.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import cn.pedant.SweetAlert.SweetAlertDialog
import com.luckyboy.jetpacklearn.R
import com.luckyboy.jetpacklearn.common.BaseConstant
import com.luckyboy.jetpacklearn.databinding.ActivityUserAvatarBinding
import com.luckyboy.jetpacklearn.databinding.FragmentMeBinding
import com.luckyboy.jetpacklearn.viewmodel.CustomViewModelProvider
import com.luckyboy.jetpacklearn.viewmodel.MeModel

class MeFragment : Fragment() {

    private val TAG by lazy {
        this::class.java.simpleName
    }

    private val model:MeModel by viewModels{
        CustomViewModelProvider.providerMeModel(requireContext())
    }


    private lateinit var meBinding: FragmentMeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "onCreateView")
        meBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_me, container, false)
        initListener(meBinding)
        onSubscribeUi(meBinding)
        return meBinding.root
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG, "onDestroyView")
    }

    // 选择图片的标识
    private val REQUEST_CODE_IMAGE = 100
    // 加载框
    private val sweetAlertDialog: SweetAlertDialog by lazy {
        SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("头像")
            .setContentText("更新中...")
    }

    // 初始化监听器
    private fun initListener(binding: FragmentMeBinding) {
        binding.ivAvatar.setOnClickListener {
            // 选择处理的图片
            val chooseIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(chooseIntent, REQUEST_CODE_IMAGE)
        }
    }

    // Binding 绑定
    private fun onSubscribeUi(binding: FragmentMeBinding) {
        model.user.observe(this, Observer {
            binding.user = it
        })
        // 任务状态的观测
        model.outPutWorkInfos.observe(this, Observer {
            if (it.isNullOrEmpty()) {
                return@Observer
            }
            val state = it[0]
            if (state.state.isFinished) {
                // 更新头像
                Log.e(TAG, "获取到头像数据后 准备更新头像操作")
                val outputImageUri = state.outputData.getString(BaseConstant.KEY_IMAGE_URI)
                if (!outputImageUri.isNullOrEmpty()) {
                    model.setOutputUri(outputImageUri)
                }
                sweetAlertDialog.dismiss()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE -> data?.let {
                    handleImageRequestResult(data)
                }
            }
        } else {
            Log.e(TAG, String.format("Unexpected Result code %s", resultCode))
        }
    }

    // 图片选择完成的处理
    private fun handleImageRequestResult(intent: Intent) {
        val imageUri: Uri? = intent.clipData?.let {
            it.getItemAt(0).uri
        } ?: intent.data
        Log.e(TAG, "handleImageRequestResult $imageUri")
        if (imageUri == null) {
            Log.e(TAG, "Invalid input image uri")
            return
        }
        sweetAlertDialog.show()
        // 图片模糊处理
        model.setImageUri(imageUri.toString())
        model.applyBlur(3)
    }



}