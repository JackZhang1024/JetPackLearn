package com.luckyboy.libcommon.extension;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


/**
 * 一个能够添加 HeaderView 和 FooterView的pagedListAdapter
 * 解决了添加 HeaderView 和 FooterView时 RecycleView定位不准的问题
 */
public abstract class AbsPagedListAdapter<T, VH extends RecyclerView.ViewHolder> extends PagedListAdapter<T, VH> {

    private SparseArray<View> mHeaders = new SparseArray<>();
    private SparseArray<View> mFooters = new SparseArray<>();

    public int BASE_ITEM_TYPE_HEADER = 100000;
    public int BASE_ITEM_TYPE_FOOTER = 200000;

    public AbsPagedListAdapter(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }

    public void addHeaderView(View view) {
        // 判断给View对象是否还没有在mHeaders数组里面
        if (mHeaders.indexOfValue(view) < 0) {
            mHeaders.put(BASE_ITEM_TYPE_HEADER++, view);
            notifyDataSetChanged();
        }
    }


    public void addFooterView(View view) {
        if (mFooters.indexOfValue(view) < 0) {
            mFooters.put(BASE_ITEM_TYPE_FOOTER++, view);
            notifyDataSetChanged();
        }
    }

    // 移除头部
    public void removeHeaderView(View view) {
        int index = mHeaders.indexOfValue(view);
        if (index < 0) return;
        mHeaders.removeAt(index);
        notifyDataSetChanged();
    }


    // 移除底部
    public void removeFooterView(View view) {
        int index = mFooters.indexOfValue(view);
        if (index < 0) return;
        mFooters.removeAt(index);
        notifyDataSetChanged();
    }


    public int getHeaderCount() {
        return mHeaders.size();
    }


    public int getFooterCount() {
        return mFooters.size();
    }

    @Override
    public int getItemCount() {
        int itemCount = super.getItemCount();
        return itemCount + mHeaders.size() + mFooters.size();
    }

    public int getOriginalItemCount() {
        return getItemCount() - mHeaders.size() - mFooters.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            // 返回该position对应的headerView 的viewType
            return mHeaders.keyAt(position);
        }
        if (isFooterPosition(position)) {
            // Footer类型的 需要计算下它的position实际大小
            position = position - getOriginalItemCount() - mHeaders.size();
            return mFooters.keyAt(position);
        }
        position = position - mHeaders.size();
        return getItemViewType2(position);
    }


    protected int getItemViewType2(int position) {
        return 0;
    }

    private boolean isFooterPosition(int position) {
        return position >= getOriginalItemCount() + mHeaders.size();
    }

    private boolean isHeaderPosition(int position) {
        return position < mHeaders.size();
    }


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mHeaders.indexOfKey(viewType) >= 0) {
            View view = mHeaders.get(viewType);
            return (VH) new RecyclerView.ViewHolder(view) {
            };
        }
        if (mFooters.indexOfKey(viewType) >= 0) {
            View view = mFooters.get(viewType);
            return (VH) new RecyclerView.ViewHolder(view) {
            };
        }
        return onCreateViewHolder2(parent, viewType);
    }

    protected abstract VH onCreateViewHolder2(ViewGroup group, int viewType);

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (isHeaderPosition(position) || isFooterPosition(position)) {
            return;
        }
        // 列表中正常类型的ItemView的position正好是 减去headerview个数
        position = position - mHeaders.size();
        onBindViewHolder2(holder, position);
    }

    protected abstract void onBindViewHolder2(VH holder, int position);


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (!isHeaderPosition(holder.getAdapterPosition()) && !isFooterPosition(holder.getAdapterPosition())) {
            onViewAttachedToWindow2((VH) holder);
        }
    }

    public void onViewAttachedToWindow2(VH holder) {

    }


    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (!isHeaderPosition(holder.getAdapterPosition()) && !isFooterPosition(holder.getAdapterPosition())) {
            onViewDetachedFromWindow2((VH) holder);
        }
    }

    public void onViewDetachedFromWindow2(VH holder) {

    }

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }


    // 如果我们先添加headerView 然后网络数据回来了再更新到列表上
    // 由于Paging在计算列表上Item的位置时 并不会顾及我们有没有添加 headerView  就会出现列表定位问题
    // 实际上 recycleView#setAdapter方法 它会给Adapter注册一个AdapterDataObserver
    // 我们可以代理registerAdapterDataObserver()传递进来的observer 在各个方法实现中，把headerView 的个数计算上，再中转出去即可
    private class AdapterDataObserverProxy extends RecyclerView.AdapterDataObserver {
        private RecyclerView.AdapterDataObserver mObserver;

        public AdapterDataObserverProxy(RecyclerView.AdapterDataObserver observer) {
            mObserver = observer;
        }

        @Override
        public void onChanged() {
            mObserver.onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mObserver.onItemRangeChanged(positionStart + mHeaders.size(), itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            mObserver.onItemRangeChanged(positionStart + mHeaders.size(), itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mObserver.onItemRangeInserted(positionStart + mHeaders.size(), itemCount);
        }


        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mObserver.onItemRangeMoved(fromPosition + mHeaders.size(), toPosition + mHeaders.size(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mObserver.onItemRangeRemoved(positionStart + mHeaders.size(), itemCount);
        }

    }

}
