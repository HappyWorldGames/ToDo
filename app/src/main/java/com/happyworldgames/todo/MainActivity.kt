package com.happyworldgames.todo

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.happyworldgames.todo.databinding.ActivityMainBinding
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.DataInterface
import com.happyworldgames.todo.view.MainRecyclerViewAdapter
import java.util.*

class MainActivity : AppCompatActivity() {
    private val activityMain by lazy { ActivityMainBinding.inflate(layoutInflater) } // activity view
    private val adapter by lazy { MainRecyclerViewAdapter(this) }   // adapter for recyclerview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMain.root)

        activityMain.recyclerView.layoutManager = GridLayoutManager(this, 2,
            RecyclerView.VERTICAL, false)
        activityMain.recyclerView.adapter = adapter

        val alert = createAlert()
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
        }
        return AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.create_board))
            setView(editTextName)
            setPositiveButton(getString(R.string.create)){ _, _ ->
                if(editTextName.text.isNullOrBlank()) return@setPositiveButton
                // create board
                val boardInfo = BoardInfo(UUID.randomUUID().toString(), -1,
                    editTextName.text.toString())
                DataInterface.getDataInterface(context).saveBoard(boardInfo)
                // end create
                adapter.notifyItemInserted(adapter.itemCount)
            }
            setNeutralButton(getString(R.string.cancel), null)
        }.create()
    }
}