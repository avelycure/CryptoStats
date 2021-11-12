package com.avelycure.cryptostats.data.local.type_converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ConverterListFloat {
    @TypeConverter
    fun fromFloatList(value: List<Float>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Float>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toFloatList(value: String): List<Float> {
        val gson = Gson()
        val type = object : TypeToken<List<Float>>() {}.type
        return gson.fromJson(value, type)
    }
}