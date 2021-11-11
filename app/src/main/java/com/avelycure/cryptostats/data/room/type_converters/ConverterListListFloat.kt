package com.avelycure.cryptostats.data.room.type_converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ConverterListListFloat {
    @TypeConverter
    fun fromListListFloat(value: List<List<Float>>): String {
        val gson = Gson()
        val type = object : TypeToken<List<List<Float>>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toListListFloat(value: String): List<List<Float>> {
        val gson = Gson()
        val type = object : TypeToken<List<List<Float>>>() {}.type
        return gson.fromJson(value, type)
    }
}