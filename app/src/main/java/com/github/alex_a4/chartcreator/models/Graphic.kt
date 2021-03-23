package com.github.alex_a4.chartcreator.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


// Класс графика, на котором была построена одна или несколько функций
@Entity(tableName = "graphics")
data class Graphic(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "functions")
    val functions: MutableList<GraphicFunction>
) {
    override fun toString(): String = "$id, $functions"
}
