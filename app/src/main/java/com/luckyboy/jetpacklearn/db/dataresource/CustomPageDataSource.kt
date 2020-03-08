package com.luckyboy.jetpacklearn.db.dataresource

import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.luckyboy.jetpacklearn.common.BaseConstant
import com.luckyboy.jetpacklearn.db.data.Shoe
import com.luckyboy.jetpacklearn.db.repository.ShoeRepository

// 自定义PageKeyDataSource
// 演示Page库的时候使用
// PageKeyedDataSource 是按照页码去分页操作的
// PageKeyedDataSource<Int, Shoe>() Int 表示的是用来查询数据Shoe的Key是整形的  Shoe表示要查询的数据类型
class CustomPageDataSource(private val shoeRepository: ShoeRepository) :
    PageKeyedDataSource<Int, Shoe>() {

    private val TAG: String by lazy {
        this::class.java.simpleName
    }

    // 第一次加载的时候使用
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Shoe>
    ) {
        Log.e(TAG, "CustomPageDataSource ")
        val startIndex = 0L
        val endIndex: Long = 0L + params.requestedLoadSize
        val shoes = shoeRepository.getPageShoes(startIndex, endIndex)
        callback.onResult(shoes, null, 2)
    }

    // 每次分页加载的时候使用  分页开始了
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Shoe>) {
        Log.e(TAG, "startPage:${params.key}, size:${params.requestedLoadSize}")
        val startPage = params.key
        val startIndex = ((startPage - 1)) * BaseConstant.SINGLE_PAGE_SIZE.toLong() + 1
        val endIndex = startIndex + params.requestedLoadSize - 1
        val shoes = shoeRepository.getPageShoes(startIndex, endIndex)
        callback.onResult(shoes, params.key + 1)
    }


    // 向前加载 基本上不会用到
    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Shoe>) {
        Log.e(TAG, "endPage ${params.key}, size:${params.requestedLoadSize}")
        val endPage = params.key
        val endIndex = ((endPage - 1) * BaseConstant.SINGLE_PAGE_SIZE).toLong() + 1
        var startIndex = endIndex - params.requestedLoadSize
        startIndex = if (startIndex < 0) 0L else startIndex
        val shoes = shoeRepository.getPageShoes(startIndex, endIndex)
        callback.onResult(shoes, params.key + 1)
    }


}