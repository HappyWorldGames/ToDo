package com.happyworldgames.todo.view

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.view.ActionMode
import com.happyworldgames.todo.R

class SupportActionModeForEditText(
    private val context: Context,
    private val editTextId: Int,
    private val editText: EditText,
    private val setData: (data: String) -> Unit,
    private val getData: () -> String,
    private val runSave: () -> Unit
) : ActionMode.Callback {

    companion object {
        var actionMode: ActionMode? = null
    }

    private val saveID = 2

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        if(mode == null) return false
        menu?.add(0, saveID, 0, context.getString(R.string.save))
        mode.title = context.getString(editTextId)
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
                runSave()
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