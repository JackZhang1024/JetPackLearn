package com.luckyboy.jetpacklearn.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.luckyboy.jetpacklearn.common.BaseConstant
import com.luckyboy.jetpacklearn.common.BaseConstant.IMAGE_MANIPULATION_WORK_NAME
import com.luckyboy.jetpacklearn.common.BaseConstant.KEY_IMAGE_URI
import com.luckyboy.jetpacklearn.common.BaseConstant.TAG_OUTPUT
import com.luckyboy.jetpacklearn.db.repository.UserRepository
import com.luckyboy.jetpacklearn.utils.AppPrefsUtils
import com.luckyboy.jetpacklearn.worker.BlurWorker
import com.luckyboy.jetpacklearn.worker.CleanUpWorker
import com.luckyboy.jetpacklearn.worker.SaveImageToFileWorker
import kotlinx.coroutines.launch

// 个人信息
class MeModel(val userRepository: UserRepository) : ViewModel() {

    private val TAG by lazy {
        this::class.java.simpleName
    }

    var imageUri: Uri? = null
    var outPutUri: Uri? = null
    val outPutWorkInfos: LiveData<List<WorkInfo>>
    private val workManager = WorkManager.getInstance()
    val user = userRepository.findUserById(AppPrefsUtils.getLong(BaseConstant.SP_USER_ID))


    init {
        outPutWorkInfos = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
    }

    fun applyBlur(blurLevel: Int) {
        var continuation = workManager
            .beginUniqueWork(
                IMAGE_MANIPULATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanUpWorker::class.java)
            )
        for (i in 0 until blurLevel) {
            val builder =
                OneTimeWorkRequestBuilder<BlurWorker>()
            if (i == 0) {
                builder.setInputData(createInputDataForUri())
            }
            continuation = continuation.then(builder.build())
        }

        // 构建约束条件
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) // 非电池低电量运行
            .setRequiredNetworkType(NetworkType.CONNECTED) // 网络链接的情况
            .setRequiresStorageNotLow(true) // 存储空间足够大的时候
            .build()

        // 存储照片
        val save =
            OneTimeWorkRequestBuilder<SaveImageToFileWorker>()
                //.setConstraints(constraints)
                .addTag(TAG_OUTPUT)
                .build()
        continuation = continuation.then(save)

        continuation.enqueue()

    }


    private fun createInputDataForUri(): Data {
        val builder = Data.Builder()
        imageUri?.let {
            builder.putString(KEY_IMAGE_URI, imageUri.toString())
        }
        return builder.build()
    }

    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            null
        }
    }


    fun cancelWork() {
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }

    // setter函数
    fun setImageUri(uri: String?) {
        imageUri = uriOrNull(uri)
    }


    fun setOutputUri(uri: String?) {
        outPutUri = uriOrNull(uri)
        val value = user.value
        value?.headImage = uri!!
        if (value != null) {
            viewModelScope.launch {
                Log.e(TAG, "更新用户表用户头像字段数据  ${value.headImage}")
                userRepository.updateUser(value)
            }
        }
    }

}