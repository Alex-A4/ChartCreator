package com.github.alex_a4.chartcreator.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.github.alex_a4.chartcreator.database.GraphicDao
import com.github.alex_a4.chartcreator.models.Graphic
import com.github.alex_a4.chartcreator.models.GraphicFunction
import kotlinx.coroutines.*

class GraphicViewModel(private val dao: GraphicDao, application: Application) :
    AndroidViewModel(application) {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    private val _graphics = MutableLiveData<MutableList<Graphic>>()

    val graphics: LiveData<MutableList<Graphic>> get() = _graphics

    init {
        initGraphics()
    }

    fun initGraphics() {
        uiScope.launch {
            _graphics.value = getGraphicsFromDB()
        }
    }

    private suspend fun getGraphicsFromDB(): MutableList<Graphic> {
        return withContext(Dispatchers.IO) {
            val gr = dao.getGraphics()
            gr
        }
    }

    override fun onCleared() {
        super.onCleared()
        _graphics.value?.clear()
        viewModelJob.cancel()
    }

    fun addGraphic(functions: MutableList<GraphicFunction>) {
        uiScope.launch {
            val gr = Graphic(0L, functions)
            _graphics.value?.add(gr)
            _graphics.value = _graphics.value
            insert(gr)
        }
    }

    private suspend fun insert(graphic: Graphic) {
        withContext(Dispatchers.IO) {
            dao.addGraphic(graphic)
        }
    }

    fun deleteGraphic(index: Int) {
        uiScope.launch {
            val graphic = _graphics.value!!.removeAt(index)
            delete(graphic)
            _graphics.value = _graphics.value
        }
    }

    private suspend fun delete(graphic: Graphic) {
        withContext(Dispatchers.IO) {
            dao.deleteGraphic(graphic)
        }
    }

    fun addFunctionToGraphic(index: Int, function: String, color: Int, width: Int) {
        uiScope.launch {
            val graphic = _graphics.value!![index]
            graphic.functions.add(GraphicFunction(0L, function, color, width))
            updateGraphic(graphic)
            _graphics.value = _graphics.value
        }
    }

    private suspend fun updateGraphic(graphic: Graphic) {
        withContext(Dispatchers.IO) {
            dao.updateGraphic(graphic)
        }
    }
}


class GraphicViewModelFactory(private val dao: GraphicDao, private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GraphicViewModel::class.java)) {
            return GraphicViewModel(dao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}