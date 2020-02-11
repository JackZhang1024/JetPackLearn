package com.luckyboy.jetpacklearn.common

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList


// 给DataSource.Factory 添加扩展函数
fun <T> DataSource.Factory<Int, T>.createPageList(pageSize:Int, defaultSize:Int):LiveData<PagedList<T>> {

    return LivePagedListBuilder<Int, T>(
       this, PagedList.Config.Builder()
            .setPageSize(2)
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(2)
            .build()
    ).build()


}