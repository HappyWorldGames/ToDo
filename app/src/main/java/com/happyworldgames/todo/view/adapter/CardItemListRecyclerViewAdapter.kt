package com.happyworldgames.todo.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.happyworldgames.todo.R
import com.happyworldgames.todo.core.BoardInfo
import com.happyworldgames.todo.core.Data
import com.happyworldgames.todo.databinding.CardItemCheckboxListBinding
import com.happyworldgames.todo.databinding.CardItemEditTextBinding

class CardItemListRecyclerViewAdapter(private val boardData: Data, private val boardInfo: BoardInfo, private val cardListPosition: Int, private val cardPosition: Int) : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val EDIT_TEXT_VIEW_TYPE = R.layout.card_item_edit_text
        const val CHECK_LIST_VIEW_TYPE = R.layout.card_item_checkbox_list
    }

    private val cardInfo by lazy { boardInfo.cardListInfoList[cardListPosition].cardInfoList[cardPosition] }

    private val cardItemListData = arrayListOf(
        "",
        cardInfo.checkBoxList
    )

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 -> EDIT_TEXT_VIEW_TYPE
            1 -> CHECK_LIST_VIEW_TYPE
            else -> throw Throwable("Unknown type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            EDIT_TEXT_VIEW_TYPE -> EditTextViewHolder(itemView)
            CHECK_LIST_VIEW_TYPE -> CheckListViewHolder(itemView)
            else -> throw Throwable("Unknown type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is EditTextViewHolder -> {
                //TODO()
            }
            is CheckListViewHolder -> {
                //TODO()
            }
        }
    }

    override fun getItemCount(): Int = cardItemListData.size

    class EditTextViewHolder(itemView: View) : ViewHolder(itemView) {
        val cardItemEditTextBinding = CardItemEditTextBinding.bind(itemView)
    }
    class CheckListViewHolder(itemView: View) : ViewHolder(itemView) {
        val cardItemCheckboxListBinding = CardItemCheckboxListBinding.bind(itemView)
    }
}