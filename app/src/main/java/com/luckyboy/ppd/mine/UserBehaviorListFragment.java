package com.luckyboy.ppd.mine;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.luckyboy.ppd.core.MutablePageKeyedDataSource;
import com.luckyboy.ppd.core.exoplayer.PageListPlayDetector;
import com.luckyboy.ppd.core.exoplayer.PageListPlayerManager;
import com.luckyboy.ppd.core.model.Feed;
import com.luckyboy.ppd.core.ui.AbsListFragment;
import com.luckyboy.ppd.home.FeedAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

public class UserBehaviorListFragment extends AbsListFragment<Feed, UserBehaviorViewModel> {
    private static final String CATEGORY = "user_behavior_list";
    private boolean shouldPause = true;
    private PageListPlayDetector playDetector;

    private static final String TAG = "UserBehaviorList";

    public static UserBehaviorListFragment newInstance(int behavior) {
        Bundle args = new Bundle();
        args.putInt(UserBehaviorListActivity.KEY_BEHAVIOR, behavior);
        UserBehaviorListFragment fragment = new UserBehaviorListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playDetector = new PageListPlayDetector(this, mRecycleView);
        int behavior = getArguments().getInt(UserBehaviorListActivity.KEY_BEHAVIOR);
        mViewModel.setBehavior(behavior);
    }

    @Override
    protected PagedListAdapter getAdapter() {
        return new FeedAdapter(getContext(), CATEGORY) {
            @Override
            public void onViewAttachedToWindow2(ViewHolder holder) {
                super.onViewAttachedToWindow2(holder);
                if (holder.isVideoItem()) {
                    playDetector.addTarget(holder.listPlayerView);
                }
            }

            @Override
            public void onViewDetachedFromWindow2(ViewHolder holder) {
                super.onViewDetachedFromWindow2(holder);
                if (holder.isVideoItem()) {
                    playDetector.removeTarget(holder.listPlayerView);
                }
            }

            @Override
            public void onStartFeedDetailActivity(Feed feed) {
                super.onStartFeedDetailActivity(feed);
                shouldPause = false;
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        if (shouldPause) {
            playDetector.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        shouldPause = true;
        playDetector.onResume();
    }

    @Override
    public void onDestroyView() {
        PageListPlayerManager.release(CATEGORY);
        super.onDestroyView();
    }

    @Override
    public void onLoadMore(RefreshLayout layout) {
//        PagedList<Feed> currentList = adapter.getCurrentList();
//        finishRefresh(currentList != null && currentList.size() > 0);

        final PagedList<Feed> currentList = adapter.getCurrentList();
        if (currentList == null || currentList.size() <= 0) {
            finishRefresh(false);
            return;
        }
        Feed feed = currentList.get(adapter.getItemCount() - 1);
        mViewModel.loadAfter(feed.id, new ItemKeyedDataSource.LoadCallback<Feed>() {
            @Override
            public void onResult(@NonNull List<Feed> data) {
                PagedList.Config config = currentList.getConfig();
                if (data != null) {
                    if (data.size() > 0) {
                        // 这里 咋们手动接管 分页数据加载的时候 使用MutableItemKeyedDataSource 也是可以的
                        // 由于当且仅当 paging 不再帮我们分页的时候 我们才会接管 所以就不要ViewModel中
                        // 创建的DataSource 继续工作了 使用使用 MutablePageKeyedDtaSource也是可以的
                        MutablePageKeyedDataSource dataSource = new MutablePageKeyedDataSource();

                        // 这里把列表上已经显示的先添加到dataSource.data中
                        // 而后把本次分页回来的数据在添加到dataSource.dta中
                        dataSource.data.addAll(currentList);
                        dataSource.data.addAll(data);
                        PagedList pagedList = dataSource.buildNewPagedList(config);
                        Log.e(TAG, "onResult: onLoadMore-------");
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
