package com.luckyboy.ppd.detail;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.luckyboy.jetpacklearn.databinding.LayoutFeedCommentListItemBinding;
import com.luckyboy.libcommon.extension.AbsPagedListAdapter;
import com.luckyboy.libcommon.utils.PixUtils;
import com.luckyboy.libcommon.utils.ToastManager;
import com.luckyboy.ppd.core.MutableItemKeyDataSource;
import com.luckyboy.ppd.core.model.Comment;
import com.luckyboy.ppd.core.ui.InteractionPresenter;
import com.luckyboy.ppd.login.UserManager;
import com.luckyboy.ppd.publish.PreviewActivity;

public class FeedCommentAdapter extends AbsPagedListAdapter<Comment, FeedCommentAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;

    public FeedCommentAdapter(Context context) {
        super(new DiffUtil.ItemCallback<Comment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
                return oldItem.equals(newItem);
            }
        });
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    protected FeedCommentAdapter.ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
        LayoutFeedCommentListItemBinding binding = LayoutFeedCommentListItemBinding.inflate(mInflater, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    protected void onBindViewHolder2(FeedCommentAdapter.ViewHolder holder, int position) {
        Comment item = getItem(position);
        holder.bindData(item);
        holder.mBinding.commentDelete.setOnClickListener(v -> {
            InteractionPresenter.deleteFeedComment(mContext, item.itemId, item.commentId)
                    .observe((LifecycleOwner) mContext, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean isSuccess) {
                            if (isSuccess) {
                                deleteAndRefresList(item);
                            }
                        }
                    });

        });
        holder.mBinding.commentCover.setOnClickListener(v -> {
            boolean isVideo = item.commentType == Comment.COMMENT_TYPE_VIDEO;
            PreviewActivity.startActivityForResult((Activity) mContext, isVideo ? item.videoUrl : item.imageUrl, isVideo, null);
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ViewHolder";

        private LayoutFeedCommentListItemBinding mBinding;

        public ViewHolder(@NonNull View itemView, LayoutFeedCommentListItemBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        public void bindData(Comment item) {
            Log.e(TAG, "bindData: " + item.author);
            mBinding.setComment(item);
            boolean self = item.author == null ? false : UserManager.get().getUser() == null ? false :
                    UserManager.get().getUser().userId == item.author.userId;
            mBinding.labelAuthor.setVisibility(self ? View.VISIBLE : View.GONE);
            mBinding.commentDelete.setVisibility(self ? View.VISIBLE : View.GONE);
            if (!TextUtils.isEmpty(item.imageUrl)) {
                mBinding.commentExt.setVisibility(View.VISIBLE);
                mBinding.commentCover.setVisibility(View.VISIBLE);
                mBinding.commentCover.bindData(item.width, item.height, 0, PixUtils.dp2px(200), PixUtils.dp2px(200), item.imageUrl);
                if (!TextUtils.isEmpty(item.videoUrl)) {
                    mBinding.videoIcon.setVisibility(View.VISIBLE);
                } else {
                    mBinding.videoIcon.setVisibility(View.GONE);
                }
            } else {
                mBinding.commentCover.setVisibility(View.GONE);
                mBinding.videoIcon.setVisibility(View.GONE);
                mBinding.commentExt.setVisibility(View.GONE);
            }
        }
    }


    // 添加并刷新列表
    public void addAndRefreshList(Comment comment) {
        PagedList<Comment> currentList = getCurrentList();
        MutableItemKeyDataSource<Integer, Comment> mutableItemKeyDataSource
                = new MutableItemKeyDataSource<Integer, Comment>((ItemKeyedDataSource) currentList.getDataSource()) {
            @NonNull
            @Override
            public Integer getKey(@NonNull Comment item) {
                return item.id;
            }
        };
        // 将新的评论放到第一条的位置
        mutableItemKeyDataSource.data.add(comment);
        mutableItemKeyDataSource.data.addAll(currentList);
        PagedList<Comment> pagedList = mutableItemKeyDataSource.buildNewPagedList(currentList.getConfig());
        submitList(pagedList);
    }


    // 删除并刷新数据
    public void deleteAndRefresList(Comment item) {
        MutableItemKeyDataSource<Integer, Comment> dataSource = new MutableItemKeyDataSource<Integer, Comment>((ItemKeyedDataSource) getCurrentList().getDataSource()) {
            @NonNull
            @Override
            public Integer getKey(@NonNull Comment item) {
                return item.id;
            }
        };
        PagedList<Comment> currentList = getCurrentList();
        // 过滤掉不会将要删除的数据
        for (Comment comment : currentList) {
            if (comment != item) {
                dataSource.data.add(comment);
            }
        }
        PagedList<Comment> pagedList = dataSource.buildNewPagedList(getCurrentList().getConfig());
        submitList(pagedList);
    }

}
