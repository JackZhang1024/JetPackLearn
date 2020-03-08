package com.luckyboy.ppd.core.util;

import android.content.res.AssetManager;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.luckyboy.libcommon.global.AppGlobals;
import com.luckyboy.ppd.core.model.BottomBar;
import com.luckyboy.ppd.core.model.Destination;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class AppConfig {


    private static HashMap<String, Destination> mDestinationCConfig;
    private static BottomBar mBottomBar;

    // api xxxx 是全局的 implementation 是局部依赖
    public static HashMap<String, Destination> getDestinationConfig() {
        if (mDestinationCConfig == null) {
            String content = parseFile("destination.json");
            mDestinationCConfig = JSON.parseObject(content, new TypeReference<HashMap<String, Destination>>() {});
        }
        return mDestinationCConfig;
    }


    public static BottomBar getBottomBarConfig() {
        if (mBottomBar == null) {
            String content = parseFile("main_tabs_config.json");
            mBottomBar = JSON.parseObject(content, BottomBar.class);
        }
        return mBottomBar;
    }


    // 解析
    private static String parseFile(String filename) {
        AssetManager assetManager = AppGlobals.getInstance().getAssets();
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder builder = new StringBuilder();
        try {
            is = assetManager.open(filename);
            br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {

            }
        }
        return builder.toString();
    }


}
