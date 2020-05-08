package com.luckyboy.ppd.find;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.luckyboy.jetpacklearn.R;
import com.luckyboy.jetpacklearn.databinding.ActivityLayoutTagFeedListBinding;
import com.luckyboy.jetpacklearn.databinding.LayoutTagFeedListHeaderBinding;
import com.luckyboy.libcommon.extension.AbsPagedListAdapter;
import com.luckyboy.libcommon.utils.PixUtils;
import com.luckyboy.libcommon.utils.StatusBar;
import com.luckyboy.libcommon.view.EmptyView;
import com.luckyboy.ppd.core.exoplayer.PageListPlayDetector;
import com.luckyboy.ppd.core.exoplayer.PageListPlayerManager;
import com.luckyboy.ppd.core.model.Feed;
import com.luckyboy.ppd.core.model.TagList;
import com.luckyboy.ppd.core.util.RecyclerViewUtils;
import com.luckyboy.ppd.core.view.LoadMoreDecoration;
import com.luckyboy.ppd.core.view.PPDClassicHeader;
import com.luckyboy.ppd.core.view.PPDTwinkRefreshLayout;
import com.luckyboy.ppd.home.FeedAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

public class TagFeedListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TagFeedListActivity";

    public static final String KEY_TAG_LIST = "tag_list";
    public static final String KEY_FEED_TYPE = "tag_feed_list";
    private ActivityLayoutTagFeedListBinding binding;
    private SwipeRecyclerView recyclerView;
    private EmptyView emptyView;
    private SmartRefreshLayout twinklingRefreshLayout;
    private TagList tagList;
    private PageListPlayDetector playDetector;
    private boolean shouldPause = true;
    private AbsPagedListAdapter adapter;
    private int totalScrollY;
    private TagFeedListViewModel tagFeedListViewModel;
    protected LoadMoreDecoration decoration;

    public static void startActivity(Context context, TagList tagList) {
        Intent intent = new Intent(context, TagFeedListActivity.class);
        intent.putExtra(KEY_TAG_LIST, tagList);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBar.fitSystemBar(this);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,
                R.layout.activity_layout_tag_feed_list);
        recyclerView = binding.refreshLayout.rv;
        emptyView = binding.refreshLayout.emptyView;
        twinklingRefreshLayout = binding.refreshLayout.refreshLayout;
        binding.actionBack.setOnClickListener(this);
        RecyclerViewUtils viewUtils = new RecyclerViewUtils();
        viewUtils.initRecyclerView(this, recyclerView, new SwipeRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                // 加载更多数据操作
                Log.e(TAG, "onLoadMore: 加载更多");
                PagedList currentList = getAdapter().getCurrentList();
                finishRefresh(currentList != null && currentList.size() > 0, true);
                //全权委托给paging框架
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = (AbsPagedListAdapter) getAdapter();
        recyclerView.setAdapter(adapter);
        decoration = new LoadMoreDecoration(this, LinearLayoutManager.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.list_divider));
        recyclerView.addItemDecoration(decoration);
        recyclerView.setItemAnimator(null);

        twinklingRefreshLayout.setEnableRefresh(true);
        twinklingRefreshLayout.setEnableLoadMore(false);
        twinklingRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                tagFeedListViewModel.getDataSource().invalidate();
            }
        });
        twinklingRefreshLayout.setRefreshHeader(new PPDClassicHeader(this));
        //twinklingRefreshLayout.setOnRefreshListener(refreshListenerAdapter);

        tagList = (TagList) getIntent().getSerializableExtra(KEY_TAG_LIST);
        binding.setTagList(tagList);
        binding.setOwner(this);

        tagFeedListViewModel = ViewModelProviders.of(this).get(TagFeedListViewModel.class);
        tagFeedListViewModel.setFeedType(tagList.title);
        tagFeedListViewModel.getPageData().observe(this, feeds -> {
            Log.e(TAG, "onCreate: submitList " + feeds.size());
            submitList(feeds);
        });
        tagFeedListViewModel.getBoundaryPageData().observe(this, hasData -> finishRefresh(hasData, true));
        playDetector = new PageListPlayDetector(this, recyclerView);
        addHeaderView();
    }

    private void submitList(PagedList<Feed> feeds) {
        if (feeds.size() > 0) {
            adapter.submitList(feeds);
        }
        finishRefresh(feeds.size() > 0, true);
    }

    private RefreshListenerAdapter refreshListenerAdapter = new RefreshListenerAdapter() {
        @Override
        public void onRefresh(TwinklingRefreshLayout refreshLayout) {
            tagFeedListViewModel.getDataSource().invalidate();
        }

        @Override
        public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
            PagedList currentList = getAdapter().getCurrentList();
            finishRefresh(currentList != null && currentList.size() > 0, true);
            // 全权委托给paging框架来处理
        }
    };

    private void finishRefresh(boolean hasData, boolean loadMore) {
        PagedList currentList = adapter.getCurrentList();
        hasData = currentList != null && currentList.size() > 0 || hasData;
        if (twinklingRefreshLayout.getState() == RefreshState.Refreshing) {
            twinklingRefreshLayout.finishRefresh();
        } else if (loadMore) {
            // 处于加载更多状态
            recyclerView.loadMoreFinish(false, false);
        }
        if (hasData) {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void addHeaderView() {
        LayoutTagFeedListHeaderBinding headerBinding = LayoutTagFeedListHeaderBinding.inflate(LayoutInflater.from(this), recyclerView, false);
        headerBinding.setTagList(tagList);
        headerBinding.setOwner(this);
        adapter.addHeaderView(headerBinding.getRoot());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalScrollY += dy;
                boolean overHeight = totalScrollY > PixUtils.dp2px(48);
                binding.tagLogo.setVisibility(overHeight ? View.VISIBLE : View.GONE);
                binding.tagTitle.setVisibility(overHeight ? View.VISIBLE : View.GONE);
                binding.topBarFollow.setVisibility(overHeight ? View.VISIBLE : View.GONE);
                binding.actionBack.setImageResource(overHeight ? R.drawable.icon_back_black : R.drawable.icon_back_white);
                binding.topBar.setBackgroundColor(overHeight ? Color.WHITE : Color.TRANSPARENT);
                binding.topLine.setVisibility(overHeight ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }


    public PagedListAdapter getAdapter() {
        return new FeedAdapter(this, KEY_FEED_TYPE) {
            @Override
            public void onViewDetachedFromWindow2(ViewHolder holder) {
                playDetector.removeTarget(holder.getListPlayerView());
            }

            @Override
            public void onViewAttachedToWindow2(ViewHolder holder) {
                if (holder.isVideoItem()) {
                    playDetector.addTarget(holder.getListPlayerView());
                }
            }

            @Override
            public void onStartFeedDetailActivity(Feed feed) {
                boolean isVideo = feed.itemType == Feed.TYPE_VIDEO;
                shouldPause = !isVideo;
            }

            @Override
            public void onCurrentListChanged(@Nullable PagedList<Feed> previousList, @Nullable PagedList<Feed> currentList) {
                // 这个方法是在我们每提交一次 pageList 对象到adapter 就会触发贺词
                // 每调用一次 adapter.submitList
                if (previousList != null && currentList != null) {
                    if (!currentList.containsAll(previousList)) {
                        recyclerView.scrollToPosition(0);
                    }
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (shouldPause) {
            playDetector.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        shouldPause = true;
        playDetector.onResume();
    }

    @Override
    protected void onDestroy() {
        PageListPlayerManager.release(KEY_FEED_TYPE);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        finish();
    }

}
