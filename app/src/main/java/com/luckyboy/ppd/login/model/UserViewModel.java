package com.luckyboy.ppd.login.model;

import android.app.Activity;
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
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;


// 注册的一些业务逻辑都加在上面了
public class UserViewModel extends ViewModel {

    private static final String TAG = "UserViewModel";

    public MutableLiveData<String> phone = new MutableLiveData<>("");
    public MutableLiveData<String> password = new MutableLiveData<>("");
    public MutableLiveData<String> name = new MutableLiveData<>("");
    public MutableLiveData<String> userDescription = new MutableLiveData<>("");
    public MutableLiveData<String> status = new MutableLiveData<>("");
    public MutableLiveData<String> userAvatar = new MutableLiveData<>("");
    private Tencent tencent;

    public void register() {
        ApiService.post("/user/register")
                .addParam("phone", phone.getValue())
                .addParam("password", password.getValue())
                .addParam("name", name.getValue())
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

    public void onPhoneChanged(CharSequence changedName) {
        Log.e(TAG, "onPhoneChanged: " + changedName);
        phone.setValue(changedName.toString());
    }

    public void onPasswordChanged(CharSequence changedPassword) {
        Log.e(TAG, "onPasswordChanged: " + changedPassword);
        password.setValue(changedPassword.toString());
    }

    public void onUserDescriptionChanged(CharSequence changedDescription) {
        Log.e(TAG, "onUserDescriptionChanged: " + changedDescription.toString());
        userDescription.setValue(changedDescription.toString());
    }

    public void onNameChanged(CharSequence changedName) {
        name.setValue(changedName.toString());
    }

    public void login() {
        ApiService.post("/user/login")
                .addParam("phone", phone.getValue())
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

    public IUiListener loginListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            JSONObject response = (JSONObject) o;
            try {
                String openid = response.getString("openid");
                String access_token = response.getString("access_token");
                String expires_in = response.getString("expires_in");
                long expires_time = response.getLong("expires_time");

                tencent.setOpenId(openid);
                tencent.setAccessToken(access_token, expires_in);
                QQToken qqToken = tencent.getQQToken();
                getUserInfo(qqToken, expires_time, openid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    private void getUserInfo(QQToken qqToken, long expires_time, String openid) {
        UserInfo userInfo = new UserInfo(AppGlobals.getInstance(), qqToken);
        userInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject response = (JSONObject) o;
                try {
                    String nickName = response.getString("nickname");
                    String figureurl_2 = response.getString("figureurl_2");
                    save(nickName, figureurl_2, openid, expires_time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                ToastManager.showToast("登录失败 " + uiError.toString());
            }

            @Override
            public void onCancel() {
                ToastManager.showToast("取消登录");
            }
        });
    }

    private void save(String nickName, String figureurl_2, String openid, long expires_time) {
        ApiService.post("/user/register")
                .addParam("phone", "")
                .addParam("password", "")
                .addParam("name", nickName)
                .addParam("avatar", figureurl_2)
                .addParam("qqOpenId", openid)
                .addParam("expires_time", expires_time)
                .addParam("description", "")
                .responseType(User.class)
                .cacheStrategy(Request.NET_ONLY)
                .execute(new JsonCallback<User>() {
                    @Override
                    public void onSuccess(ApiResponse<User> response) {
                        super.onSuccess(response);
                        User user = response.body;
                        if (user != null) {
                            UserManager.get().save(user);
                            status.postValue("success");
                        } else {
                            ToastManager.showToast("登录失败");
                        }
                    }

                    @Override
                    public void onError(ApiResponse<User> response) {
                        super.onError(response);
                        ToastManager.showToast(response.message);
                    }
                });
    }

    public void wechatLogin() {


    }


}
