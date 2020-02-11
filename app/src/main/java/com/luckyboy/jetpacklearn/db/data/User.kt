package com.luckyboy.jetpacklearn.db.data

import androidx.room.*

// 用户表
@Entity(tableName = "user")
data class User(
    @ColumnInfo(name="user_account") val account:String, // 账号
    @ColumnInfo(name="user_pwd")  val pwd:String, // 密码
    @ColumnInfo(name="user_name") val name:String, // 用户名
    @ColumnInfo(name="user_url") var headImage:String // 头像地址

//    @Embedded val address: Address,
//    @Ignore val state:Int
    ){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    var id:Long=0

}