package com.luckyboy.jetpacklearn.db.repository

import com.luckyboy.jetpacklearn.db.dao.ShoeDao
import com.luckyboy.jetpacklearn.db.data.Shoe

class ShoeRepository private constructor(private val shoeDao: ShoeDao){

    // 通过id的范围寻找鞋子
    fun getPageShoes(startIndex:Long, endIndex:Long):List<Shoe> = shoeDao.findShoeByIndexRange(startIndex, endIndex)

    fun getAllShoes() = shoeDao.getAllShoesLD()

    // 通过品牌查询鞋子
    fun getShoesByBrand(brand:Array<String>) = shoeDao.findShoeByBrandLD(brand)

    // 通过Id查询一双鞋
    fun getShoeById(id:Long) = shoeDao.findShoeByIdLD(id)


    // 查询用户收藏的鞋
    fun getShoesByUserId(userId:Long) = shoeDao.findShoesByUserId(userId)


    // 插入鞋子的集合
    fun insertShoes(shoes:List<Shoe>) = shoeDao.insertShoes(shoes)

    companion object {
        @Volatile
        private var instance: ShoeRepository? = null

        fun getInstance(shoeDao: ShoeDao):ShoeRepository =
            instance?: synchronized(this){
                instance?:ShoeRepository(shoeDao).also {
                    instance = it
                }
            }
    }
}