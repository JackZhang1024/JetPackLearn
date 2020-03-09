package com.luckyboy.ppd.core.view;

import android.content.Context;
import android.util.AttributeSet;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

public class PPDTwinkRefreshLayout extends TwinklingRefreshLayout {

    public PPDTwinkRefreshLayout(Context context) {
        super(context);
    }

    public PPDTwinkRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PPDTwinkRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public boolean isRefreshState() {
        return isRefreshing;
    }

    public boolean isLoadMoreState(){
        return isLoadingMore;
    }


}

