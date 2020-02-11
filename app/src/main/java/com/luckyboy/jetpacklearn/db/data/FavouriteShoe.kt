package com.luckyboy.jetpacklearn.db.data

import androidx.room.*
import java.util.*

// 喜欢的鞋子
@Entity(
    tableName = "fav_shoe",
    foreignKeys = [
        ForeignKey(entity = Shoe::class, parentColumns = ["id"],
            childColumns = ["shoe_id"]),
        ForeignKey(entity = User::class, parentColumns = ["id"],
            childColumns = ["user_id"])
    ],
    indices = [Index("shoe_id")]
)
data class FavouriteShoe(
    @ColumnInfo(name = "shoe_id")  val shoeId:Long,
    @ColumnInfo(name = "user_id")  val userId:Long,
    @ColumnInfo(name = "fav_date") val date:Calendar
){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id:Long = 0
}

