package com.luckyboy.ppd.core.model;

import java.util.List;

public class BottomBar {

    public String activeColor;
    public String inActiveColor;
    public int selectTab; // 底部导航栏默认选中项
    public List<Tab> tabs;


    public static class Tab {
        public int size;
        public boolean enable;
        public String pageUrl;
        public int index;
        public String title;
        public String tintColor;
    }


}
