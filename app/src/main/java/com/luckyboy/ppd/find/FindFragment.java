package com.luckyboy.ppd.find;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.luckyboy.jetpacklearn.databinding.FragmentFreshBinding;
import com.luckyboy.libnavannotation.FragmentDestination;
import com.luckyboy.ppd.core.model.SofaTab;
import com.luckyboy.ppd.core.util.AppConfig;
import com.luckyboy.ppd.sofa.SofaFragment;

@FragmentDestination(pageUrl = "main/tabs/find", asStarter = false)
public class FindFragment extends SofaFragment {


    @Override
    protected Fragment getTabFragment(int position) {
        SofaTab.Tabs tab = getTabConfig().tabs.get(position);
        TagListFragment fragment = TagListFragment.newInstance(tab.tag);
        return fragment;
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        String tagType = childFragment.getArguments().getString(TagListFragment.KEY_TAG_TYPE);
        if (TextUtils.equals(tagType, "onlyFollow")) {
            ViewModelProviders.of(childFragment).get(TagListViewModel.class)
                    .getSwitchTabLiveData().observe(this, new Observer() {
                @Override
                public void onChanged(Object o) {
                    viewPager2.setCurrentItem(1);
                }
            });
        }
    }

    @Override
    protected SofaTab getTabConfig() {
        return AppConfig.getFindTabConfig();
    }
}
