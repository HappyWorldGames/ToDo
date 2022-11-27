package com.happyworldgames.todo

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.happyworldgames.todo.core.Data
import com.happyworldgames.todo.databinding.ActivityBoardBinding
import com.happyworldgames.todo.view.adapter.ListCardListRecyclerViewAdapter

class BoardActivity : AppCompatActivity() {

    companion object {
        const val INPUT_BOARD_NAME = "BoardName"
    }

    private val activityBoardBinding by lazy { ActivityBoardBinding.inflate(layoutInflater) }

    private val boardName by lazy { intent.getStringExtra(INPUT_BOARD_NAME)?:"" }
    private val boardData by lazy { Data(this as Context) }
    private val boardInfo by lazy { boardData.loadBoardInfo(boardName) }

    private val listCardListRecyclerViewAdapter by lazy { ListCardListRecyclerViewAdapter(boardData, boardInfo) }
    private val listCardListRecyclerViewLayoutManager by lazy { LinearLayoutManager(this as Context, RecyclerView.HORIZONTAL, false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityBoardBinding.root)

        activityBoardBinding.listCardListRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (RecyclerView.SCROLL_STATE_DRAGGING == newState) {
                    if (listCardListRecyclerViewAdapter.editPos >= 0) {
                        listCardListRecyclerViewAdapter.notifyItemChanged(listCardListRecyclerViewAdapter.editPos)
                        listCardListRecyclerViewAdapter.editPos = -1
                    }
                    if (listCardListRecyclerViewAdapter.isAddEditListName) {
                        listCardListRecyclerViewAdapter.isAddEditListName = false
                        listCardListRecyclerViewAdapter.notifyItemChanged(listCardListRecyclerViewAdapter.itemCount)
                    }
                }
            }
        })
        activityBoardBinding.listCardListRecyclerView.adapter = listCardListRecyclerViewAdapter
        activityBoardBinding.listCardListRecyclerView.layoutManager = listCardListRecyclerViewLayoutManager

        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(activityBoardBinding.listCardListRecyclerView)
    }
}