package com.luckyboy.ppd.detail;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import com.luckyboy.jetpacklearn.R;
import com.luckyboy.libcommon.utils.PixUtils;
import com.luckyboy.ppd.core.view.FullScreenPlayerView;

public class ViewZoomBehavior extends CoordinatorLayout.Behavior<FullScreenPlayerView> {

    private OverScroller overScroller;
    private int minHeight;
    private int scrollingId;
    private ViewDragHelper viewDragHelper;
    private View scrollingView;
    private FullScreenPlayerView refChild;
    private int childOriginalHeight;
    private boolean canFullScreen;
    private FlingRunnable runnable;

    public ViewZoomBehavior() {

    }

    public ViewZoomBehavior(Context context, AttributeSet attributeSet) {
        TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.view_zoom_behavior, 0, 0);
        scrollingId = array.getResourceId(R.styleable.view_zoom_behavior_scrolling_id, 0);
        minHeight = array.getDimensionPixelOffset(R.styleable.view_zoom_behavior_mini_height, PixUtils.dp2px(200));
        array.recycle();

        overScroller = new OverScroller(context);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull FullScreenPlayerView child, int layoutDirection) {
        // 我们需要这在这里获取 scrollingView
        // 并全局保存下 child view
        // 并计算出初始时 child的底部值 也就是它的高度 我们后续拖拽滑动的时候  它就是最大高度的限制
        // 于此同时 还需要计算出 当前页面是否可以进行视频的全屏展示 即 h>w 即可
        if (viewDragHelper == null) {
            viewDragHelper = ViewDragHelper.create(parent, 1.0f, mCallback);
            this.scrollingView = parent.findViewById(scrollingId);
            this.refChild = child;
            this.childOriginalHeight = child.getMeasuredHeight();
            // 如果视频的原始高度大于屏幕的跨度 那么就可以全屏显示
            canFullScreen = childOriginalHeight > parent.getMeasuredWidth();
        }
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        // 告诉ViewDragHelper 什么时候 可以拦截 手指触摸的这个view的手势分发
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            if (canFullScreen && refChild.getBottom() >= minHeight && refChild.getBottom() <= childOriginalHeight) {
                return true;
            }
            return false;
        }

        // 告诉ViewDragHelper 在屏幕上滑动多少距离才算触摸
        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return viewDragHelper.getTouchSlop();
        }

        // 告诉ViewDragHelper 手指拖拽的这个View 本次滑动最终能够移动的距离
        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            if (child == null || dy == 0) {
                return 0;
            }
            // 都是结束位置减去开始位置的坐标差
            // dy>0 表示手指从屏幕上方 往屏幕下方滑动
            // dy<0 表示手指从屏幕下方 往屏幕上方滑动

            // 手指从下往上滑动（折叠操作）  dy<0 这意味着refChild的底部 会被向上移动 所以 它的底部的最小值 不能小于minHeight
            if (dy < 0 && refChild.getBottom() < minHeight
                    // 手指从上往下滑动(展开操作) dy>0 意味着refChild的底部 会被向下移动 所以它的底部的最大 不能超过父容器的高 也就是 childOriginalHeight
                    || dy > 0 && refChild.getBottom() > childOriginalHeight
                    // 手指 从屏幕上方往下滑动  如果 scrollingView 还没有滑动到列表的最顶部
                    // 也意味着列表还可以下滑动（向列表的第一条方向走）此时 咋们应该让列表自行滑动 不做拦截
                    || (dy > 0 && (scrollingView != null && scrollingView.canScrollVertically(-1)))  // (dy>0 && (scrollingView!=null && scrollingView.canScrollVertically(-1)))
            ) {
                // return 0 表示此次vertical方向上 滑动的距离为0 不动
                return 0;
            }
            int maxConsumed = 0;
            if (dy > 0) {
                // 如果本次滑动的 dy值 追加上 refChild的bottom 值会超过 父容器的最大高度值
                // 此时 咋们应该计算一下
                if (refChild.getBottom() + dy > childOriginalHeight) {
                    maxConsumed = childOriginalHeight - refChild.getBottom();
                } else {
                    maxConsumed = dy;
                }
            } else {
                // else 分支里面的 dy值是负值
                // 如果本次滑动的值 dy 加上 refChild的bottom 会小于minHeight 那么咋们就应该计算一下本次能够滑动的最大距离
                if (refChild.getBottom() + dy < minHeight) {
                    maxConsumed = minHeight - refChild.getBottom();
                } else {
                    maxConsumed = dy;
                }
            }
            ViewGroup.LayoutParams layoutParams = refChild.getLayoutParams();
            layoutParams.height = layoutParams.height + maxConsumed;
            refChild.setLayoutParams(layoutParams);
            if (mViewZoomCallback != null) {
                mViewZoomCallback.onDragZoom(layoutParams.height);
            }
            return maxConsumed;
        }

        // 指的是 我们的手指 从屏幕上 离开的时候 会被调用
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            //
            if (refChild.getBottom() > minHeight && refChild.getBottom() < childOriginalHeight && yvel != 0) {
                refChild.removeCallbacks(runnable);
                // 在手指离开屏幕的时候继续保持惯性滑动
                runnable = new FlingRunnable(refChild);
                runnable.fling((int) xvel, (int) yvel);
            }
        }
    };

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull FullScreenPlayerView child, @NonNull MotionEvent ev) {
        if (!canFullScreen && viewDragHelper == null) {
            return super.onTouchEvent(parent, child, ev);
        }
        viewDragHelper.processTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull FullScreenPlayerView child, @NonNull MotionEvent ev) {
        // 不是可以全屏状态 那么就不需要ViewDragHelper处理
        if (!canFullScreen || viewDragHelper == null) {
            return super.onInterceptTouchEvent(parent, child, ev);
        }
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    private class FlingRunnable implements Runnable {
        private View mFlingView;

        public FlingRunnable(View flingView) {
            mFlingView = flingView;
        }

        public void fling(int xVel, int yVel) {
            /**
             * startX:开始的X值 由于我们不需要在水平方向滑动 所以为0
             * startY:开始滑动时Y的起始值 那就是flingView的bottom值
             * xVel: 水平方向的速度 实际上为0
             * yVel: 垂直方向的速度 即松手时的速度
             * minX: 水平方向上 滚动回弹的越界最小值 给0即可
             * maxX: 水平方向上 滚动回弹越界的最大值 实际上给0也是一样的
             * minY: 垂直方向上 滚动回弹的越界最小值 给0即可
             * maxY：垂直方向上 滚动回弹的越界最大值 实际上给0 也一样
             * */
            overScroller.fling(0, mFlingView.getBottom(), xVel, yVel, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            run();
        }

        @Override
        public void run() {
            ViewGroup.LayoutParams params = mFlingView.getLayoutParams();
            int height = params.height;
            // 判读本次滑动是否可以滚动到最终点
            if (overScroller.computeScrollOffset() && height >= minHeight && height <= childOriginalHeight) {
                int newHeight = Math.min(overScroller.getCurrY(), childOriginalHeight);
                if (newHeight != height) {
                    params.height = newHeight;
                    mFlingView.setLayoutParams(params);
                    if (mViewZoomCallback != null) {
                        mViewZoomCallback.onDragZoom(newHeight);
                    }
                }
                ViewCompat.postOnAnimation(mFlingView, this);
            } else {
                mFlingView.removeCallbacks(this);
            }
        }
    }

    private ViewZoomCallback mViewZoomCallback;

    public void setViewZoomCallback(ViewZoomCallback callback) {
        this.mViewZoomCallback = callback;
    }

    public interface ViewZoomCallback {
        void onDragZoom(int height);
    }


}
