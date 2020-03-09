package com.luckyboy.ppd.login;


import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.luckyboy.libnetwork.cache.CacheManager;
import com.luckyboy.ppd.core.model.User;

// 用户管理
public class UserManager {

    private static final String KEY_CACHE_USER = "cache_user";

    private static UserManager mUserManager = new UserManager();
    private MutableLiveData<User> userLiveData = new MutableLiveData<>();

    private User mUser;


    public static UserManager get() {
        return mUserManager;
    }

    private UserManager() {
        User cache = (User) CacheManager.getCache(KEY_CACHE_USER);
        if (cache != null && cache.expires_time > System.currentTimeMillis()) {
            // 获取缓存的User信息
            mUser = cache;
        }
    }

    public void Save(User user) {
        mUser = user;
        CacheManager.save(KEY_CACHE_USER, user);
        if (userLiveData.hasActiveObservers()){
            userLiveData.postValue(user);
        }
    }

    public boolean isLogin() {
        return mUser == null ? false : mUser.expires_time > System.currentTimeMillis();
    }

    public User getUser() {
        return isLogin() ? mUser : null;
    }

    public long getUserId() {
        return isLogin() ? mUser.userId : 0;
    }


    public void logout() {
        CacheManager.delete(KEY_CACHE_USER, mUser);
        mUser = null;
    }

    public LiveData<User> login(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return userLiveData;
    }


}
