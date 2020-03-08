package com.luckyboy.ppd.core.util;

import android.content.ComponentName;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;
import com.luckyboy.libcommon.global.AppGlobals;
import com.luckyboy.ppd.core.model.Destination;
import com.luckyboy.ppd.core.navigator.FixFragmentNavigator;
import java.util.HashMap;
import java.util.Iterator;


public class NavGraphBuilder {

    // controller navgraph destination
    public static void build(FragmentActivity activity, NavController controller, int containerId) {


        NavigatorProvider provider = controller.getNavigatorProvider();
        NavGraph navGraph = new NavGraph(new NavGraphNavigator(provider));

        // 创建navigator
        // navigator 创建配置 destination
        // navGraph 1. 设置destination 2. 设置起航地址
        ActivityNavigator activityNavigator = provider.getNavigator(ActivityNavigator.class);

        //FragmentNavigator fragmentNavigator = provider.getNavigator(FragmentNavigator.class)
        //fragment的导航此处使用的是我们定制的FixFragmentNavigator 底部Tab切换的 使用 hide()和show() 而不是 replace()

        FixFragmentNavigator fragmentNavigator = new FixFragmentNavigator(activity, activity.getSupportFragmentManager(), containerId);
        provider.addNavigator(fragmentNavigator);

        HashMap<String, Destination> destConfig = AppConfig.getDestinationConfig();
        Iterator<Destination> iterator = destConfig.values().iterator();
        while (iterator.hasNext()) {
            Destination node = iterator.next();
            if (node.isFragment) {
                FragmentNavigator.Destination fragmentDestination = fragmentNavigator.createDestination();
                fragmentDestination.setId(node.id);
                fragmentDestination.setClassName(node.className);
                fragmentDestination.addDeepLink(node.pageUrl);
                navGraph.addDestination(fragmentDestination);
            } else {
                ActivityNavigator.Destination activityDestination = activityNavigator.createDestination();
                activityDestination.setId(node.id);
                activityDestination.setComponentName(new ComponentName(AppGlobals.getInstance().getPackageName(), node.className));
                activityDestination.addDeepLink(node.pageUrl);
                navGraph.addDestination(activityDestination);
            }
            // 给APP页面导航结构图 设置一个默认展示页面的id
            if (node.asStarter) {
                navGraph.setStartDestination(node.id);
            }
        }
        controller.setGraph(navGraph);
    }
}
