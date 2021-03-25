package com.github.alex_a4.chartcreator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import com.github.alex_a4.chartcreator.database.GraphicDatabase
import com.github.alex_a4.chartcreator.models.Graphic
import com.github.alex_a4.chartcreator.view_model.GraphicViewModel
import com.github.alex_a4.chartcreator.view_model.GraphicViewModelFactory
import com.itis.libs.parserng.android.expressParser.MathExpression
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: GraphicViewModel

    private lateinit var graphicView: GraphView
    private lateinit var graphicText: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val application = requireNotNull(this).application
        val dao = GraphicDatabase.getInstance(application).getDao()
        val viewModelFactory = GraphicViewModelFactory(dao, application)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(GraphicViewModel::class.java)

        viewModel.graphics.observe(this, { gr ->
            Log.i("Main Graphics: ", gr.size.toString())
            if (gr.isNotEmpty()) {
                createGraphic(gr.last())
            } else {
                graphicText.visibility = View.INVISIBLE
                graphicView.visibility = View.INVISIBLE
            }
        })

        graphicView = findViewById(R.id.last_graphic)
        graphicText = findViewById(R.id.last_graphic_text)
    }

    fun addGraphicClick(view: View) {
        val addActivity = Intent(this, AddGraphicActivity::class.java)
        startActivity(addActivity)
    }

    fun viewHistoryClick(view: View) {
        val historyIntent = Intent(this, HistoryActivity::class.java)
        startActivity(historyIntent)
    }

    private fun createGraphic(graphic: Graphic) {
        graphicText.visibility = View.VISIBLE
        graphicView.visibility = View.VISIBLE

        graphicView.series.clear()
        try {
            for (function in graphic.functions) {
                val series: LineGraphSeries<DataPoint> = LineGraphSeries()
                var x: Double = -20.0
                while (x <= 20.0) {
                    val expression = MathExpression("x=$x;${function.function};")
                    val y = expression.solve().toDouble()
                    series.appendData(DataPoint(x, y), false, 500)
                    x += 1
                }
                series.title = function.function
                series.color = function.color
                graphicView.addSeries(series)
            }
        } catch (e: Exception) {
            Log.e("Exception:", e.toString())
        }
    }
}