package com.luckyboy.ppd.find;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
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

public class TagFeedListViewModel extends AbsViewModel<Feed> {
    private static final String TAG = "TagFeedListViewModel";

    private String feedType;
    private AtomicBoolean loadAfter = new AtomicBoolean();

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    @Override
    public DataSource createDataSource() {
        return new DataSource();
    }

    private class DataSource extends ItemKeyedDataSource<Integer, Feed> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            loadData(0, callback);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            Log.e(TAG, "TagFeedListView loadAfter: " + params.key);
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

    private void loadData(Integer feedId, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (feedId >0 ){
            loadAfter.set(true);
        }
        ApiResponse<List<Feed>> response = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("userId", UserManager.get() == null ? 0 : UserManager.get().getUserId())
                .addParam("pageCount", 10)
                .addParam("feedType", feedType)
                .addParam("feedId", feedId)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType())
                .execute();

        List<Feed> result = response.body == null ? Collections.emptyList() : response.body;
        callback.onResult(result);

        if (feedId > 0) {
            loadAfter.set(false);
            // 分页的情况 通知一下UI 本次加载是否有数据 方便UI 关闭上拉加载动画什么的
            ((MutableLiveData) getBoundaryPageData()).postValue(result.size() > 0);
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
