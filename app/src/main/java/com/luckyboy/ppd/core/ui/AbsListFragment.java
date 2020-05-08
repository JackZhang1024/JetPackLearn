package com.luckyboy.ppd.core.ui;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.luckyboy.jetpacklearn.R;
import com.luckyboy.jetpacklearn.databinding.FragmentFreshBinding;
import com.luckyboy.libcommon.view.EmptyView;
import com.luckyboy.ppd.core.AbsViewModel;
import com.luckyboy.ppd.core.util.RecyclerViewUtils;
import com.luckyboy.ppd.core.view.LoadMoreDecoration;
import com.luckyboy.ppd.core.view.PPDClassicHeader;
import com.luckyboy.ppd.core.view.PPDTwinkRefreshLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

// 抽象列表
// T 表示我列表想要的数据类型 M 表示我想要的ViewModel 一些重要的业务逻辑操作都在里面了
public abstract class AbsListFragment<T, M extends AbsViewModel> extends Fragment {

    private static final String TAG = "AbsListFragment";
    protected FragmentFreshBinding binding;

    protected SwipeRecyclerView mRecycleView;

    protected SmartRefreshLayout mTwinkRefreshLayout;

    protected EmptyView mEmptyView;

    protected PagedListAdapter<T, RecyclerView.ViewHolder> adapter;
    protected M mViewModel;

    protected LoadMoreDecoration decoration;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFreshBinding.inflate(inflater, container, false);
        binding.getRoot().setFitsSystemWindows(true);
        mRecycleView = binding.rv;
        mTwinkRefreshLayout = binding.twkRefreshLayout;
        mEmptyView = binding.emptyView;
        adapter = getAdapter();

        RecyclerViewUtils viewUtils = new RecyclerViewUtils();
        viewUtils.initRecyclerView(getContext(), mRecycleView, new SwipeRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                // 加载更多数据操作
                Log.e(TAG, "onLoadMore: 加载更多");
                onTwinkLoadMore(null);
            }
        });
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //mRecycleView.addItemDecoration(new LoadMoreDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecycleView.setItemAnimator(null);

        decoration = new LoadMoreDecoration(getContext(), LinearLayoutManager.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
        mRecycleView.addItemDecoration(decoration);
        mRecycleView.setAdapter(adapter);
        mTwinkRefreshLayout.setEnableRefresh(true);
        mTwinkRefreshLayout.setEnableLoadMore(false);
        mTwinkRefreshLayout.setRefreshHeader(new PPDClassicHeader(getContext()));
        mTwinkRefreshLayout.setOnRefreshListener(new TwinkRefreshListener());
        genericViewModel();
        return binding.getRoot();
    }


    private void genericViewModel() {
        // 利用子类传递的 泛型参数 实例化出 absViewModel 对象
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] arguments = type.getActualTypeArguments();
        if (arguments.length > 1) {
            // 取出的是第一个参数  ViewModel对应的泛型类型
            Type argument = arguments[1];
            Class claz = ((Class) argument).asSubclass(AbsViewModel.class);
            mViewModel = (M) ViewModelProviders.of(this).get(claz);

            // 触发页面初始化数据加载的逻辑
            mViewModel.getPageData().observe(getViewLifecycleOwner(), new Observer<PagedList<T>>() {
                @Override
                public void onChanged(PagedList<T> pagedList) {
                    submitList(pagedList);
                }
            });
            // 监听分页时有无更多数据 以决定是否关闭上拉加载的动画
            mViewModel.getBoundaryPageData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean hasData) {
                    finishRefresh(hasData, true);
                }
            });
        }
    }

    public void submitList(PagedList<T> pagedList) {
        // 只有当新数据集合大于0 的时候 才会调用 adapter.submitList
        if (pagedList.size() > 0) {
            adapter.submitList(pagedList);
        }
        finishRefresh(pagedList.size() > 0, true);
    }

    public void finishRefresh(boolean hasData, boolean loadMore) {
        PagedList<T> currentList = adapter.getCurrentList();
        hasData = hasData || currentList != null && currentList.size() > 0;
        if (mTwinkRefreshLayout.getState() == RefreshState.Refreshing) {
            mTwinkRefreshLayout.finishRefresh();
        } else if (loadMore) {
            // 处于加载更多状态
            mRecycleView.loadMoreFinish(false, false);
        }
        if (hasData) {
            mRecycleView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        } else {
            mRecycleView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    // 因而 我们在onCreateView 的时候 创建了 PagedListAdapgter
    // 所以 如果 arguments 有参数需要传递到Adapter中 ，那么需要在getAdapter()方法中取出参数
    protected abstract PagedListAdapter<T, RecyclerView.ViewHolder> getAdapter();

    class TwinkRefreshListener implements OnRefreshListener {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            onTwinkRefresh(null);
        }
    }


    public abstract void onTwinkLoadMore(TwinklingRefreshLayout layout);

    public abstract void onTwinkRefresh(TwinklingRefreshLayout layout);

}


