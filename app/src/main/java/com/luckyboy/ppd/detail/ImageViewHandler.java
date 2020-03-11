package com.luckyboy.ppd.detail;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.luckyboy.jetpacklearn.R;
import com.luckyboy.jetpacklearn.databinding.ActivityFeedDetailTypeImageBinding;
import com.luckyboy.jetpacklearn.databinding.LayoutFeedDetailTypeImageHeaderBinding;
import com.luckyboy.libcommon.view.PPImageView;
import com.luckyboy.ppd.core.model.Feed;

public class ImageViewHandler extends ViewHandler {

    protected ActivityFeedDetailTypeImageBinding mImageBinding;
    protected LayoutFeedDetailTypeImageHeaderBinding mHeaderBinding;


    public ImageViewHandler(FragmentActivity activity) {
        super(activity);
        mImageBinding = DataBindingUtil.setContentView(activity, R.layout.activity_feed_detail_type_image);
        // TODO: 2020-03-10 嵌套布局 binding的使用
        mInteractionBinding = mImageBinding.interactionLayout;
        mRecycleView = mImageBinding.recycleView;
        mImageBinding.actionClose.setOnClickListener(v -> {
            mActivity.finish();
        });
    }

    @Override
    public void bindInitData(Feed feed) {
        super.bindInitData(feed);
        mImageBinding.setFeed(feed);

        mHeaderBinding = LayoutFeedDetailTypeImageHeaderBinding.inflate(LayoutInflater.from(mActivity), mRecycleView, false);
        mHeaderBinding.setFeed(feed);

        PPImageView headerImage = mHeaderBinding.headerImage;
        headerImage.bindData(mFeed.width, mFeed.height, mFeed.width > mFeed.height ? 0 : 16, mFeed.cover);
        listAdapter.addHeaderView(mHeaderBinding.getRoot());

        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean visible = mHeaderBinding.getRoot().getTop() <= -mImageBinding.titleLayout.getMeasuredHeight();
                mImageBinding.authorInfoLayout.getRoot().setVisibility(visible? View.VISIBLE:View.GONE);
                mImageBinding.title.setVisibility(visible? View.GONE: View.VISIBLE);
            }
        });
        handleEmpty(false);
    }

}
