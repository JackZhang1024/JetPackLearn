package com.luckyboy.ppd.publish;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.luckyboy.jetpacklearn.R;
import com.luckyboy.jetpacklearn.databinding.ActivityPpdPublishBinding;
import com.luckyboy.libnavannotation.ActivityDestination;

@ActivityDestination(pageUrl = "main/tabs/publish", needLogin = true)
public class publishActivity extends AppCompatActivity {

    ActivityPpdPublishBinding activityPpdPublishBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPpdPublishBinding = DataBindingUtil.setContentView(this, R.layout.activity_ppd_publish);
    }


}
