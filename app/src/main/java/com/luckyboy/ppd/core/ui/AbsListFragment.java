package com.luckyboy.ppd.core.ui;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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

// 抽象列表
public abstract class AbsListFragment<T, M extends AbsViewModel> extends Fragment {

    protected FragmentFreshBinding binding;

    protected RecyclerView mRecycleView;

    protected TwinklingRefreshLayout mTwinkRefreshLayout;

    protected EmptyView mEmptyView;

    protected PagedListAdapter<T, RecyclerView.ViewHolder> adapter;
    protected M mViewModel;

    protected DividerItemDecoration decoration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFreshBinding.inflate(inflater, container, false);
        binding.getRoot().setFitsSystemWindows(true);
        mRecycleView = binding.rv;
        mTwinkRefreshLayout = binding.twkRefreshLayout;
        mEmptyView = binding.emptyView;
        adapter = getAdapter();

        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecycleView.setItemAnimator(null);

        decoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
        mRecycleView.addItemDecoration(decoration);
        mRecycleView.setAdapter(adapter);
        mTwinkRefreshLayout.setOnRefreshListener(new TwinkRefreshListener());
        return binding.getRoot();

    }


    protected abstract PagedListAdapter<T, RecyclerView.ViewHolder> getAdapter();

    class TwinkRefreshListener extends RefreshListenerAdapter {

        @Override
        public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
            super.onLoadMore(refreshLayout);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.onFinishLoadMore();
                }
            }, 2000);
        }


        @Override
        public void onRefresh(TwinklingRefreshLayout refreshLayout) {
            super.onRefresh(refreshLayout);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.finishRefreshing();
                }
            }, 2000);
        }
    }

}


