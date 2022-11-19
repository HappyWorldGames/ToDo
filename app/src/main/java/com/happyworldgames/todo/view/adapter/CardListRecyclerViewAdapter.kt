package com.happyworldgames.todo.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.happyworldgames.todo.R
import com.happyworldgames.todo.core.BoardInfo
import com.happyworldgames.todo.core.CardInfo
import com.happyworldgames.todo.core.Data

class CardListRecyclerViewAdapter(private val boardData: Data, private val boardInfo: BoardInfo, private val cardListPosition: Int) : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val ADD_TYPE = R.layout.card_list_add_recycler_view_adapter_item
        const val ADD_NAME_TYPE = R.layout.card_list_field_recycler_view_adapter_item
        const val VIEW_TYPE = R.layout.card_list_recycler_view_adapter_item
    }

    private val cardList: ArrayList<CardInfo> get() = boardInfo.cardListInfoList[cardListPosition].cardInfoList

    private var isAddEditListName = false

    override fun getItemViewType(position: Int): Int = when (position) {
        itemCount - 1 -> when {
            isAddEditListName -> ADD_NAME_TYPE
            else -> ADD_TYPE
        }
        else -> VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = cardList.size + 1
}