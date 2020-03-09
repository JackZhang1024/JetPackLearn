package com.luckyboy.ppd.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.luckyboy.jetpacklearn.databinding.FragmentFreshBinding;
import com.luckyboy.libnavannotation.FragmentDestination;
import com.luckyboy.ppd.core.MutablePageKeyedDataSource;
import com.luckyboy.ppd.core.exoplayer.PageListPlayDetector;
import com.luckyboy.ppd.core.exoplayer.PageListPlayerManager;
import com.luckyboy.ppd.core.model.Feed;
import com.luckyboy.ppd.core.ui.AbsListFragment;

import java.util.List;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends AbsListFragment<Feed, HomeViewModel> {

    private static final String TAG = "HomeFragment";

    private FragmentFreshBinding binding;
    private PageListPlayDetector playDetector;
    private String feedType;
    private boolean shouldPause = true;

    public static HomeFragment newInstance(String feedType) {
        Bundle args = new Bundle();
        args.putString("feedType", feedType);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getCacheLiveData().observe(this, new Observer<PagedList<Feed>>() {
            @Override
            public void onChanged(PagedList<Feed> feeds) {
                submitList(feeds);
            }
        });
        playDetector = new PageListPlayDetector(this, mRecycleView);
        mViewModel.setFeedType(feedType);
    }


    @Override
    public void onTwinkRefresh(TwinklingRefreshLayout layout) {
        // invalidate 之后 paging会重新创建一个DataSource 重新调用他的loadInitial方法加载
        // 初始化数据 详情见： LivePagedListBuilder#compute方法
        // 意思是一切重新开始
        Log.e(TAG, "onTwinkRefresh: ");
        mViewModel.getDataSource().invalidate();
    }

    @Override
    public void onTwinkLoadMore(TwinklingRefreshLayout layout) {
        final PagedList<Feed> currentList = adapter.getCurrentList();
        if (currentList == null || currentList.size() <= 0) {
            finishRefresh(false);
            return;
        }
        Feed feed = currentList.get(adapter.getItemCount() - 1);
        mViewModel.loadAfter(feed.id, new ItemKeyedDataSource.LoadCallback<Feed>() {
            @Override
            public void onResult(@NonNull List<Feed> data) {
                PagedList.Config config = currentList.getConfig();
                if (data != null && data.size() > 0) {
                    // 这里 咋们手动接管 分页数据加载的时候 使用MutableItemKeyedDataSource 也是可以的
                    // 由于当且仅当 paging 不再帮我们分页的时候 我们才会接管 所以就不要ViewModel中
                    // 创建的DataSource 继续工作了 使用使用 MutablePageKeyedDtaSource也是可以的
                    MutablePageKeyedDataSource dataSource = new MutablePageKeyedDataSource();

                    // 这里把列表上已经显示的先添加到dataSource.data中
                    // 而后把本次分页回来的数据在添加到dataSource.dta中
                    dataSource.data.addAll(currentList);
                    dataSource.data.addAll(data);
                    PagedList pagedList = dataSource.buildNewPagedList(config);
                    submitList(pagedList);
                }
            }
        });
    }


    @Override
    protected PagedListAdapter getAdapter() {
        feedType = getArguments() == null ? "all" : getArguments().getString("feedType");
        return new FeedAdapter(getContext(), feedType) {

            @Override
            public void onViewAttachedToWindow2(ViewHolder holder) {
                if (holder.isVideoItem()) {
                    playDetector.addTarget(holder.getListPlayerView());
                }
            }

            @Override
            public void onViewDetachedFromWindow2(ViewHolder holder) {
                if (holder.isVideoItem()) {
                    playDetector.removeTarget(holder.getListPlayerView());
                }
            }

            @Override
            public void onStartFeedDetailActivity(Feed feed) {
                boolean isVideo = feed.itemType == Feed.TYPE_VIDEO;
                shouldPause = !isVideo;
            }

            @Override
            public void onCurrentListChanged(@Nullable PagedList<Feed> previousList, @Nullable PagedList<Feed> currentList) {
                // 这个方法是我们每提交一次 pageList对象到 adapter 就会触发一次
                // 每调用一次 adapter.submitList
                Log.e(TAG, "onCurrentListChanged: ");
                if (previousList != null && currentList != null) {
                    if (!currentList.containsAll(previousList)) {
                        mRecycleView.scrollToPosition(0);
                    }
                }
            }

        };
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, "onHiddenChanged: " + hidden);
        if (hidden) {
            playDetector.onPause();
        } else {
            playDetector.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 如果是跳转到详情页 咋们就不需要 暂停视频播放了
        // 如果前后台切换 或者去别的页面了 都是需要暂停视频播放的
        if (shouldPause) {
            playDetector.onPause();
        }
        Log.e(TAG, "onPause: feedType " + feedType);
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        shouldPause = true;
        // 由于沙发Tab的几个子页面 复用了HomeFragment
        // 我们需要判断下 当前页面 它是否有ParentFragment
        // 当且仅当 它和它的parentFragment均可见的时候 才能恢复视频播放
        if (getParentFragment() != null) {
            if (getParentFragment().isVisible() && isVisible()) {
                Log.e(TAG, "onResume: feedType1 " + feedType);
                playDetector.onResume();
            }
        } else {
            if (isVisible()) {
                Log.e(TAG, "onResume: feedType2 " + feedType);
                playDetector.onResume();
            }
        }
    }

    @Override
    public void onDestroy() {
        // 记得销毁
        PageListPlayerManager.release(feedType);
        super.onDestroy();
    }

}






