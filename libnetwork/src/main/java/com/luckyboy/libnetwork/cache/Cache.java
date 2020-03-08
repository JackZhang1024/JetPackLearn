package com.luckyboy.libnetwork.cache;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "cache")
public class Cache implements Serializable {

    // PrimaryKey 必须要有 且不能为空 autoGenerate 主键的值是否由Room自动生成 默认为false
    @PrimaryKey(autoGenerate = false)
    @NonNull
    public String key;

//    @ColumnInfo(name = "_data") // 指定该字段在表中的名称
    public byte[] data;



}
