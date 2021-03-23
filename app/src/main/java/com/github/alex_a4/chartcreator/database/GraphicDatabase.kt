package com.github.alex_a4.chartcreator.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.alex_a4.chartcreator.models.Graphic

@Database(entities = [Graphic::class], version = 1)
@TypeConverters(GraphicConverter::class)
abstract class GraphicDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: GraphicDatabase? = null

        fun getInstance(context: Context): GraphicDatabase {
            synchronized(this) {
                var instance = Companion.INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        GraphicDatabase::class.java, "graphics"
                    )
                        .build()
                    Companion.INSTANCE = instance
                }
                return instance
            }
        }
    }

    abstract fun getDao(): GraphicDao
}
