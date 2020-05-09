package com.luckyboy.ppd.core.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.luckyboy.jetpacklearn.R;
import com.luckyboy.libcommon.utils.PixUtils;
import com.luckyboy.libcommon.view.PPImageView;
import com.luckyboy.ppd.core.exoplayer.IPlayTarget;
import com.luckyboy.ppd.core.exoplayer.PageListPlay;
import com.luckyboy.ppd.core.exoplayer.PageListPlayerManager;

/**
 * 视频播放列表
 */
public class ListPlayerView extends FrameLayout implements IPlayTarget, PlayerControlView.VisibilityListener, Player.EventListener {

    public View bufferView;

    public PPImageView cover, blur;

    protected ImageView playerBtn;

    protected String mCategory;

    protected String mVideoUrl;

    protected boolean isPlaying;

    protected int mWidthPx;

    protected int mHeightPx;


    public ListPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_player_view, this, true);

        // 缓冲转圈的view
        bufferView = findViewById(R.id.buffer_view);
        // 封面View
        cover = findViewById(R.id.cover);
        // 高斯模糊背景图 防止出现两边留黑
        blur = findViewById(R.id.blur_background);
        // 播放和暂停的按钮
        playerBtn = findViewById(R.id.play_btn);
        playerBtn.setOnClickListener((view) -> {
            if (isPlaying()) {
                inActive();
            } else {
                onActive();
            }
        });
        this.setTransitionName("listPlayerView");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 点击该区域时  我们主动让视频控制器显示出来
        PageListPlay pageListPlay = PageListPlayerManager.get(mCategory);
        pageListPlay.controlView.show();
        return true;
    }

    public void bindData(String category, int widthPx, int heightPx, String coverUrl, String videoUrl) {
        mCategory = category;
        mVideoUrl = videoUrl;
        mWidthPx = widthPx;
        mHeightPx = heightPx;
        cover.setImageUrl(coverUrl);
        // 如果该视频的宽度小于高度 则高斯模糊背景图显示出来
        if (widthPx < heightPx) {
            PPImageView.setBlurImageUrl(blur, coverUrl, 10);
            blur.setVisibility(VISIBLE);
        } else {
            // TODO: 2020-03-10 注意的问题 不能为GONE
            blur.setVisibility(INVISIBLE);
        }
        setSize(widthPx, heightPx);
    }

    protected void setSize(int widthPx, int heightPx) {
        // 这里主要是做视频宽大于高 或者高大于宽时  视频的等比缩放
        int maxWidth = PixUtils.getScreenWidth();
        // 最大高度就是屏幕的宽度
        int maxHeight = maxWidth;

        int layoutWidth = maxWidth;
        int layoutHeight = 0;

        int coverWidth;
        int coverHeight = 0;

        if (widthPx >= heightPx) {
            coverWidth = maxWidth;
            layoutHeight = coverHeight = (int) ((heightPx) / (widthPx * 1.0 / maxWidth));
        } else {
            layoutHeight = coverHeight = maxHeight;
            coverWidth = (int) (widthPx / (heightPx * 1.0 / maxHeight));
        }

        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = layoutWidth;
        params.height = layoutHeight;
        setLayoutParams(params);

        ViewGroup.LayoutParams blurParams = blur.getLayoutParams();
        blurParams.width = layoutWidth;
        blurParams.height = layoutHeight;
        blur.setLayoutParams(blurParams);

        FrameLayout.LayoutParams coverParams = (LayoutParams) cover.getLayoutParams();
        coverParams.width = coverWidth;
        coverParams.height = coverHeight;
        coverParams.gravity = Gravity.CENTER;
        cover.setLayoutParams(coverParams);

        FrameLayout.LayoutParams playBtnParams = (LayoutParams) playerBtn.getLayoutParams();
        playBtnParams.gravity = Gravity.CENTER;
        playerBtn.setLayoutParams(playBtnParams);
    }

    @Override
    public ViewGroup getOwner() {
        return this;
    }

    @Override
    public void onActive() {
        // 视频播放 或恢复播放

        // 通过该View所在页面的mCategory（比如首页列表 tab_all,沙发tab的 tab_video 标签帖子聚合的 tag_feed）字段
        // 取出管理该页面的ExoPlayer播放器，Exoplayer播放View 控制器对象 PageListPlay
        PageListPlay pageListPlay = PageListPlayerManager.get(mCategory);
        PlayerView playerView = pageListPlay.playerView;
        PlayerControlView controlView = pageListPlay.controlView;
        SimpleExoPlayer exoPlayer = pageListPlay.exoPlayer;
        if (playerView == null) {
            return;
        }
        // 此处我们需要主动调用一次 switchPlayerView 把播放器Exoplayer 和展示视频画面的View ExoplayerView 相关联
        // 为什么呢？因为在列表页面点击视频Item跳转到视频详情页面的时候 详情页面会复用到列表页面的播放器Exoplayer,
        // 然后和新创建的展示视频画面的View ExoplayerView 相关联 达到视频无缝续播的效果
        pageListPlay.switchPlayerView(playerView, true);
        ViewParent parent = playerView.getParent();
        if (parent != this) {

            // 把展示视频画面的View添加到Item的容器上
            if (parent != null) {
                ((ViewGroup) parent).removeView(playerView);
                // 还应该暂停列表上正在播放的那个
                ((ListPlayerView) parent).inActive();
            }
            ViewGroup.LayoutParams coverParams = cover.getLayoutParams();
            this.addView(playerView, 1, coverParams);
        }
        ViewParent ctrlParent = controlView.getParent();
        if (ctrlParent != this) {
            // 把视频控制器 添加到ItemView的容器上
            if (ctrlParent != null) {
                ((ViewGroup) (ctrlParent)).removeView(controlView);
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            this.addView(controlView, params);
        }
        // 如果是同一个视频资源 则不需要重新创建mediaSource
        // 但需要onPlayerStateChanged 否则 不会触发onPlayerStateChanged()
        if (TextUtils.equals(pageListPlay.playUrl, mVideoUrl)) {
            onPlayerStateChanged(true, Player.STATE_READY);
        } else {
            MediaSource mediaSource = PageListPlayerManager.createMediaSource(mVideoUrl);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
            pageListPlay.playUrl = mVideoUrl;
        }
        controlView.show();
        controlView.setVisibilityListener(this);
        exoPlayer.addListener(this);
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isPlaying = false;
        bufferView.setVisibility(GONE);
        cover.setVisibility(VISIBLE);
        playerBtn.setVisibility(VISIBLE);
        playerBtn.setImageResource(R.drawable.icon_video_play);
    }

    @Override
    public void inActive() {
        // 暂停视频的播放并且让封面图和开始播放按钮显示出来
        PageListPlay pageListPlay = PageListPlayerManager.get(mCategory);
        if (pageListPlay.exoPlayer == null || pageListPlay.controlView == null || pageListPlay.exoPlayer == null) {
            return;
        }
        pageListPlay.exoPlayer.setPlayWhenReady(false);
        pageListPlay.controlView.setVisibilityListener(null);
        pageListPlay.exoPlayer.removeListener(this);
        cover.setVisibility(VISIBLE);
        playerBtn.setVisibility(VISIBLE);
        playerBtn.setImageResource(R.drawable.icon_video_play);
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }


    @Override
    public void onVisibilityChange(int visibility) {
        playerBtn.setVisibility(visibility);
        playerBtn.setImageResource(isPlaying() ? R.drawable.icon_video_pause : R.drawable.icon_video_play);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        // 监听视频播放的状态
        PageListPlay pageListPlay = PageListPlayerManager.get(mCategory);
        SimpleExoPlayer exoPlayer = pageListPlay.exoPlayer;
        if (playbackState == Player.STATE_READY && exoPlayer.getBufferedPosition() != 0 && playWhenReady) {
            cover.setVisibility(GONE);
            bufferView.setVisibility(GONE);
        } else if (playbackState == Player.STATE_BUFFERING) {
            bufferView.setVisibility(VISIBLE);
        }
        isPlaying = playbackState == Player.STATE_READY && exoPlayer.getBufferedPosition() != 0 && playWhenReady;
        playerBtn.setImageResource(isPlaying ? R.drawable.icon_video_pause : R.drawable.icon_video_play);
    }

    public View getPlayController() {
        PageListPlay listPlay = PageListPlayerManager.get(mCategory);
        return listPlay.controlView;
    }


}


