package com.luckyboy.ppd.mine;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.ItemKeyedDataSource;

import com.alibaba.fastjson.TypeReference;
import com.luckyboy.libnetwork.ApiResponse;
import com.luckyboy.libnetwork.ApiService;
import com.luckyboy.ppd.core.AbsViewModel;
import com.luckyboy.ppd.core.model.Feed;
import com.luckyboy.ppd.login.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class ProfileViewModel extends AbsViewModel<Feed> {
    private String profileType;
    private AtomicBoolean loadAfter = new AtomicBoolean(false);

    @Override
    public DataSource createDataSource() {
        return new DataSource();
    }

    public void setProfileType(String tabType) {
        this.profileType = tabType;
    }

    private class DataSource extends ItemKeyedDataSource<Integer, Feed> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            loadData(params.requestedInitialKey, callback);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            loadData(params.key, callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            callback.onResult(Collections.emptyList());
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }

    }

    private void loadData(Integer key, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (key>0){
            loadAfter.set(true);
        }
        ApiResponse<List<Feed>> response = ApiService.get("/feeds/queryProfileFeeds")
                .addParam("feedId", key)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("pageCount", 10)
                .addParam("profileType", profileType)
                .responseType(
                        new TypeReference<ArrayList<Feed>>() {
                        }.getType())
                .execute();
        List<Feed> result = response.body == null ? Collections.emptyList() : response.body;
        callback.onResult(result);

        if (key > 0) {
            // 告知UI层 本次分页是否有更多数据被加载回来了 也方便UI层
            // 关闭上拉加载的动画
            ((MutableLiveData) getBoundaryPageData()).postValue(result.size() > 0);
            loadAfter.set(false);
        }
    }

    @SuppressLint("RestrictedApi")
    public void loadAfter(int id, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (loadAfter.get()) {
            callback.onResult(Collections.emptyList());
            return;
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(() -> {
            loadData(id, callback);
        });
    }

}
