package com.luckyboy.ppd.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.luckyboy.jetpacklearn.R;
import com.luckyboy.jetpacklearn.databinding.ActivityPpdRegisterBinding;
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

    public void doRegister(View view) {
        registerModel.register();
    }

}

