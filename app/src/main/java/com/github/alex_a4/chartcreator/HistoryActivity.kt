package com.github.alex_a4.chartcreator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
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

class HistoryActivity : AppCompatActivity() {
    private lateinit var viewModel: GraphicViewModel
    private lateinit var historyGraphicList: RecyclerView

    private val adapter: GraphicsAdapter =
        GraphicsAdapter(
            deleteCallback = { i: Int ->
                viewModel.deleteGraphic(i)
            },
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(findViewById(R.id.history_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        historyGraphicList = findViewById(R.id.history_graphics_list)
        historyGraphicList.adapter = adapter

        val application = requireNotNull(this).application
        val dao = GraphicDatabase.getInstance(application).getDao()
        val viewModelFactory = GraphicViewModelFactory(dao, application)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(GraphicViewModel::class.java)

        viewModel.graphics.observe(this, { gr ->
            adapter.updateData(gr)
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}


class GraphicsAdapter(
    private var items: List<Graphic> = mutableListOf(),
    val deleteCallback: (index: Int) -> Unit,
) :
    RecyclerView.Adapter<GraphicsAdapter.GraphicsHolder>() {

    fun updateData(list: List<Graphic>) {
        items = list
        notifyDataSetChanged()
    }

    inner class GraphicsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val deleteButton = itemView.findViewById<AppCompatButton>(R.id.history_item_delete)
        private val graphic = itemView.findViewById<LineChart>(R.id.history_item_graphic)
        private val functions = itemView.findViewById<LinearLayout>(R.id.history_functions_list)

        fun bind(item: Graphic) {
            functions.removeViews(0, functions.childCount)
            graphic.isDragEnabled = true
            graphic.setScaleEnabled(true)
            item.functions.forEach { f ->
                functions.addView(TextView(itemView.context).apply {
                    text = f.function
                    textSize = 15.0F
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.marginEnd = 20
                    layoutParams = params
                })
            }
            calculateGraphicFunctions(item)
            deleteButton.setOnClickListener {
                deleteCallback(adapterPosition)
            }
        }

        private fun calculateGraphicFunctions(item: Graphic) {
            try {
                val dataSets: ArrayList<ILineDataSet> = ArrayList()
                for (function in item.functions) {
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
                graphic.data = data
            } catch (e: Exception) {
                Log.e("Exception:", e.toString())
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraphicsHolder =
        GraphicsHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.graphic_history_item,
                parent,
                false
            )
        )


    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: GraphicsHolder, i: Int) {
        holder.bind(items[i])
    }

}
