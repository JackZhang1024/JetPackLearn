package com.luckyboy.jetpacklearn.db.dataresource

import androidx.paging.DataSource
import com.luckyboy.jetpacklearn.db.data.Shoe
import com.luckyboy.jetpacklearn.db.repository.ShoeRepository

class CustomPageDataSourceFactory(private val shoeRepository: ShoeRepository) :
    DataSource.Factory<Int, Shoe>() {

    override fun create(): DataSource<Int, Shoe> {
        return CustomPageDataSource(shoeRepository)
    }

}