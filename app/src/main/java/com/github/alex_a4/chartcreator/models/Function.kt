package com.github.alex_a4.chartcreator.models

// Класс, описывающий функцию, нарисованную на графике
data class GraphicFunction(val function: String, val color: Int, val width: Int) {
    override fun toString(): String = "$function, $color, $width"
}
