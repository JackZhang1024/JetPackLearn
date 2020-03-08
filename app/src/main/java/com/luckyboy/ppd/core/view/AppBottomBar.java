package com.luckyboy.ppd.core.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.luckyboy.jetpacklearn.R;
import com.luckyboy.ppd.core.model.BottomBar;
import com.luckyboy.ppd.core.model.Destination;
import com.luckyboy.ppd.core.util.AppConfig;
import com.luckyboy.ppd.core.util.DensityUtils;

import java.util.List;

public class AppBottomBar extends BottomNavigationView {

    private static int[] sIcons = new int[]{R.drawable.icon_tab_home, R.drawable.icon_tab_sofa, R.drawable.icon_tab_publish, R.drawable.icon_tab_find, R.drawable.icon_tab_mine};

    public AppBottomBar(Context context) {
        this(context, null);
    }

    public AppBottomBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("RestrictedApi")
    public AppBottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        BottomBar mConfig = AppConfig.getBottomBarConfig();

        int[][] states = new int[2][];
        states[1] = new int[]{android.R.attr.state_selected};
        states[0] = new int[]{};
        int[] colors = new int[]{Color.parseColor(mConfig.activeColor), Color.parseColor(mConfig.inActiveColor)};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        setItemTextColor(colorStateList);
        setItemIconTintList(colorStateList);

        setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        List<BottomBar.Tab> tabs = mConfig.tabs;

        for (BottomBar.Tab tab : tabs) {
            if (!tab.enable) {
                continue;
            }

            int itemId = getItemId(tab.pageUrl);
            if (itemId < 0) {
                continue;
            }
            MenuItem menuItem = getMenu().add(0, itemId, tab.index, tab.title);
            menuItem.setIcon(sIcons[tab.index]);
        }

        // 给按钮设置大小
        int index = 0;
        for (BottomBar.Tab tab : tabs) {
            if (!tab.enable) {
                continue;
            }
            int itemId = getItemId(tab.pageUrl);
            if (itemId < 0) {
                continue;
            }
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) getChildAt(0);
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(index);
            // 这块限制 需要在方法上加 @SuppressLint("RestrictedApi")
            itemView.setIconSize(DensityUtils.dp2Px(tab.size));
            if (TextUtils.isEmpty(tab.title)) {
                int themeColor = getResources().getColor(R.color.colorAccent);
                int tintColor = TextUtils.isEmpty(tab.tintColor) ? themeColor : Color.parseColor(tab.tintColor);
                itemView.setIconTintList(ColorStateList.valueOf(tintColor));
                // 禁止 上下浮动效果
                itemView.setShifting(false);
            }
            index++;
        }

        // 默认选中项\
        if (mConfig.selectTab != 0) {
            int selectTabIndex = mConfig.selectTab;
            BottomBar.Tab selectTab = mConfig.tabs.get(selectTabIndex);
            if (selectTab.enable) {
                // 这里需要延迟以下 在定位到默认选中的Tab
                // 因为 我们需要等待内容区域，也就是NavGraphBuilder 解析数据并初始化完成
                // 否则会出现 底部按钮切换过去了 但是内容区域环没有切换过去
                post(() -> {
                    setSelectedItemId(getItemId(selectTab.pageUrl));
                });
            }
        }
    }


    private int getItemId(String pageUrl) {
        Destination destination = AppConfig.getDestinationConfig().get(pageUrl);
        if (destination == null) {
            return -1;
        }
        return destination.id;
    }

}
