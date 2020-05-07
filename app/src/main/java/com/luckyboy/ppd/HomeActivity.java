package com.luckyboy.ppd;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.luckyboy.jetpacklearn.R;
import com.luckyboy.libcommon.utils.StatusBar;
import com.luckyboy.ppd.core.model.Destination;
import com.luckyboy.ppd.core.util.AppConfig;
import com.luckyboy.ppd.core.util.NavGraphBuilder;
import com.luckyboy.ppd.login.UserManager;
import com.luckyboy.ppd.core.model.User;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private NavController navController;
    private BottomNavigationView nav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 启用沉浸式布局 白底黑字
        StatusBar.fitSystemBar(this);
        setContentView(R.layout.activity_ppd_home);
        nav = findViewById(R.id.nav_view);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = NavHostFragment.findNavController(fragment);
        NavGraphBuilder.build(this, navController, fragment.getId());
        nav.setOnNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // 判断是否需要登录
        Log.e(TAG, "onNavigationItemSelected: ");
        HashMap<String, Destination> destinationHashMap = AppConfig.getDestinationConfig();
        Iterator<Map.Entry<String, Destination>> iterator = destinationHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Destination> entry = iterator.next();
            Destination destination = entry.getValue();
            if (destination != null && !UserManager.get().isLogin() && destination.needLogin && item.getItemId() == destination.id) {
                Log.e(TAG, "onNavigationItemSelected: 执行了多次？？");
                // 进行登录页面跳转
                UserManager.get().login(this).observe(this, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        if (user!=null){
                            Log.e(TAG, "onChanged: user "+(user==null));
                            nav.setSelectedItemId(item.getItemId());
                        }
                    }
                });
                return false;
            }
        }
        navController.navigate(item.getItemId());
        return !TextUtils.isEmpty(item.getTitle());
    }

    @Override
    public void onBackPressed() {
        // 获取当前页面的DestinationID
        int currentDestinationId = navController.getCurrentDestination().getId();
        // 获取APP页面导航结构图 获取首页的destinationID
        int homeDestinationId = navController.getGraph().getStartDestination();
        // 如果当前页面的destinationId和首页的不一致 则跳转到首页
        if (currentDestinationId != homeDestinationId) {
            Log.e(TAG, "onBackPressed: 跳转到首页");
            nav.setSelectedItemId(homeDestinationId);
            return;
        }
        // 否则 finish 这里不宜调用 onBackPressed() 因为navigation 会操作回退栈 切换到之前显示的页面
        finish();
    }



}
