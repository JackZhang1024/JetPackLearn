package com.luckyboy.jetpacklearn.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.luckyboy.jetpacklearn.db.dao.FavouriteShoeDao
import com.luckyboy.jetpacklearn.db.dao.ShoeDao
import com.luckyboy.jetpacklearn.db.dao.UserDao
import com.luckyboy.jetpacklearn.db.data.FavouriteShoe
import com.luckyboy.jetpacklearn.db.data.Shoe
import com.luckyboy.jetpacklearn.db.data.User

// 数据库文件
@Database(
    entities = [
        User::class,
        Shoe::class,
        FavouriteShoe::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {

    // 得到UserDao
    abstract fun userDao(): UserDao

    // 得到ShoeDao
    abstract fun shoeDao(): ShoeDao

    // 得到FavouriteShoeDao
    abstract fun favouriteShoeDao(): FavouriteShoeDao

    companion object {

        @Volatile
        private var instance: AppDataBase? = null


        // A?:B 如果A为空 则返回B 否则即使A本身
        fun getInstance(context: Context): AppDataBase{
            return instance?: synchronized(this){
               instance?: buildDataBase(context)
                   .also {
                       instance = it
                   }
            }
        }

        private fun buildDataBase(context: Context): AppDataBase {
            return Room
                .databaseBuilder(
                    context,
                    AppDataBase::class.java,
                    "jetPackDemo.db")
                .addCallback(object :RoomDatabase.Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        // 读取鞋的的集合
                        // ???
                    }
                })
                .build()
        }


    }

}