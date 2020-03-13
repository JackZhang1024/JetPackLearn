package com.luckyboy.ppd.login.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.luckyboy.libcommon.utils.ToastManager;
import com.luckyboy.libnetwork.ApiResponse;
import com.luckyboy.libnetwork.ApiService;
import com.luckyboy.libnetwork.JsonCallback;
import com.luckyboy.libnetwork.Request;
import com.luckyboy.ppd.core.model.User;
import com.luckyboy.ppd.login.UserManager;


// 注册的一些业务逻辑都加在上面了
public class UserViewModel extends ViewModel {

    private static final String TAG = "UserViewModel";

    public MutableLiveData<String> name = new MutableLiveData<>("");
    public MutableLiveData<String> password = new MutableLiveData<>("");
    public MutableLiveData<String> status = new MutableLiveData<>("");


    public void register() {
        ApiService.post("/user/insertNormal")
                .addParam("name", name.getValue())
                .addParam("password", password.getValue())
                .responseType(User.class)
                .cacheStrategy(Request.NET_ONLY)
                .execute(new JsonCallback<User>() {
                    @Override
                    public void onSuccess(ApiResponse<User> response) {
                        super.onSuccess(response);
                        User user = response.body;
                        Log.e(TAG, "onSuccess: " + user.name);
                        ToastManager.showToast("注册成功");
                        // 跳转回登录页面
                        status.postValue("success");
                        //name.postValue();
                    }

                    @Override
                    public void onError(ApiResponse<User> response) {
                        super.onError(response);
                        ToastManager.showToast(response.message);
                    }
                });
    }

    public void onNameChanged(CharSequence changedName) {
        Log.e(TAG, "onPhoneChanged: " + changedName);
        name.setValue(changedName.toString());
    }

    public void onPasswordChanged(CharSequence changedPassword) {
        Log.e(TAG, "onPasswordChanged: " + changedPassword);
        password.setValue(changedPassword.toString());
    }


    // http://localhost:8080/serverdemo/user/queryNormal?name=hash&password=123456
    public void login() {
        ApiService.post("/user/queryNormal")
                .addParam("name", name.getValue())
                .addParam("password", password.getValue())
                .responseType(User.class)
                .execute(new JsonCallback<User>() {
                    @Override
                    public void onSuccess(ApiResponse<User> response) {
                        super.onSuccess(response);
                        User user = response.body;
                        UserManager.get().save(user);
                        status.postValue("success");
                    }

                    @Override
                    public void onError(ApiResponse<User> response) {
                        super.onError(response);
                        ToastManager.showToast(response.message);
                    }
                });
    }


}
