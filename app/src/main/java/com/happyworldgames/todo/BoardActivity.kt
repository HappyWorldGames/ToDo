package com.happyworldgames.todo

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.happyworldgames.todo.core.Data

class BoardActivity : AppCompatActivity() {

    companion object {
        const val InputBoardName = "BoardName"
    }

    private val boardName by lazy { intent.getStringExtra(InputBoardName)?:"" }
    private val boardData by lazy{ Data(this as Context) }
    private val boardInfo by lazy { boardData.loadBoardInfo(boardName) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}