package com.github.alex_a4.chartcreator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class GraphicActivity : AppCompatActivity() {
    companion object {
        const val graphicIndexKey = "GraphicIndexKey"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphic)

        val index = intent.getIntExtra(graphicIndexKey, -1)
        if (index == -1) {
            finish()
        }
    }
}