package com.luckyboy.ppd.mine;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.luckyboy.ppd.core.exoplayer.PageListPlayDetector;
import com.luckyboy.ppd.core.exoplayer.PageListPlayerManager;
import com.luckyboy.ppd.core.model.Feed;
import com.luckyboy.ppd.core.ui.AbsListFragment;
import com.luckyboy.ppd.home.FeedAdapter;

public class UserBehaviorListFragment extends AbsListFragment<Feed, UserBehaviorViewModel> {
    private static final String CATEGORY = "user_behavior_list";
    private boolean shouldPause = true;
    private PageListPlayDetector playDetector;

    public static  UserBehaviorListFragment newInstance(int behavior){
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
        return new FeedAdapter(getContext(), CATEGORY){
            @Override
            public void onViewAttachedToWindow2(ViewHolder holder) {
                super.onViewAttachedToWindow2(holder);
                if (holder.isVideoItem()){
                    playDetector.addTarget(holder.listPlayerView);
                }
            }

            @Override
            public void onViewDetachedFromWindow2(ViewHolder holder) {
                super.onViewDetachedFromWindow2(holder);
                if (holder.isVideoItem()){
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
        if (shouldPause){
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
    public void onTwinkLoadMore(TwinklingRefreshLayout layout) {
        PagedList<Feed> currentList = adapter.getCurrentList();
        finishRefresh(currentList!=null && currentList.size()>0, true);
    }

    @Override
    public void onTwinkRefresh(TwinklingRefreshLayout layout) {
        mViewModel.getDataSource().invalidate();
    }


}
