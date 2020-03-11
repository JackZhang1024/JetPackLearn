package com.luckyboy.ppd.detail;

import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luckyboy.jetpacklearn.databinding.LayoutFeedDetailBottomInteractionBinding;
import com.luckyboy.libcommon.utils.PixUtils;
import com.luckyboy.libcommon.utils.ToastManager;
import com.luckyboy.libcommon.view.EmptyView;
import com.luckyboy.ppd.core.model.Comment;
import com.luckyboy.ppd.core.model.Feed;

public abstract class ViewHandler {

    private static final String TAG = "ViewHandler";

    private final FeedDetailViewModel viewModel;
    protected FragmentActivity mActivity;
    protected Feed mFeed;
    protected RecyclerView mRecycleView;
    protected LayoutFeedDetailBottomInteractionBinding mInteractionBinding;
    protected FeedCommentAdapter listAdapter;
    private CommentDialog commentDialog;

    public ViewHandler(FragmentActivity activity) {
        mActivity = activity;
        viewModel = ViewModelProviders.of(activity).get(FeedDetailViewModel.class);
    }

    @CallSuper
    public void bindInitData(Feed feed) {
        mInteractionBinding.setOwner(mActivity);
        mFeed = feed;
        mRecycleView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mRecycleView.setItemAnimator(null);
        listAdapter = new FeedCommentAdapter(mActivity) {
            @Override
            public void onCurrentListChanged(@Nullable PagedList<Comment> previousList, @Nullable PagedList<Comment> currentList) {
                boolean empty = currentList.size() <= 0;
                handleEmpty(empty);
            }
        };
        mRecycleView.setAdapter(listAdapter);
        viewModel.setItemId(mFeed.itemId);
        viewModel.getPageData().observe(mActivity, new Observer<PagedList<Comment>>() {
            @Override
            public void onChanged(PagedList<Comment> comments) {
                // 提交数据
                Log.e(TAG, "onChanged: 返回的评论数据个数 " + comments.size());
                listAdapter.submitList(comments);
                handleEmpty(comments.size() > 0);
            }
        });
        mInteractionBinding.inputView.setOnClickListener((view) -> {
            showCommentDialog();
        });
    }

    private EmptyView mEmptyView;

    public void handleEmpty(boolean hasData) {
        if (hasData) {
            if (mEmptyView != null) {
                listAdapter.removeHeaderView(mEmptyView);
            }
        } else {
            if (mEmptyView == null) {
                mEmptyView = new EmptyView(mActivity);
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.topMargin = PixUtils.dp2px(40);
                mEmptyView.setLayoutParams(params);
                // TODO: 2020-03-10 设置提示文字
            }
            listAdapter.addHeaderView(mEmptyView);
        }
    }

    private void showCommentDialog() {
        if (commentDialog == null) {
            commentDialog = CommentDialog.newInstance(mFeed.itemId);
        }
        commentDialog.show(mActivity.getSupportFragmentManager(), "comment_dialog");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onBackPressed() {

    }


}
