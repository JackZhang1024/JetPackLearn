package com.luckyboy.ppd.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.luckyboy.jetpacklearn.R;
import com.luckyboy.jetpacklearn.databinding.LayoutFeedTypeImageBinding;
import com.luckyboy.jetpacklearn.databinding.LayoutFeedTypeVideoBinding;
import com.luckyboy.libcommon.extension.AbsPagedListAdapter;
import com.luckyboy.libcommon.extension.LiveDataBus;
import com.luckyboy.ppd.core.model.Feed;
import com.luckyboy.ppd.core.ui.InteractionPresenter;
import com.luckyboy.ppd.core.view.ListPlayerView;
import com.luckyboy.ppd.detail.FeedDetailActivity;

public class FeedAdapter extends AbsPagedListAdapter<Feed, FeedAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    protected Context mContext;
    protected String mCategory;

    public FeedAdapter(Context context, String category) {
        super(new DiffUtil.ItemCallback<Feed>() {
            @Override
            public boolean areItemsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.equals(newItem);
            }
        });
        inflater = LayoutInflater.from(context);
        mContext = context;
        mCategory = category;
    }

    @Override
    protected int getItemViewType2(int position) {
        Feed feed = getItem(position);
        if (feed.itemType == Feed.TYPE_IMAGE_TEXT) {
            return R.layout.layout_feed_type_image;
        } else if (feed.itemType == Feed.TYPE_VIDEO) {
            return R.layout.layout_feed_type_video;
        }
        return 0;
    }

    @Override
    protected ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, viewType, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position) {
        final Feed feed = getItem(position);
        holder.bindData(feed);
        holder.itemView.setOnClickListener((view) -> {
            // 跳转到详情页面
            FeedDetailActivity.startFeedDetailActivity(mContext, feed, mCategory);
            onStartFeedDetailActivity(feed);
            if (mFeedObserver == null) {
                mFeedObserver = new FeedObserver();
                LiveDataBus.get().with(InteractionPresenter.DATA_FROM_INTERACTION)
                        .observe((LifecycleOwner) mContext, mFeedObserver);
            }
            mFeedObserver.setFeed(feed);
        });
    }

    public void onStartFeedDetailActivity(Feed feed) {

    }

    private FeedObserver mFeedObserver;

    private class FeedObserver implements Observer<Feed> {
        private static final String TAG = "FeedObserver";
        private Feed mFeed;

        @Override
        public void onChanged(Feed newOne) {
            if (mFeed.id != newOne.id) {
                return;
            }
            Log.e(TAG, "onChanged: old "+mFeed.ugc.hasLiked+" new "+newOne.ugc.hasLiked);
            mFeed.author = newOne.author;
            mFeed.ugc = newOne.ugc;
            mFeed.notifyChange();
        }

        public void setFeed(Feed feed) {
            mFeed = feed;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewDataBinding mBinding;
        public ListPlayerView listPlayerView;
        public ImageView feedImage;

        public ViewHolder(@NonNull View itemView, ViewDataBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        void bindData(Feed item) {
            // 这里之所以手动绑定数据的原因是 图片和视频区域都是需要计算的
            // 而dataBinding的执行默认是延迟一帧的
            // 当列表上下滑动的时候 会明显的看到宽高尺寸比对称的问题
            mBinding.setVariable(com.luckyboy.jetpacklearn.BR.feed, item);
            mBinding.setVariable(com.luckyboy.jetpacklearn.BR.lifeCycleOwner, mContext);
            if (mBinding instanceof LayoutFeedTypeImageBinding) {
                LayoutFeedTypeImageBinding imageBinding = (LayoutFeedTypeImageBinding) mBinding;
                feedImage = imageBinding.feedImage;
                imageBinding.feedImage.bindData(item.width, item.height, 16, item.cover);
            } else if (mBinding instanceof LayoutFeedTypeVideoBinding) {
                LayoutFeedTypeVideoBinding videoBinding = (LayoutFeedTypeVideoBinding) mBinding;
                videoBinding.listPlayerView.bindData(mCategory, item.width, item.height, item.cover, item.url);
                listPlayerView = videoBinding.listPlayerView;
            }
        }

        public boolean isVideoItem() {
            return mBinding instanceof LayoutFeedTypeVideoBinding;
        }

        public ListPlayerView getListPlayerView() {
            return listPlayerView;
        }

    }
}
