package com.github.alex_a4.chartcreator.models

import androidx.room.*
import com.github.alex_a4.chartcreator.database.GraphicConverter
import com.google.gson.annotations.SerializedName


// Класс графика, на котором была построена одна или несколько функций
@Entity(tableName = "graphics")
data class Graphic(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var id: Long = 0L,

    @SerializedName("functions")
    @TypeConverters(GraphicConverter::class)
    var functions: MutableList<GraphicFunction>
) {
    override fun toString(): String = "$id, $functions"
}
