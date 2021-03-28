package com.github.alex_a4.chartcreator

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import com.github.alex_a4.chartcreator.database.GraphicDatabase
import com.github.alex_a4.chartcreator.models.Graphic
import com.github.alex_a4.chartcreator.view_model.GraphicViewModel
import com.github.alex_a4.chartcreator.view_model.GraphicViewModelFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.itis.libs.parserng.android.expressParser.MathExpression


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: GraphicViewModel

    private lateinit var graphicView: LineChart
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

    override fun onStart() {
        viewModel.initGraphics()
        super.onStart()
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
        graphicView.isDragEnabled = true
        graphicView.setScaleEnabled(true)

        val dataSets: ArrayList<ILineDataSet> = ArrayList()

        graphicView.clear()
        try {
            for (function in graphic.functions) {
                val data = mutableListOf<Entry>()
                var x: Float = -20.0F
                while (x <= 20.0F) {
                    val expression = MathExpression("x=$x;${function.function};")
                    val y = expression.solve().toFloat()
                    if (!y.isNaN() && !y.isInfinite()) {
                        data.add(Entry(x, y))
                    }
                    x += 1
                }
                val series = LineDataSet(data, function.function)
                series.color = function.color
                series.valueTextSize = 0.0F
                series.setDrawCircles(false)
                series.setDrawCircleHole(false)
                series.lineWidth = function.width.toFloat()
                dataSets.add(series)
            }

            val data = LineData(dataSets)
            graphicView.data = data
        } catch (e: Exception) {
        }
    }
}