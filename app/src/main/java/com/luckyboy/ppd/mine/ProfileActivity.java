package com.luckyboy.ppd.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.luckyboy.jetpacklearn.R;
import com.luckyboy.jetpacklearn.databinding.ActivityLayoutProfileBinding;
import com.luckyboy.libcommon.utils.StatusBar;
import com.luckyboy.ppd.core.model.User;
import com.luckyboy.ppd.login.UserManager;

public class ProfileActivity extends AppCompatActivity {
    private ActivityLayoutProfileBinding mBinding;

    public static final String TAB_TYPE_ALL = "tab_all";
    public static final String TAB_TYPE_FEED = "tab_feed";
    public static final String TAB_TYPE_COMMENT = "tab_comment";

    public static final String KEY_TAB_TYPE = "key_tab_type";

    public static void startProfileActivity(Context context, String tabType) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(KEY_TAB_TYPE, tabType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBar.fitSystemBar(this);
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_layout_profile);

        User user = UserManager.get().getUser();
        mBinding.setUser(user);
        mBinding.actionBack.setOnClickListener(v -> finish());

        String[] tabs = getResources().getStringArray(R.array.profile_tabs);
        ViewPager2 viewPager = mBinding.viewPager;
        TabLayout tabLayout = mBinding.tabLayout;
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return ProfileListFragment.newInstance(getTabFragment(position));
            }

            private String getTabFragment(int position){
                switch (position){
                    case 0:
                        return TAB_TYPE_ALL;
                    case 1:
                        return TAB_TYPE_FEED;
                    case 2:
                        return TAB_TYPE_COMMENT;
                }
                return TAB_TYPE_ALL;
            }

            @Override
            public int getItemCount() {
                return tabs.length;
            }
        });

        // autoRefresh 当我们调用 viewPager的adapter.notifyChanged
        // 方法的时候  要不要主动的把tabLayout的选项卡给移除掉重新配置
        // 要在给viewPager设置adapter之后调用
        new TabLayoutMediator(tabLayout, viewPager, false, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabs[position]);
            }
        }).attach();

        int initTabPosition = getInitTabPosition();
        if (initTabPosition != 0) {
            viewPager.post(() -> {
                viewPager.setCurrentItem(initTabPosition, false);
            });
        }

        mBinding.appbar.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                boolean expand = Math.abs(verticalOffset) <
                        appBarLayout.getTotalScrollRange();
                mBinding.setExpand(expand);
            }
        });

    }


    private int getInitTabPosition(){
        String initTab = getIntent().getStringExtra(KEY_TAB_TYPE);
        switch (initTab){
            case TAB_TYPE_ALL:
                return 0;
            case TAB_TYPE_FEED:
                return 1;
            case TAB_TYPE_COMMENT:
                return 2;
            default:return 0;
        }
    }

}
