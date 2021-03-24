package com.github.alex_a4.chartcreator.database

import androidx.room.TypeConverter
import com.github.alex_a4.chartcreator.models.GraphicFunction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Конвертер, позволяющий преобразовывать функции из/в json
class GraphicConverter {
    @TypeConverter
    fun functionFromString(value: String?): List<GraphicFunction>? {
        val type = object : TypeToken<List<GraphicFunction>>() {}.type
        return Gson().fromJson<List<GraphicFunction>>(value, type)
    }

    @TypeConverter
    fun functionToString(functions: List<GraphicFunction?>?): String? {
        val type = object : TypeToken<List<GraphicFunction>>() {}.type
        return Gson().toJson(functions, type)
    }
}