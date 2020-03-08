package com.luckyboy.ppd.core.util;

import android.util.DisplayMetrics;

import com.luckyboy.libcommon.global.AppGlobals;

public class DensityUtils {


    public static int dp2Px(int dpValue) {
        DisplayMetrics metrics = AppGlobals.getInstance().getResources().getDisplayMetrics();
        return (int) (metrics.density * dpValue + 0.5);
    }




}
