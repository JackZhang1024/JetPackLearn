package com.luckyboy.ppd.core;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 一个可变的ItemKeyedDataSource 数据源
 * <p>
 * 工作原理：我们知道DataSource是会被PagedList持有的
 * <p>
 * 一旦，我们调用了 new PagedList.Builder<Key,Value>().build 那么就会立刻触发当前DataSource 的loadInitial()
 * 方法，而且是同步 详情见 ContiguousPagedList 的构造函数 而我们当前 DataSource 的 loadInitial()方法返回了
 * 最新的数据集合 data 一旦 我们再次 调用  pagedListAdapter.submitList()方法 ，就会触发差分异计算 把新数据
 * 变更到 列表上了
 */
public abstract class MutableItemKeyDataSource<Key, Value> extends ItemKeyedDataSource<Key, Value> {

    private ItemKeyedDataSource mDataSource;

    public List<Value> data = new ArrayList<>();

    public PagedList<Value> buildNewPagedList(PagedList.Config config) {
        PagedList<Value> pagedList = new PagedList.Builder<Key, Value>(this, config)
                .setFetchExecutor(ArchTaskExecutor.getIOThreadExecutor())
                .setNotifyExecutor(ArchTaskExecutor.getMainThreadExecutor())
                .build();
        return pagedList;
    }


    public MutableItemKeyDataSource(ItemKeyedDataSource dataSource) {
        mDataSource = dataSource;
    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams<Key> params, @NonNull LoadInitialCallback<Value> callback) {
        callback.onResult(data);
    }


    @Override
    public void loadAfter(@NonNull LoadParams<Key> params, @NonNull LoadCallback<Value> callback) {
        // 一旦  和 当前DataSource 关联的PagedList 被提交到pagedListAdapter
        // 那么 ViewModel中创建的DataSource 就不会被调用了
        // 我们需要在分页的时候 代理以下 原来的DataSource 迫使其继续工作
        if (mDataSource != null) {
            mDataSource.loadAfter(params, callback);
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Key> params, @NonNull LoadCallback<Value> callback) {
        callback.onResult(Collections.emptyList());
    }


    @NonNull
    @Override
    public abstract Key getKey(@NonNull Value item);
}

