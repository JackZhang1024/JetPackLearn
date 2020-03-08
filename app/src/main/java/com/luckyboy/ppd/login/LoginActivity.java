package com.luckyboy.ppd.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.luckyboy.jetpacklearn.R;
import com.luckyboy.jetpacklearn.databinding.ActivityPpdLoginBinding;
import com.luckyboy.ppd.login.model.UserViewModel;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    ActivityPpdLoginBinding binding;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ppd_login);
        binding.toolbar.setNavigationOnClickListener((view) -> {
            finish();
        });
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        binding.setModel(userViewModel);
        initObservers();
    }

    private void initObservers() {
        userViewModel.name.observe(this, value -> {

        });
        userViewModel.password.observe(this, value -> {

        });
        userViewModel.status.observe(this, value -> {
            if ("success".equals(value)) {
                finish();
            }
        });
    }

    public void register(View view) {
        RegisterActivity.launchActivity(this);
    }


    public void doLogin(View view) {
        userViewModel.login();
    }

}

