package com.luckyboy.jetpacklearn.db

import androidx.room.TypeConverter
import java.util.*

// Type converters to allow room to reference complex data types

class Converters{

    @TypeConverter fun calendarToDatestamp(calendar: Calendar):Long = calendar.timeInMillis


    @TypeConverter fun datestampToCalendar(value:Long):Calendar = Calendar.getInstance().apply { timeInMillis = value }


}