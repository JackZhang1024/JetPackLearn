package com.luckyboy.jetpacklearn.db.dataresource

import androidx.paging.ItemKeyedDataSource
import com.luckyboy.jetpacklearn.db.data.Shoe

class CustomItemDataSource :ItemKeyedDataSource<Int, Shoe>(){

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Shoe>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Shoe>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Shoe>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getKey(item: Shoe): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}