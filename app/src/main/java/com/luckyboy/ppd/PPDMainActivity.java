package com.luckyboy.ppd;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.luckyboy.jetpacklearn.R;
import com.luckyboy.libnavannotation.ActivityDestination;

// App 主页入口
@ActivityDestination(pageUrl = "/PPDMain", needLogin = false, asStarter = false)
public class PPDMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppd_main);
        new Handler().postDelayed(()->{
            startActivity(new Intent(PPDMainActivity.this, HomeActivity.class));
        }, 1000);
    }



}
