package com.luckyboy.jetpacklearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luckyboy.jetpacklearn.db.data.FavouriteShoe
import com.luckyboy.jetpacklearn.db.data.Shoe
import com.luckyboy.jetpacklearn.db.repository.FavouriteShoeRepository
import com.luckyboy.jetpacklearn.db.repository.ShoeRepository
import kotlinx.coroutines.launch

class DetailModel constructor(
    shoeRepository: ShoeRepository,
    private val favouriteShoeRepository: FavouriteShoeRepository,
    private val shoeId:Long,
    var userId:Long
) : ViewModel() {

    // 鞋
    val shoe:LiveData<Shoe> = shoeRepository.getShoeById(shoeId)

    // 收藏记录
    val favouriteShoe:LiveData<FavouriteShoe?> =
        favouriteShoeRepository.findFavouriteShoe(userId, shoeId)

    // 收藏一双鞋
    fun favourite(){
        viewModelScope.launch {
            favouriteShoeRepository.createFavouriteShoe(userId, shoeId)
        }
    }


}