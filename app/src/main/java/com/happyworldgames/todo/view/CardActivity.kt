package com.happyworldgames.todo.view

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
    private val boardInfo by lazy{ BoardInfo.getBoardInfo(this, intent.getStringExtra("id_board")!!) }
    private val posList by lazy { intent.getIntExtra("pos_list", -1) }
    private val posCard by lazy { intent.getIntExtra("pos_card", -1) }

    private var actionMode: ActionMode? = null
    private val supportActionMode = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            if(mode == null) return false
            //mode.menuInflater.inflate(R.menu., menu)
            mode.title = "Test"
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
            activityCardBinding.description.clearFocus()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityCardBinding.root)
        title = cardInfo.name

        activityCardBinding.description.setText(cardInfo.description)
        activityCardBinding.description.isCursorVisible = false
        activityCardBinding.description.setOnFocusChangeListener { view, b ->
            if(actionMode == null && b) actionMode = startSupportActionMode(supportActionMode)
            (view as EditText).isCursorVisible = b
            cardInfo.description = view.text.toString()
            if(!b) DataInterface.getDataInterface(this).saveCard(boardInfo.id, boardInfo.lists[posList].id, cardInfo)
        }
    }
}