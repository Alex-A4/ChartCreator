package com.github.alex_a4.chartcreator.database

import androidx.room.TypeConverter
import com.github.alex_a4.chartcreator.models.GraphicFunction
import com.google.gson.Gson

// Конвертер, позволяющий преобразовывать функции из/в json
class GraphicConverter {
    @TypeConverter
    fun functionFromString(value: String?): GraphicFunction? {
        return Gson().fromJson(value, GraphicFunction::class.java)
    }

    @TypeConverter
    fun functionToString(function: GraphicFunction): String? {
        return Gson().toJson(function)
    }
}