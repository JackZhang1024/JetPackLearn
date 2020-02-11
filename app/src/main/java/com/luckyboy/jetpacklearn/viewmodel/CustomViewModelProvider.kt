package com.luckyboy.jetpacklearn.viewmodel

import android.content.Context
import com.luckyboy.jetpacklearn.db.RepositoryProvider
import com.luckyboy.jetpacklearn.db.repository.ShoeRepository
import com.luckyboy.jetpacklearn.db.repository.UserRepository
import com.luckyboy.jetpacklearn.viewmodel.factory.*

// ViewModel提供者
object CustomViewModelProvider {

    fun providerRegisterModel(context: Context): RegisterModelFactory {
        val repository: UserRepository = RepositoryProvider.providerUserRepository(context)
        return RegisterModelFactory(repository)
    }

    fun providerLoginModel(context: Context): LoginModelFactory {
        val repository: UserRepository =
            RepositoryProvider.providerUserRepository(context)
        return LoginModelFactory(repository, context)
    }

    fun providerShoeModel(context: Context): ShoeModelFactory {
        val repository: ShoeRepository =
            RepositoryProvider.providerShoeRepository(context)
        return ShoeModelFactory(repository)
    }

    fun provideFavouriteModel(context: Context): FavouriteModelFactory {
        val repository: ShoeRepository =
            RepositoryProvider.providerShoeRepository(context)
        val userId: Long = 100L
        return FavouriteModelFactory(repository, userId)
    }

    fun providerMeModel(context: Context): MeModelFactory {
        val repository: UserRepository = RepositoryProvider.providerUserRepository(context)
        return MeModelFactory(repository)
    }

    fun providerDetailModel(
        context: Context,
        shoeId: Long,
        userId: Long
    ): FavouriteShoeModelFactory {
        val repository: ShoeRepository = RepositoryProvider.providerShoeRepository(context)
        val favouriteShoeModelFactory = RepositoryProvider.providerFavouriteShoeRepository(context)
        return FavouriteShoeModelFactory(repository, favouriteShoeModelFactory, shoeId, userId)
    }

}