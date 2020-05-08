package com.luckyboy.ppd.core;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public abstract class AbsViewModel<T> extends ViewModel {

    private static final String TAG = "AbsViewModel";

    protected PagedList.Config config;
    private DataSource dataSource;
    private LiveData<PagedList<T>> pageData;

    private MutableLiveData<Boolean> boundaryPageData = new MutableLiveData<>();

    DataSource.Factory factory = new DataSource.Factory() {
        @NonNull
        @Override
        public DataSource create() {
            if (dataSource == null || dataSource.isInvalid()) {
                dataSource = createDataSource();
            }
            return dataSource;
        }
    };

    public AbsViewModel() {

        config = new PagedList.Config.Builder()
                .setPageSize(5)
                .setInitialLoadSizeHint(6)
                //.setMaxSize(100)
                //.setEnablePlaceholders(true)
                //.setPrefetchDistance()
                .build();

        pageData = new LivePagedListBuilder(factory, config)
                .setInitialLoadKey(0)
                .setBoundaryCallback(callback)
                .build();
    }


    public LiveData<PagedList<T>> getPageData() {
        return pageData;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public LiveData<Boolean> getBoundaryPageData() {
        return boundaryPageData;
    }

    // BoundaryCallback触发加载更多条件
    PagedList.BoundaryCallback<T> callback = new PagedList.BoundaryCallback<T>() {
        @Override
        public void onZeroItemsLoaded() {
            // 新提交的pagedList中没有数据
            Log.d(TAG, "onZeroItemsLoaded: ++++++++++++++++++++++");
            boundaryPageData.postValue(false);
        }

        @Override
        public void onItemAtFrontLoaded(@NonNull T itemAtFront) {
            // 新提交的PagedList中第一条数据被加载到列表中
            Log.d(TAG, "onItemAtFrontLoaded: ++++++++++++++++++++++");
            boundaryPageData.postValue(true);
        }

        @Override
        public void onItemAtEndLoaded(@NonNull T itemAtEnd) {
            // 新提交的pageList中最后一条数据被加载到列表上
            Log.d(TAG, "onItemAtEndLoaded: ");
        }
    };

    public abstract DataSource createDataSource();

    // 可以在这方法里 进行一些清理的操作
    @Override
    protected void onCleared() {
        super.onCleared();
    }


}
