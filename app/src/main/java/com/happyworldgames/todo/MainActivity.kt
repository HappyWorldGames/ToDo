package com.happyworldgames.todo

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.happyworldgames.todo.databinding.ActivityMainBinding
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.DataInterface
import com.happyworldgames.todo.view.MainRecyclerViewAdapter
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + Job()

    private val activityMain by lazy { ActivityMainBinding.inflate(layoutInflater) } // activity view
    private val adapter by lazy { MainRecyclerViewAdapter(this) }   // adapter for recyclerview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMain.root)

        launch(Dispatchers.Main) {
            onCreate()
        }
    }

    private suspend fun onCreate() {
        activityMain.recyclerView.layoutManager = GridLayoutManager(this, 2,
            RecyclerView.VERTICAL, false)
        activityMain.recyclerView.adapter = adapter

        val alert = withContext(Dispatchers.Main){
            createAlert()
        }
        activityMain.floatingActionButton.setOnClickListener {
            alert.show()
        }
    }

    /*
        Alert for create board
     */
    private fun createAlert(): AlertDialog {
        val editTextName = EditText(this).apply {
            hint = getString(R.string.board_name)
            inputType = InputType.TYPE_TEXT_VARIATION_FILTER
            setOnKeyListener { _, _, _ ->
                if(text.isNotBlank()) error = null
                true
            }
        }
        val alertDialog = AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.create_board))
            setView(editTextName)
            setCancelable(false)
            setPositiveButton(getString(R.string.create), null)
            setNeutralButton(getString(R.string.cancel), null)
            setOnDismissListener {
                editTextName.error = null
                editTextName.setText("")
            }
        }.create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                if(editTextName.text.isNullOrBlank()){
                    editTextName.error = "Null"
                    return@setOnClickListener
                }
                // create board
                val boardInfo = BoardInfo(UUID.randomUUID().toString(), -1,
                    editTextName.text.toString())
                DataInterface.getDataInterface(this@MainActivity).saveBoard(boardInfo)
                // end create
                adapter.notifyItemInserted(adapter.itemCount)
                alertDialog.dismiss()
            }
        }
        return alertDialog
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }
}