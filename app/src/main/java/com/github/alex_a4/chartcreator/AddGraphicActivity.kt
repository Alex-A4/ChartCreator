package com.github.alex_a4.chartcreator

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.github.alex_a4.chartcreator.database.GraphicDatabase
import com.github.alex_a4.chartcreator.models.GraphicFunction
import com.github.alex_a4.chartcreator.view_model.GraphicViewModel
import com.github.alex_a4.chartcreator.view_model.GraphicViewModelFactory
import com.itis.libs.parserng.android.expressParser.MathExpression
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import top.defaults.colorpicker.ColorPickerPopup


class AddGraphicActivity : AppCompatActivity() {
    private lateinit var viewModel: GraphicViewModel
    private lateinit var functionsList: RecyclerView

    private val adapter = FunctionsAdapter()
    private val functions = MutableLiveData<MutableList<GraphicFunction>>(mutableListOf())

    private lateinit var functionInput: EditText
    private lateinit var widthInput: EditText
    private lateinit var colorButton: AppCompatButton
    private lateinit var graphicView: GraphView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_graphic)
        setSupportActionBar(findViewById(R.id.add_graphic_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        functionsList = findViewById(R.id.add_graphic_functions_list)
        functionsList.adapter = adapter

        val application = requireNotNull(this).application
        val dao = GraphicDatabase.getInstance(application).getDao()
        val viewModelFactory = GraphicViewModelFactory(dao, application)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(GraphicViewModel::class.java)

        functions.observe(this, { f ->
            adapter.updateData(f)
            updateGraphic(f)
        })

        graphicView = findViewById(R.id.add_graphic_view)
        functionInput = findViewById(R.id.add_graphic_function_edit)
        widthInput = findViewById(R.id.add_graphic_function_width)
        colorButton = findViewById(R.id.add_graphic_color_pick)
    }

    private fun updateGraphic(f: MutableList<GraphicFunction>) {
        graphicView.series.clear()
        try {
            for (function in f) {
                val series: LineGraphSeries<DataPoint> = LineGraphSeries()
                var x: Double = -20.0
                while (x <= 20.0) {
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

    fun addFunction(view: View) {
        try {
            val f = functionInput.text.toString()
            val expression = MathExpression("x=1;$f")
            expression.solve()

            val func = GraphicFunction(
                0L,
                f,
                (colorButton.background as ColorDrawable).color,
                widthInput.text.toString().toInt(),
            )
            Log.i("Added function", func.toString())
            functions.value!!.add(func)
            functions.value = functions.value
            functionInput.text.clear()
            widthInput.text.clear()
        } catch (e: Exception) {
            Toast.makeText(this, "Функция должна зависеть от x", Toast.LENGTH_SHORT).show()
        }
    }

    fun pickColor(view: View) {
        ColorPickerPopup.Builder(this)
            .initialColor((view.background as ColorDrawable).color) // Set initial color
            .enableBrightness(true) // Enable brightness slider or not
            .enableAlpha(true) // Enable alpha slider or not
            .okTitle("Выбрать")
            .cancelTitle("Отменить")
            .showIndicator(true)
            .showValue(true)
            .build()
            .show(
                view,
                object : ColorPickerPopup.ColorPickerObserver() {
                    override fun onColorPicked(color: Int) {
                        view.setBackgroundColor(color)
                    }
                },
            )
    }

    fun saveGraphic(view: View) {
        if (functions.value!!.size != 0) {
            viewModel.addGraphic(functions.value!!)
            finish()
        }
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


class FunctionsAdapter(
    private var items: List<GraphicFunction> = mutableListOf(),
) :
    RecyclerView.Adapter<FunctionsAdapter.GraphicsHolder>() {

    fun updateData(list: List<GraphicFunction>) {
        items = list
        notifyDataSetChanged()
    }

    inner class GraphicsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val functionText = itemView.findViewById<TextView>(R.id.function_item_text)
        private val widthText = itemView.findViewById<TextView>(R.id.function_item_width)
        private val colorView = itemView.findViewById<View>(R.id.function_item_color)

        fun bind(item: GraphicFunction) {
            functionText.text = item.function
            widthText.text = item.width.toString()
            colorView.setBackgroundColor(item.color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraphicsHolder =
        GraphicsHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.function_item,
                parent,
                false
            )
        )


    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: GraphicsHolder, i: Int) {
        holder.bind(items[itemCount - i - 1])
    }
}
