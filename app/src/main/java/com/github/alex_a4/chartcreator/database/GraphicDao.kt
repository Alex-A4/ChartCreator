package com.github.alex_a4.chartcreator.database

import androidx.room.*
import com.github.alex_a4.chartcreator.models.Graphic

@Dao
interface GraphicDao {
    @Query("SELECT * FROM graphics")
    fun getGraphics(): MutableList<Graphic>

    @Insert
    fun addGraphic(graphic: Graphic)

    @Update
    fun updateGraphic(graphic: Graphic)

    @Delete
    fun deleteGraphic(graphic: Graphic)
}