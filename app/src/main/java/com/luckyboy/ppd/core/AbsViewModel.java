package com.luckyboy.ppd.core;


import androidx.lifecycle.ViewModel;

public class AbsViewModel<T> extends ViewModel {


    public AbsViewModel(){

    }


    // 可以在这方法里 进行一些清理的操作
    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
