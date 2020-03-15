package com.luckyboy.ppd.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.luckyboy.jetpacklearn.R;
import com.luckyboy.jetpacklearn.databinding.ActivityPpdRegisterBinding;
import com.luckyboy.libcommon.utils.StatusBar;
import com.luckyboy.libcommon.utils.ToastManager;
import com.luckyboy.ppd.login.model.UserViewModel;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    public static void launchActivity(Context context) {
        context.startActivity(new Intent(context, RegisterActivity.class));
    }

    ActivityPpdRegisterBinding binding;

    UserViewModel registerModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBar.fitSystemBar(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ppd_register);
        binding.toolbar.setNavigationOnClickListener((view) -> {
            finish();
        });
        registerModel = new ViewModelProvider(this).get(UserViewModel.class);
        registerModel.name.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String name) {
                //Log.e(TAG, "onChanged: name " + name);
            }
        });
        registerModel.password.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String password) {
                //Log.e(TAG, "onChanged: password " + password);
            }
        });
        binding.setModel(registerModel);
        registerResponse();
    }


    public void registerResponse() {
        registerModel.status.observe(this, value -> {
            if ("success".equals(value)) {
                finish();
            }
        });
    }

    public void doRegisterNext(View view) {
        // 跳转到下一页 然后选择拍摄或者选择照片
        //registerModel.register();
        // 判断用户名和密码是否为空
        String userPhone = registerModel.name.getValue();
        String userPwd = registerModel.password.getValue();
        if (TextUtils.isEmpty(userPhone) || TextUtils.isEmpty(userPwd)) {
            ToastManager.showToast("用户手机号或者密码不能为空");
            return;
        }
        ChooseAvatarActivity.startChooseAvatarActivityForResult(this, registerModel.name.getValue(), registerModel.password.getValue());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChooseAvatarActivity.REGISTER_REQUEST_CODE && resultCode == RESULT_OK) {
            finish();
        }
    }


}


