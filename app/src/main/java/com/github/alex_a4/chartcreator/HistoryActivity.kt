package com.github.alex_a4.chartcreator

import android.content.Intent
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
import com.itis.libs.parserng.android.expressParser.MathExpression
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class HistoryActivity : AppCompatActivity() {
    private lateinit var viewModel: GraphicViewModel
    private lateinit var historyGraphicList: RecyclerView

    private val adapter =
        GraphicsAdapter(
            deleteCallback = { i: Int ->
                val index = viewModel.graphics.value!!.size - i - 1
                viewModel.deleteGraphic(index)
            },
            openCallback = { i: Int ->
                val index = viewModel.graphics.value!!.size - i - 1
                val intent = Intent(this, GraphicActivity::class.java)
                intent.putExtra(GraphicActivity.graphicIndexKey, index)
                startActivity(intent)
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
    val openCallback: (index: Int) -> Unit,
) :
    RecyclerView.Adapter<GraphicsAdapter.GraphicsHolder>() {

    fun updateData(list: List<Graphic>) {
        items = list
        notifyDataSetChanged()
    }

    inner class GraphicsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val openButton = itemView.findViewById<AppCompatButton>(R.id.history_item_open)
        private val deleteButton = itemView.findViewById<AppCompatButton>(R.id.history_item_delete)
        private val graphic = itemView.findViewById<GraphView>(R.id.history_item_graphic)
        private val functions = itemView.findViewById<LinearLayout>(R.id.history_functions_list)

        fun bind(item: Graphic) {
            item.functions.forEach { f ->
                functions.addView(TextView(itemView.context).apply {
                    text = f.function
                    textSize = 15.0F
                })
            }
            calculateGraphicFunctions(item)
            openButton.setOnClickListener {
                openCallback(adapterPosition)
            }
            deleteButton.setOnClickListener {
                deleteCallback(adapterPosition)
            }
        }

        private fun calculateGraphicFunctions(item: Graphic) {
            try {
                for (function in item.functions) {
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
                    graphic.addSeries(series)
                }
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
        holder.bind(items[itemCount - i - 1])
    }

}
