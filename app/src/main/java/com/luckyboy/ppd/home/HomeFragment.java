package com.luckyboy.ppd.home;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.luckyboy.jetpacklearn.databinding.FragmentFreshBinding;
import com.luckyboy.libnavannotation.FragmentDestination;

import java.util.ArrayList;
import java.util.List;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private FragmentFreshBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFreshBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.twkRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                new Handler().postDelayed(() -> {
                    binding.twkRefreshLayout.finishRefreshing();
                }, 2000);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                new Handler().postDelayed(() -> {
                    binding.twkRefreshLayout.finishLoadmore();
                }, 2000);
            }
        });
        DataAdapter dataAdapter = new DataAdapter(loadData());
        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rv.setAdapter(dataAdapter);
    }


    private List<String> loadData() {
        List<String> data = new ArrayList<>();
        for (int index = 0; index < 10; index++) {
            data.add("index " + index);
        }
        return data;
    }

    class DataAdapter extends RecyclerView.Adapter {

        public List<String> list = new ArrayList<>();

        public DataAdapter(List<String> data) {
            this.list = data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setTextSize(20);
            return new DataViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            DataViewHolder viewHolder = (DataViewHolder) holder;
            viewHolder.setItemData(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


    }


    class DataViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }

        public void setItemData(String data) {
            textView.setText(data);
        }

    }

}

