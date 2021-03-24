package com.github.alex_a4.chartcreator

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val application = requireNotNull(this).application
        val dao = GraphicDatabase.getInstance(application).getDao()
        val viewModelFactory = GraphicViewModelFactory(dao, application)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(GraphicViewModel::class.java)

        viewModel.graphics.observe(this, { gr ->
            if (gr.isNotEmpty()) {
                createGraphic(gr.last())
            }
        })
    }

    fun addGraphicClick(view: View) {
//        val myPasswordsIntent = Intent(this, MyPasswordsActivity::class.java)
//        startActivity(myPasswordsIntent)
    }

    fun viewHistoryClick(view: View) {
//        val myPasswordsIntent = Intent(this, MyPasswordsActivity::class.java)
//        startActivity(myPasswordsIntent)
    }

    private fun createGraphic(graphic: Graphic) {
        val graphicView: GraphView = findViewById(R.id.last_graphic)
        val graphicText: AppCompatTextView = findViewById(R.id.last_graphic_text)
        graphicText.visibility = View.VISIBLE
        graphicView.visibility = View.VISIBLE

        graphicView.series.clear()
        try {
            for (function in graphic.functions) {
                val series: LineGraphSeries<DataPoint> = LineGraphSeries()
                var x: Double = -100.0
                while (x <= 100.0) {
                    val expression = MathExpression("x=$x;${function.function};")
                    val y = expression.solve().toDouble()
                    series.appendData(DataPoint(x, y), true, 500)
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