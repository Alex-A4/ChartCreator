package com.github.alex_a4.chartcreator.models

import androidx.room.Entity
import androidx.room.PrimaryKey

// Класс, описывающий функцию, нарисованную на графике
@Entity
data class GraphicFunction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val function: String,
    val color: Int,
    val width: Int
) {
    override fun toString(): String = "$function, $color, $width"
}
