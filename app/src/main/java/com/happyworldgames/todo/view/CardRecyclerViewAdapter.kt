package com.happyworldgames.todo.view

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.happyworldgames.todo.R
import com.happyworldgames.todo.databinding.ActivityBoardItemCardBinding
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.DataInterface

class CardRecyclerViewAdapter(private val dataInterface: DataInterface, private val boardInfo: BoardInfo,
                              private val posList: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_board_item_card, parent,
            false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cardInfo = boardInfo.lists[posList].cards[position]
        val activityBoardItemCardBinding = (holder as MainViewHolder).mainView

        activityBoardItemCardBinding.textView.text = cardInfo.name

        activityBoardItemCardBinding.root.setOnClickListener {
            val intent = Intent(activityBoardItemCardBinding.root.context, CardActivity::class.java)
                .apply {
                putExtra("id_board", boardInfo.id)
                putExtra("pos_list", posList)
                putExtra("pos_card", position)
            }
            activityBoardItemCardBinding.root.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = boardInfo.lists[posList].cards.size

    class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainView = ActivityBoardItemCardBinding.bind(view)
    }
}