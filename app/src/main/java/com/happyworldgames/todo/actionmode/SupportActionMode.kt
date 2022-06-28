package com.happyworldgames.todo.actionmode

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode

open class SupportActionMode(
    private val titleText: String,
    private val doneButtonText: String = NO_DONE_BUTTON             // if doneButtonText == NO_DONE_BUTTON then no done button
    ) : ActionMode.Callback {

    companion object {
        const val SAVE_ID = 12

        const val NO_DONE_BUTTON = ""
    }

    protected var actionMode: ActionMode? = null                                        // for save created actionMode

    private var onClickDone: (() -> Unit)? = null
    private var onDestroyActionMode: (() -> Unit)? = null

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        if(mode == null) return false
        mode.title = titleText                          // set title
        if(doneButtonText != NO_DONE_BUTTON) menu?.add(0, SAVE_ID, 0, doneButtonText)        // add done button
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if(item == null) return false
        return when(item.itemId){
            SAVE_ID -> {         // if clicked done button
                onClickDone?.let { it() }
                true
            }
            else -> false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        onDestroyActionMode?.let { it() }
        actionMode = null                                   // clear actionMode
    }

    fun setOnClickDone(onClick: () -> Unit) {
        onClickDone = onClick
    }
    fun setOnDestroyActionMode(onDestroy: () -> Unit) {
        onDestroyActionMode = onDestroy
    }

    fun startActionMode(appCompatActivity: AppCompatActivity) {
        actionMode = appCompatActivity.startSupportActionMode(this)                    // show action mode
    }

}