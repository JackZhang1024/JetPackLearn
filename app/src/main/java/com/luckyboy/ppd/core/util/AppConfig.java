package com.luckyboy.ppd.core.util;

import android.content.res.AssetManager;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.luckyboy.libcommon.global.AppGlobals;
import com.luckyboy.ppd.core.model.BottomBar;
import com.luckyboy.ppd.core.model.Destination;
import com.luckyboy.ppd.core.model.SofaTab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class AppConfig {


    private static HashMap<String, Destination> mDestinationCConfig;
    private static BottomBar mBottomBar;
    private static SofaTab sSoftTab, sFindTabConfig;

    // api xxxx 是全局的 implementation 是局部依赖
    public static HashMap<String, Destination> getDestinationConfig() {
        if (mDestinationCConfig == null) {
            String content = parseFile("destination.json");
            mDestinationCConfig = JSON.parseObject(content, new TypeReference<HashMap<String, Destination>>() {
            });
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

    public static SofaTab getSofaTabConfig() {
        if (sSoftTab == null) {
            String content = parseFile("sofa_tabs_config.json");
            sSoftTab = JSON.parseObject(content, SofaTab.class);
            Collections.sort(sSoftTab.tabs, new Comparator<SofaTab.Tabs>() {
                @Override
                public int compare(SofaTab.Tabs o1, SofaTab.Tabs o2) {
                    // 按照下标从小到大进行排序
                    return o1.index < o2.index ? -1 : 1;
                }
            });
        }
        return sSoftTab;
    }

    public static SofaTab getFindTabConfig() {
        if (sFindTabConfig == null) {
            String content = parseFile("find_tabs_config.json");
            sFindTabConfig = JSON.parseObject(content, SofaTab.class);
            Collections.sort(sFindTabConfig.tabs, new Comparator<SofaTab.Tabs>() {
                @Override
                public int compare(SofaTab.Tabs o1, SofaTab.Tabs o2) {
                    return o1.index<o2.index? -1: 1;
                }
            });
        }
        return sFindTabConfig;
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
