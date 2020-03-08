package com.luckyboy.jetpacklearn.viewmodel

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import com.luckyboy.jetpacklearn.common.createPageList
import com.luckyboy.jetpacklearn.db.data.Shoe
import com.luckyboy.jetpacklearn.db.dataresource.CustomPageDataSource
import com.luckyboy.jetpacklearn.db.dataresource.CustomPageDataSourceFactory
import com.luckyboy.jetpacklearn.db.repository.ShoeRepository


class ShoeModel constructor(shoeRepository: ShoeRepository) : ViewModel() {

    // 品牌的观察对象 默认观察所有的品牌
    private val brand = MutableLiveData<String>().apply {
        value = ALL
    }

    // 鞋子集合的观察类
    val shoes: LiveData<PagedList<Shoe>> = brand.switchMap {
        // Room数据库查询，只要知道返回的是LiveData<List<Shoe>> 即可
        if (it == ALL) {
            val factory = CustomPageDataSourceFactory(shoeRepository)
            val pageConfig: PagedList.Config = PagedList.Config.Builder()
                .setPageSize(10)
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(10)
                .build()
            LivePagedListBuilder<Int, Shoe>(factory, pageConfig).build()
        } else {
            val array: Array<String> =
                when (it) {
                    NIKE -> arrayOf("Nike", "Air Jordan")
                    ADIDAS -> arrayOf("Adidas")
                    else -> arrayOf("Converse", "UA", "ANTA")
                }
            shoeRepository.getShoesByBrand(array)
                .createPageList(6, 6)
        }
    }

    fun setBrand(brand: String) {
        this.brand.value = brand
        this.brand.map {

        }
    }

    fun clearBrand() {
        this.brand.value = ALL
    }


    companion object {

        const val ALL = "所有"

        const val NIKE = "Nike"

        const val ADIDAS = "Adidas"

        const val OTHER = "other"
    }


}