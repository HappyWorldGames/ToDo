package com.happyworldgames.todo

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.happyworldgames.todo.core.Data
import com.happyworldgames.todo.databinding.ActivityMainBinding
import com.happyworldgames.todo.view.adapter.MainRecyclerViewAdapter

class MainActivity : AppCompatActivity() {

    private val activityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val boardData by lazy { Data(this as Context) }
    private val mainRecyclerViewAdapter by lazy { MainRecyclerViewAdapter(boardData) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        activityMainBinding.mainRecyclerView.layoutManager = LinearLayoutManager(this as Context)
        activityMainBinding.mainRecyclerView.adapter = mainRecyclerViewAdapter

        activityMainBinding.addFloatingActionButton.setOnClickListener {
            val titleEditName = EditText(this as Context).apply {
                hint = getString(R.string.board_name)
            }
            val alertDialog = AlertDialog.Builder(this as Context).apply {
                title = getString(R.string.create)
                setView(titleEditName)
                setPositiveButton(getString(R.string.create)){ _, _ -> }
                setNegativeButton(getString(R.string.cancel)){ _, _ ->}
            }.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener positiveButton@{
                val titleText = titleEditName.text.toString()

                when {
                    titleText.isBlank() -> titleEditName.error = getString(R.string.blank_text)
                    boardData.existsBoard(titleText) -> titleEditName.error = getString(R.string.exists_text)
                }
                if (titleText.isBlank() || boardData.existsBoard(titleText)) return@positiveButton

                boardData.createBoardInfo(titleText)
                mainRecyclerViewAdapter.notifyItemInserted(mainRecyclerViewAdapter.itemCount)
                alertDialog.dismiss()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 1, 0, "Delete")?.setIcon(android.R.drawable.ic_menu_edit)?.setShowAsAction(1)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 1) {
            mainRecyclerViewAdapter.deleteModeChange(!mainRecyclerViewAdapter.isDeleteMode())
            activityMainBinding.addFloatingActionButton.visibility = if (mainRecyclerViewAdapter.isDeleteMode()) View.GONE else View.VISIBLE
            item.setIcon(if (mainRecyclerViewAdapter.isDeleteMode()) android.R.drawable.ic_menu_save else android.R.drawable.ic_menu_edit)
            return true
        }
        return false
    }
}