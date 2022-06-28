package com.happyworldgames.todo.actionmode

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener

/*
    For easy use ActionMode on EditText
 */
class SupportActionModeForEditText(
    titleText: String,
    doneText: String,
    private val blankText: String,                   // if == NO_BLANK_CHECK, no check

    private val editText: EditText,
    private val setData: (data: String) -> Unit,    // save text from edittext
    private val getData: () -> String,              // for set no edit data in edittext
    private val runSave: () -> Unit                 // save edited data
) : SupportActionMode(titleText, doneText) {

    companion object {
        const val NO_BLANK_CHECK = ""

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

    init {
        if(blankText != NO_BLANK_CHECK) editText.addTextChangedListener {
            if(editText.text.isNotBlank()) editText.error = null
        }
        setOnClickDone {
            doneDo()
        }
        setOnDestroyActionMode {
            if(editText.text.toString() != getData()) editText.setText(getData())
            destroy()
        }
    }

    /*
        Function need use onFocusChangeListener in edittext focus change
    */
    fun onFocusChangeListener(appCompatActivity: AppCompatActivity, view: View, hasFocus: Boolean) {
        if(hasFocus) startActionMode(appCompatActivity)         // show action mode

        (view as EditText).isCursorVisible = hasFocus
        if(!hasFocus) actionMode?.finish()                     // close action mode if has`t focus
    }

    private fun doneDo() {
        if(blankText != NO_BLANK_CHECK && editText.text.isBlank()) {
            editText.error = blankText
            return
        }
        setData(editText.text.toString().trim())
        runSave()
        actionMode?.finish()  // close action mode
    }

    private fun destroy() {
        editText.clearFocus()
        softKeyBoard(editText, false)       // hide soft keyboard
    }

    fun onKeyListener(keyCode: Int, event: KeyEvent): Boolean {
        if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            doneDo()
            return true
        }
        return false
    }

}