package com.luckyboy.ppd.detail;

import android.view.LayoutInflater;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.luckyboy.jetpacklearn.R;
import com.luckyboy.jetpacklearn.databinding.LayoutFeedDetailTypeVideoBinding;
import com.luckyboy.jetpacklearn.databinding.LayoutFeedDetailTypeVideoHeaderBinding;
import com.luckyboy.ppd.core.model.Feed;
import com.luckyboy.ppd.core.view.FullScreenPlayerView;

public class VideoViewHandler extends ViewHandler {
    private final CoordinatorLayout coordinatorLayout;
    private FullScreenPlayerView playerView;
    private LayoutFeedDetailTypeVideoBinding mVideoBinding;
    private String category;
    private boolean backPressed;


    public VideoViewHandler(FragmentActivity activity) {
        super(activity);
        mVideoBinding = DataBindingUtil.setContentView(activity, R.layout.layout_feed_detail_type_video);

        mInteractionBinding = mVideoBinding.bottomInteraction;
        mRecycleView = mVideoBinding.recyclerView;
        playerView = mVideoBinding.playerView;
        coordinatorLayout = mVideoBinding.coordinator;

        View authorInfoView = mVideoBinding.authorInfo.getRoot();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) authorInfoView.getLayoutParams();
        params.setBehavior(new ViewAnchorBehavior(R.id.player_view));

        mVideoBinding.actionClose.setOnClickListener(v -> {
            doExitFullScreen();
            mActivity.finish();
        });

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) playerView.getLayoutParams();
        ViewZoomBehavior behavior = (ViewZoomBehavior) layoutParams.getBehavior();
        behavior.setViewZoomCallback(new ViewZoomBehavior.ViewZoomCallback() {
            @Override
            public void onDragZoom(int height) {
                int bottom = playerView.getBottom();
                boolean moveUp = height < bottom;
                boolean fullScreen = moveUp ? height >= coordinatorLayout.getBottom() - mInteractionBinding.getRoot().getHeight()
                        : height >= coordinatorLayout.getBottom();
                setViewAppearance(fullScreen);
            }
        });
    }


    @Override
    public void bindInitData(Feed feed) {
        super.bindInitData(feed);
        mVideoBinding.setFeed(feed);

        category = mActivity.getIntent().getStringExtra(FeedDetailActivity.KEY_CATEGORY);
        playerView.bindData(category, mFeed.width, mFeed.height, mFeed.cover, mFeed.url);

        // 这里需要延迟一阵 等待布局完成 再来拿playerView的bottom 和 coordinator的bottom 值
        // 做个比较 来校验是否进入详情页时 视频在全屏播放
        playerView.post(() -> {
            boolean fullScreen = playerView.getBottom() >= coordinatorLayout.getBottom();
            setViewAppearance(fullScreen);
        });

        // 给HeaderView 绑定数据并添加到列表上
        LayoutFeedDetailTypeVideoHeaderBinding headerBinding = LayoutFeedDetailTypeVideoHeaderBinding.inflate(
                LayoutInflater.from(mActivity), mRecycleView, false);
        headerBinding.setFeed(feed);
        listAdapter.addHeaderView(headerBinding.getRoot());
    }

    private void setViewAppearance(boolean fullScreen) {
        mVideoBinding.setFullScreen(fullScreen);
        mInteractionBinding.setFullScreen(fullScreen);
        mVideoBinding.fullscreenAuthorInfo.getRoot().setVisibility(fullScreen ? View.VISIBLE : View.GONE);

        // 底部互动区域的高度
        int inputHeight = mInteractionBinding.getRoot().getMeasuredHeight();
        // 播放控制器的高度
        int ctrlViewHeight = playerView.getPlayController().getMeasuredHeight();
        // 播放控制器的bottom值
        int bottom = playerView.getPlayController().getBottom();
        // 全屏播放时 播放控制按钮需要处在底部互动区域的上面
        playerView.getPlayController().setY(fullScreen ? bottom - inputHeight - ctrlViewHeight : bottom - ctrlViewHeight);
        mInteractionBinding.inputView.setBackgroundResource(fullScreen ? R.drawable.bg_edit_view2 : R.drawable.bg_edit_view);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        doExitFullScreen();
    }

    private void doExitFullScreen() {
        backPressed = true;
        // 播放控制器的高度
        int ctrlViewHeight = playerView.getPlayController().getMeasuredHeight();
        // 播放控制器的bottom值
        int bottom = playerView.getPlayController().getBottom();
        // 按了返回键后需要 恢复 播放控制按钮的位置  否则回到列表时 可能会不正确的显示
        playerView.getPlayController().setTranslationY(0);
        //playerView.getPlayController().setY(bottom - ctrlViewHeight);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!backPressed) {
            playerView.inActive();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        backPressed = false;
        playerView.onActive();
    }


}
