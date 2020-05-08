package com.luckyboy.ppd.home;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

import com.alibaba.fastjson.TypeReference;
import com.luckyboy.libnetwork.ApiResponse;
import com.luckyboy.libnetwork.ApiService;
import com.luckyboy.libnetwork.JsonCallback;
import com.luckyboy.libnetwork.Request;
import com.luckyboy.ppd.core.AbsViewModel;
import com.luckyboy.ppd.core.MutablePageKeyedDataSource;
import com.luckyboy.ppd.core.model.Feed;
import com.luckyboy.ppd.login.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


// 首页视频列表相关信息
public class HomeViewModel extends AbsViewModel<Feed> {
    private static final String TAG = "HomeViewModel";
    private volatile boolean withCache = true;

    private MutableLiveData<PagedList<Feed>> cacheLiveData = new MutableLiveData<>();
    private AtomicBoolean loadAfter = new AtomicBoolean(false);
    private String mFeedType;

    /**
     * java.lang.NullPointerException: Attempt to invoke virtual method 'void androidx.paging.DataSource.addInvalidatedCallback
     * (androidx.paging.DataSource$InvalidatedCallback)' on a null object reference
     * at androidx.paging.LivePagedListBuilder$1.compute(LivePagedListBuilder.java:195)
     * at androidx.paging.LivePagedListBuilder$1.compute(LivePagedListBuilder.java:167)
     * at androidx.lifecycle.ComputableLiveData$2.run(ComputableLiveData.java:101)
     * at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
     * at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
     * at java.lang.Thread.run(Thread.java:764)
     */
    @Override
    public DataSource createDataSource() {
        return new FeedDataSource();
    }

    // 跟母当前列表最后一项的数据的id来查找数据
    class FeedDataSource extends ItemKeyedDataSource<Integer, Feed> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            // 加载初始化数据
            Log.e(TAG, "loadInitial: ");
            // 开始加载数据
            loadData(0, params.requestedLoadSize, callback);
            withCache = false;
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            // 向后加载分页数据
            Log.e(TAG, "loadAfter: 加载更多数据.......");
            loadData(params.key, params.requestedLoadSize, callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            // 向前加载数据的
            callback.onResult(Collections.emptyList());
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }
    }

    private void loadData(int key, int count, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (key > 0) {
            loadAfter.set(true);
        }
        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("feedType", mFeedType)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("feedId", key)
                .addParam("pageCount", count)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());
        if (withCache) {
            // 第一页用本地缓存数据？？这操作不错
            request.cacheStrategy(Request.CACHE_ONLY);
            request.execute(new JsonCallback<List<Feed>>() {
                @Override
                public void onCacheSuccess(ApiResponse<List<Feed>> response) {
                    Log.e(TAG, "onCacheSuccess: ");
                    // 这块为毛用 这种方式来回传数据 待会测试看看
                    MutablePageKeyedDataSource dataSource = new MutablePageKeyedDataSource<Feed>();
                    dataSource.data.addAll(response.body);
                    PagedList pagedList = dataSource.buildNewPagedList(config);
                    // postValue 之后 主线程中的观察者就可以看到数据过来了 就进行更新操作了
                    cacheLiveData.postValue(pagedList);

                    // java.lang.IllegalSateException: callback.onResult already called, cannot call again;
                    //if (callback != null) {
                    //    callback.onResult(response.body);
                    //}
                }
            });
        }

        try {
            Request netRequest = withCache ? request.clone() : request;
            netRequest.cacheStrategy(key == 0 ? Request.NET_CACHE : Request.NET_ONLY);
            ApiResponse<List<Feed>> response = netRequest.execute();
            List<Feed> data = response.body == null ? Collections.emptyList() : response.body;
            callback.onResult(data);

            if (key > 0) {
                // 通过 BoundaryPageData 发送数据 告诉UI层 是否应该主动关闭上拉加载分页的动画
                ((MutableLiveData) getBoundaryPageData()).postValue(data.size() > 0);
                loadAfter.set(false);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "loadData: key " + key);
    }

    @SuppressLint("RestrictedApi")
    public void loadAfter(int id, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (loadAfter.get()) {
            callback.onResult(Collections.emptyList());
            return;
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(() -> {
            loadData(id, config.pageSize, callback);
        });
    }


    public MutableLiveData<PagedList<Feed>> getCacheLiveData() {
        return cacheLiveData;
    }

    public void setFeedType(String mFeedType) {
        this.mFeedType = mFeedType;
    }


}
