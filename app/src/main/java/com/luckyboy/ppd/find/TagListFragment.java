package com.luckyboy.ppd.find;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.luckyboy.ppd.core.MutableItemKeyDataSource;
import com.luckyboy.ppd.core.model.TagList;
import com.luckyboy.ppd.core.ui.AbsListFragment;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

// 关注和推荐两个Tab页面
public class TagListFragment extends AbsListFragment<TagList, TagListViewModel> {

    private static final String TAG = "TagListFragment";

    public static final String KEY_TAG_TYPE = "tag_type";
    private String tagType;

    public static TagListFragment newInstance(String tagType) {
        Bundle args = new Bundle();
        args.putString(KEY_TAG_TYPE, tagType);
        TagListFragment fragment = new TagListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated: xxxxxxxx " + tagType);
        if (TextUtils.equals(tagType, "onlyFollow")) {
            // 刚开始没有数据的时候开始加载数据
            Log.e(TAG, "onViewCreated: 还没有数据 等待重新加载");
        }
        mRecycleView.removeItemDecorationAt(0);
        mViewModel.setTagType(tagType);
    }

    @Override
    protected PagedListAdapter getAdapter() {
        tagType = getArguments().getString(KEY_TAG_TYPE);
        TagListAdapter tagListAdapter = new TagListAdapter(getContext());
        return tagListAdapter;
    }


    // 加载更多数据
    @Override
    public void onLoadMore(RefreshLayout layout) {
        PagedList<TagList> currentList = getAdapter().getCurrentList();
        long tagId = currentList == null ? 0 : currentList.get(currentList.size() - 1).tagId;
        Log.e(TAG, "onTwinkLoadMore: tagId " + tagId);
        mViewModel.loadAfter(tagId, new ItemKeyedDataSource.LoadCallback() {
            @Override
            public void onResult(@NonNull List data) {
                Log.e(TAG, "onResult: CurrentThreadName " + Thread.currentThread().getName() + " size  " + data.size());
                if (data != null) {
                    if (data.size() > 0) {
                        MutableItemKeyDataSource<Long, TagList> mutableItemKeyDataSource =
                                new MutableItemKeyDataSource<Long, TagList>((ItemKeyedDataSource) mViewModel.getDataSource()) {
                                    @NonNull
                                    @Override
                                    public Long getKey(@NonNull TagList item) {
                                        return item.tagId;
                                    }
                                };
                        mutableItemKeyDataSource.data.addAll(currentList);
                        mutableItemKeyDataSource.data.addAll(data);
                        PagedList<TagList> pagedList = mutableItemKeyDataSource.buildNewPagedList(currentList.getConfig());
                        submitList(pagedList);
                    } else {
                        finishWithNoMoreData();
                    }
                }
            }
        });
    }


    @Override
    public void onRefresh(RefreshLayout layout) {
        mViewModel.getDataSource().invalidate();
    }
}
