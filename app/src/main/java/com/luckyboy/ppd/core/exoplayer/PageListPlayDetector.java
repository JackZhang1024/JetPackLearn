package com.luckyboy.ppd.core.exoplayer;

import android.util.Log;
import android.util.Pair;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表视频自动播放 检测逻辑
 */
public class PageListPlayDetector {

    private static final String TAG = "PageListPlayDetector";

    // 收集一个个的能够进行视频播放的 对象 面向接口
    private List<IPlayTarget> mTargets = new ArrayList<>();
    private RecyclerView mRecycleView;
    // 正在播放的那个
    private IPlayTarget playingTarget;

    public void addTarget(IPlayTarget target) {
        mTargets.add(target);
    }

    public void removeTarget(IPlayTarget target) {
        mTargets.remove(target);
    }

    public PageListPlayDetector(LifecycleOwner owner, RecyclerView recyclerView) {
        mRecycleView = recyclerView;
        owner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    // 根据宿主的生命周期结束 清空数据
                    playingTarget = null;
                    mTargets.clear();
                    mRecycleView.removeCallbacks(delayAutoPlay);
                    recyclerView.removeOnScrollListener(scrollListener);
                    owner.getLifecycle().removeObserver(this);
                }
            }
        });
        recyclerView.getAdapter().registerAdapterDataObserver(mDataObserver);
        recyclerView.addOnScrollListener(scrollListener);
    }

    private final RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            Log.e(TAG, "onItemRangeInserted: ");
            postAutoPlay();
        }
    };


    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                // 列表停止滑动的时候 自动播放
                autoPlay();
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dx == 0 && dy == 0) {
                // 时序问题 当执行了AdapterDataObserver#onItemRangeInserted 可能还没有被布局到RecycleView上
                // 所以此时 recycleView.getChildCount()还是等于0的
                // 等childView 被布局到RecycleView上之后 会执行onScrolled()方法
                // 并且此时 dx dy 都等于 0
                postAutoPlay();
            } else {
                // 如果有正在播放的 且滑动时被划出了屏幕 则 停止掉它
                if (playingTarget != null && playingTarget.isPlaying() && !isTargetInBounds(playingTarget)) {
                    playingTarget.inActive();
                }
            }
        }
    };

    /**
     * 检测 IPlayTarget 所在的ViewVGroup 是否至少还有一半的大小在屏幕内
     */
    private boolean isTargetInBounds(IPlayTarget target) {
        ViewGroup owner = target.getOwner();
        ensureRecyclerViewLocation();
        if (!owner.isShown() || !owner.isAttachedToWindow()) {
            return false;
        }
        int[] location = new int[2];
        owner.getLocationOnScreen(location);

        int center = location[1] + owner.getHeight() / 2;
        // 承载视频播放画面的ViewGroup 它需要至少一半的大小 在RecycleView上下范围内
        return center >= rvLocation.first && center <= rvLocation.second;
    }

    private Pair<Integer, Integer> rvLocation = null;

    private Pair<Integer, Integer> ensureRecyclerViewLocation() {
        if (rvLocation == null) {
            int[] location = new int[2];
            mRecycleView.getLocationOnScreen(location);

            int top = location[1];
            int bottom = top + mRecycleView.getHeight();

            rvLocation = new Pair(top, bottom);
        }
        return rvLocation;
    }

    private void postAutoPlay() {
        mRecycleView.post(delayAutoPlay);
    }

    private Runnable delayAutoPlay = new Runnable() {
        @Override
        public void run() {
            autoPlay();
        }
    };

    private void autoPlay() {
        if (mTargets.size() <= 0 || mRecycleView.getChildCount() <= 0) {
            return;
        }
        if (playingTarget != null && playingTarget.isPlaying() && isTargetInBounds(playingTarget)) {
            return;
        }
        IPlayTarget activeTarget = null;
        for (IPlayTarget target : mTargets) {
            boolean inBounds = isTargetInBounds(target);
            if (inBounds) {
                activeTarget = target;
                break;
            }
        }
        if (activeTarget != null) {
            if (playingTarget != null) {
                // 暂停掉正在播放的
                playingTarget.inActive();
            }
            playingTarget = activeTarget;
            activeTarget.onActive();
        }
    }

    public void onPause() {
        if (playingTarget != null) {
            playingTarget.inActive();
        }
    }


    public void onResume() {
        if (playingTarget != null) {
            playingTarget.onActive();
        }
    }


}


