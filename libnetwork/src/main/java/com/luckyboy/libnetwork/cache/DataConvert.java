package com.luckyboy.libnetwork.cache;

import androidx.room.TypeConverter;

import java.util.Date;

public class DataConvert {

    @TypeConverter
    public static Long date2Long(Date date){
        return date.getTime();
    }


    @TypeConverter
    public static Date long2Date(Long data){
        return new Date(data);
    }


}
