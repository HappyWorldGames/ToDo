package com.happyworldgames.todo.view

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode

/*
    For easy use ActionMode on EditText
 */
class SupportActionModeForEditText(
    private val editTextId: Int,                    // resources id for getString(editTextId)
    private val doneTextId: Int,                    // resources id for getString(doneTextId)
    private val editText: EditText,
    private val setData: (data: String) -> Unit,    // save text from edittext
    private val getData: () -> String,              // for set no edit data in edittext
    private val runSave: () -> Unit                 // save edited data
) : ActionMode.Callback {
    companion object {
        private var actionMode: ActionMode? = null  // use for block create new and clone haven`t

        /*
            Function need use onFocusChangeListener in edittext focus change
         */
        fun onFocusChangeListener(appCompatActivity: AppCompatActivity, view: View, hasFocus: Boolean,
                                  actionModeCallBack: ActionMode.Callback) {
            if(actionMode == null && hasFocus)
                actionMode = appCompatActivity.startSupportActionMode(actionModeCallBack)   // show action mode
            (view as EditText).isCursorVisible = hasFocus
            if(!hasFocus) actionMode?.finish()                     // close action mode if has`t focus
        }

        fun onKeyListener(runSave: () -> Unit) {
            TODO("Обработка enter кнопки, done")
            runSave()
        }
        /*
            Show or close soft keyboard
         */
        fun softKeyBoard(view: View, show: Boolean) {
            view.post{
                val imm: InputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager   // init InputMethodManager
                if(show) imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)  // show
                else imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS) // hide
            }
        }
    }
    private val context = editText.context
    private val saveID = 2                          // id save button from menu

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        if(mode == null) return false
        menu?.add(0, saveID, 0, context.getString(doneTextId))  // add done button
        mode.title = context.getString(editTextId)              // set title
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if(item == null) return false
        return when(item.itemId){
            saveID -> {         // if clicked done button
                setData(editText.text.toString())
                runSave()
                destroy()
                mode?.finish()  // close action mode
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
        actionMode = null                         // unlock action mode
        editText.clearFocus()
        softKeyBoard(editText, false)       // hide soft keyboard
    }
}