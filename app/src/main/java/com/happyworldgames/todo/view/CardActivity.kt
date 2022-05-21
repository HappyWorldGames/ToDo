package com.happyworldgames.todo.view

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import com.happyworldgames.todo.R
import com.happyworldgames.todo.databinding.ActivityCardBinding
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.DataInterface

class CardActivity : AppCompatActivity() {
    private val activityCardBinding by lazy { ActivityCardBinding.inflate(layoutInflater) }

    private val cardInfo by lazy { boardInfo.lists[posList].cards[posCard] }
    private val boardInfo by lazy{BoardInfo.getBoardInfo(this, intent.getStringExtra("id_board")!!) }
    private val posList by lazy { intent.getIntExtra("pos_list", -1) }
    private val posCard by lazy { intent.getIntExtra("pos_card", -1) }

    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityCardBinding.root)
        title = ""

        activityCardBinding.titleName.setText(cardInfo.name)
        activityCardBinding.titleName.setOnFocusChangeListener { view, b ->
            if(actionMode == null && b) actionMode = startSupportActionMode(
                SupportActionMode(this, 0, activityCardBinding.titleName))
            (view as EditText).isCursorVisible = b
        }

        activityCardBinding.description.setText(cardInfo.description)
        activityCardBinding.description.setOnFocusChangeListener { view, b ->
            if(actionMode == null && b) actionMode = startSupportActionMode(
                SupportActionMode(this, 1, activityCardBinding.description))
            (view as EditText).isCursorVisible = b
        }
    }

    inner class SupportActionMode(private val context: Context, private val type: Int,
                                  private val editText: EditText) : ActionMode.Callback {
        private val saveID = 2

        private val editTextIdsArray = arrayOf(
            R.string.edit_title_name,
            R.string.edit_description
        )

        private fun setData(data: String) {
            when(type){
                0 -> cardInfo.name = data
                1 -> cardInfo.description = data
            }
        }
        private fun getData(): String {
            return when(type){
                0 -> cardInfo.name
                1 -> cardInfo.description
                else -> ""
            }
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            if(mode == null) return false
            //mode.menuInflater.inflate(R.menu., menu)
            menu?.add(0, saveID, 0, context.getString(R.string.save))
            mode.title = context.getString(editTextIdsArray[type])
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            if(item == null) return false
            return when(item.itemId){
                saveID -> {
                    setData(editText.text.toString())
                    DataInterface.getDataInterface(context)
                        .saveCard(boardInfo.id, boardInfo.lists[posList].id, cardInfo)
                    destroy()
                    mode?.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            editText.setText(getData())
            destroy()
        }

        private fun destroy() {
            actionMode = null
            editText.clearFocus()
        }
    }
}