package com.luckyboy.ppd.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.luckyboy.jetpacklearn.R;
import com.luckyboy.jetpacklearn.databinding.FragmentFreshBinding;
import com.luckyboy.jetpacklearn.databinding.FragmentMineBinding;
import com.luckyboy.libcommon.utils.StatusBar;
import com.luckyboy.libnavannotation.FragmentDestination;
import com.luckyboy.ppd.core.model.User;
import com.luckyboy.ppd.login.UserManager;

@FragmentDestination(pageUrl = "main/tabs/my", asStarter = false, needLogin = true)
public class MineFragment extends Fragment {

    private FragmentMineBinding mineBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mineBinding = FragmentMineBinding.inflate(inflater, container, false);
        return mineBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        User user = UserManager.get().getUser();
        mineBinding.setUser(user);

        UserManager.get().refresh().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    mineBinding.setUser(user);
                }
            }
        });
        mineBinding.actionLogout.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setMessage(getString(R.string.fragment_my_logout))
                .setPositiveButton(getString(R.string.fragment_my_logout_ok), (dialog, which) -> {
                    dialog.dismiss();
                    UserManager.get().logout();
                    getActivity().onBackPressed();
                }).setNegativeButton(getString(R.string.fragment_my_logout_cancel), null)
                .create().show());

        mineBinding.goDetail.setOnClickListener(v -> ProfileActivity.startProfileActivity(getContext(), ProfileActivity.TAB_TYPE_ALL));
        mineBinding.userFeed.setOnClickListener(v -> ProfileActivity.startProfileActivity(getContext(), ProfileActivity.TAB_TYPE_FEED));
        mineBinding.userComment.setOnClickListener(v -> ProfileActivity.startProfileActivity(getContext(), ProfileActivity.TAB_TYPE_COMMENT));
        mineBinding.userFavorite.setOnClickListener(v -> UserBehaviorListActivity.startBehaviorListActivity(getContext(), UserBehaviorListActivity.BEHAVIOR_FAVORITE));
        mineBinding.userHistory.setOnClickListener(v -> UserBehaviorListActivity.startBehaviorListActivity(getContext(), UserBehaviorListActivity.BEHAVIOR_HISTORY));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBar.lightStatusBar(getActivity(), false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        StatusBar.lightStatusBar(getActivity(), hidden);
    }


}
