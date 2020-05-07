package com.luckyboy.ppd.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.luckyboy.ppd.core.exoplayer.PageListPlayDetector;
import com.luckyboy.ppd.core.exoplayer.PageListPlayerManager;
import com.luckyboy.ppd.core.model.Feed;
import com.luckyboy.ppd.core.ui.AbsListFragment;

public class ProfileListFragment extends AbsListFragment<Feed, ProfileViewModel> {
    private String tabType;
    private PageListPlayDetector playDetector;
    private boolean shouldPause = true;

    public static ProfileListFragment newInstance(String tabType) {
        Bundle args = new Bundle();
        args.putString(ProfileActivity.KEY_TAB_TYPE, tabType);
        ProfileListFragment fragment = new ProfileListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playDetector = new PageListPlayDetector(this, mRecycleView);
        mViewModel.setProfileType(tabType);
        mTwinkRefreshLayout.setEnableRefresh(false);
    }

    @Override
    protected PagedListAdapter getAdapter() {
        tabType = getArguments().getString(ProfileActivity.KEY_TAB_TYPE);
        return new ProfileListAdapter(getContext(), tabType) {
            @Override
            public void onViewAttachedToWindow2(ViewHolder holder) {
                super.onViewAttachedToWindow2(holder);
                if (holder.isVideoItem()) {
                    playDetector.removeTarget(holder.listPlayerView);
                }
            }

            @Override
            public void onViewDetachedFromWindow2(ViewHolder holder) {
                super.onViewDetachedFromWindow2(holder);
                if (holder.isVideoItem()) {
                    playDetector.addTarget(holder.listPlayerView);
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
        // 从评论tab页条跳转到 详情页之后再返回回来
        // 我们需要暂停视频播放 因为评论和Tab页是没有视频的
        if (TextUtils.equals(tabType, ProfileActivity.TAB_TYPE_COMMENT)) {
            playDetector.onPause();
        } else {
            playDetector.onResume();
        }
    }

    @Override
    public void onDestroyView() {
        PageListPlayerManager.release(tabType);
        super.onDestroyView();
    }

    @Override
    public void onTwinkLoadMore(TwinklingRefreshLayout layout) {
        PagedList<Feed> currentList = adapter.getCurrentList();
        finishRefresh(currentList!=null && currentList.size()>0, true);
    }

    @Override
    public void onTwinkRefresh(TwinklingRefreshLayout layout) {

    }


}
