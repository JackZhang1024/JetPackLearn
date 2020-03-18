package com.luckyboy.ppd.mine;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import com.luckyboy.jetpacklearn.R;
import com.luckyboy.ppd.core.MutableItemKeyDataSource;
import com.luckyboy.ppd.core.model.Feed;
import com.luckyboy.ppd.core.ui.InteractionPresenter;
import com.luckyboy.ppd.core.util.TimeUtils;
import com.luckyboy.ppd.home.FeedAdapter;
import com.luckyboy.ppd.login.UserManager;

public class ProfileListAdapter extends FeedAdapter {

    public ProfileListAdapter(Context context, String category){
        super(context, category);
    }

    @Override
    protected int getItemViewType2(int position) {
        if (TextUtils.equals(mCategory, ProfileActivity.TAB_TYPE_COMMENT)){
            return R.layout.layout_feed_type_comment;
        } else if (TextUtils.equals(mCategory, ProfileActivity.TAB_TYPE_ALL)){
            Feed feed = getItem(position);
            if (feed.topComment!=null && feed.topComment.userId == UserManager.get().getUserId()){
                return R.layout.layout_feed_type_comment;
            }
        }
        return super.getItemViewType2(position);
    }


    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position) {
        super.onBindViewHolder2(holder, position);
        super.onBindViewHolder2(holder, position);
        View deleteView = holder.itemView.findViewById(R.id.feed_delete);
        TextView createTime = holder.itemView.findViewById(R.id.create_time);

        Feed feed = getItem(position);
        createTime.setVisibility(View.VISIBLE);
        createTime.setText(TimeUtils.calculate(feed.createTime));

        boolean isCommentTab = TextUtils.equals(mCategory, ProfileActivity.TAB_TYPE_COMMENT);
        deleteView.setVisibility(View.VISIBLE);
        deleteView.setOnClickListener(v -> {
            //如果是个人主页的评论tab，删除的时候，实际上是删除帖子的评论。
            if (isCommentTab) {
                InteractionPresenter.deleteFeedComment(mContext, feed.itemId, feed.topComment.commentId)
                        .observe((LifecycleOwner) mContext, success -> {
                            refreshList(feed);
                        });
            } else {
                InteractionPresenter.deleteFeed(mContext, feed.itemId)
                        .observe((LifecycleOwner) mContext, success -> {
                            refreshList(feed);
                        });
            }
        });
    }


    private void refreshList(Feed delete){
        PagedList<Feed> currentList = getCurrentList();
        MutableItemKeyDataSource<Integer, Feed> dataSource
                = new MutableItemKeyDataSource<Integer, Feed>((ItemKeyedDataSource) currentList.getDataSource()) {
            @NonNull
            @Override
            public Integer getKey(@NonNull Feed item) {
                return item.id;
            }
        };
        // for 循环遍历一遍 过滤掉被删除的这个帖子
        for (Feed feed: currentList){
             if (feed!=delete){
                  dataSource.data.add(feed);
             }
        }
        PagedList<Feed> pagedList = dataSource.buildNewPagedList(currentList.getConfig());
        submitList(pagedList);
    }

}
