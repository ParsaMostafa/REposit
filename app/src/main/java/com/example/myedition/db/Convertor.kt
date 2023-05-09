package com.example.myedition.db

import androidx.room.TypeConverter
import com.example.myedition.models.Source

class Convertor {
    @TypeConverter
    fun fromSource (source: Source):String? {
        return source.name
    }
    @TypeConverter
    fun toSource(name:String): Source {
        return Source(name,name)
    }
}