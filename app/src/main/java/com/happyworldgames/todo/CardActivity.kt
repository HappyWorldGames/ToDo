package com.happyworldgames.todo

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.happyworldgames.todo.core.Data
import com.happyworldgames.todo.databinding.ActivityCardBinding
import com.happyworldgames.todo.view.adapter.CardItemListRecyclerViewAdapter

class CardActivity : AppCompatActivity() {

    companion object {
        const val INPUT_BOARD_NAME = "BoardName"
        const val CARD_LIST_POSITION = "cardListPosition"
        const val CARD_POSITION = "cardPosition"
    }

    private val activityCardBinding by lazy { ActivityCardBinding.inflate(layoutInflater) }

    private val boardName by lazy { intent.getStringExtra(INPUT_BOARD_NAME)?:"" }
    private val cardListPosition by lazy { intent.getIntExtra(CARD_LIST_POSITION, 0) }
    private val cardPosition by lazy { intent.getIntExtra(CARD_POSITION, 0) }

    private val boardData by lazy { Data(this as Context) }
    private val boardInfo by lazy { boardData.loadBoardInfo(boardName) }

    private val cardItemListRecyclerViewAdapter by lazy { CardItemListRecyclerViewAdapter(boardData, boardInfo, cardListPosition, cardPosition) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityCardBinding.root)

        activityCardBinding.cardItems.apply {
            layoutManager = LinearLayoutManager(this@CardActivity)
            adapter = cardItemListRecyclerViewAdapter
        }
    }
}