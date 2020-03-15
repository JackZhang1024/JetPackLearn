package com.luckyboy.ppd.login.model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.luckyboy.libcommon.global.AppGlobals;
import com.luckyboy.libcommon.utils.ToastManager;
import com.luckyboy.libnetwork.ApiResponse;
import com.luckyboy.libnetwork.ApiService;
import com.luckyboy.libnetwork.JsonCallback;
import com.luckyboy.libnetwork.Request;
import com.luckyboy.ppd.core.model.User;
import com.luckyboy.ppd.login.UserManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;


// 注册的一些业务逻辑都加在上面了
public class UserViewModel extends ViewModel {

    private static final String TAG = "UserViewModel";

    public MutableLiveData<String> name = new MutableLiveData<>("");
    public MutableLiveData<String> password = new MutableLiveData<>("");
    public MutableLiveData<String> userDescription = new MutableLiveData<>("");
    public MutableLiveData<String> status = new MutableLiveData<>("");
    public MutableLiveData<String> userAvatar = new MutableLiveData<>("");
    private Tencent tencent;

    public void register() {
        ApiService.post("/user/register")
                .addParam("name", name.getValue())
                .addParam("password", password.getValue())
                .addParam("avatar", userAvatar.getValue())
                .addParam("description", userDescription.getValue())
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

    public void onUserDescriptionChanged(CharSequence changedDescription) {
        Log.e(TAG, "onUserDescriptionChanged: "+changedDescription.toString());
        userDescription.setValue(changedDescription.toString());
    }

    public void login() {
        ApiService.post("/user/login")
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

    public void qqLogin(Activity context) {
        if (tencent == null) {
            // jetpack 101794421
            // luckyboy 101861232
            tencent = Tencent.createInstance("101861232", AppGlobals.getInstance());
        }
        tencent.login(context, "all", loginListener);
    }

    private IUiListener loginListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            JSONObject response = (JSONObject) o;
            Log.e(TAG, "onComplete: " + response);
        }

        @Override
        public void onError(UiError uiError) {
            ToastManager.showToast(uiError.toString());
        }

        @Override
        public void onCancel() {
            ToastManager.showToast("取消登录");
        }
    };

    public void wechatLogin() {


    }


}
