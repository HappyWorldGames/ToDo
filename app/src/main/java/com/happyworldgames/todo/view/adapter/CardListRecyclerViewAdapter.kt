package com.happyworldgames.todo.view.adapter

import android.content.Intent
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.happyworldgames.todo.CardActivity
import com.happyworldgames.todo.R
import com.happyworldgames.todo.core.BoardInfo
import com.happyworldgames.todo.core.CardInfo
import com.happyworldgames.todo.core.Data
import com.happyworldgames.todo.databinding.AddCardListAdapterItemBinding
import com.happyworldgames.todo.databinding.AddNameCardListAdapterItemBinding
import com.happyworldgames.todo.databinding.CardListAdapterItemBinding

class CardListRecyclerViewAdapter(private val boardData: Data, private val boardInfo: BoardInfo, private val cardListPosition: Int) : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val ADD_TYPE = R.layout.add_card_list_adapter_item
        const val ADD_NAME_TYPE = R.layout.add_name_card_list_adapter_item
        const val VIEW_TYPE = R.layout.card_list_adapter_item
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
        val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            ADD_TYPE -> AddCardListViewHolder(itemView)
            ADD_NAME_TYPE -> AddNameCardListViewHolder(itemView)
            VIEW_TYPE -> CardListViewHolder(itemView)
            else -> throw Throwable("Unknown type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is AddCardListViewHolder -> {
                holder.addCardListAdapterItemBinding.addCardItemButton.setOnClickListener {
                    isAddEditListName = true
                    notifyItemChanged(position)
                }
            }
            is AddNameCardListViewHolder -> {
                holder.addNameCardListAdapterItemBinding.cardNameEditText.apply {
                    post {
                        requestFocus()
                    }
                    setOnKeyListener { view, keyCode, keyEvent ->
                        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                            val listNameField = view as EditText
                            val listName = listNameField.text.toString()
                            val resources = view.resources

                            if (listName.isBlank()) listNameField.error =
                                resources.getString(R.string.blank_text)
                            cardList.forEach {
                                if (listName == it.title) {
                                    listNameField.error = resources.getString(R.string.exists_text)
                                    return@forEach
                                }
                            }
                            if (listNameField.error.isNullOrBlank()) {
                                cardList.add(position, CardInfo(position, listName, ""))
                                isAddEditListName = false
                                listNameField.setText("")
                                notifyItemInserted(position)

                                boardData.saveBoardInfo(boardInfo)
                            }
                            return@setOnKeyListener true
                        }
                        false
                    }
                }
            }
            is CardListViewHolder -> {
                holder.cardListAdapterItemBinding.cardTitle.text = cardList[position].title
                holder.cardListAdapterItemBinding.cardBackground.setOnClickListener {
                    val context = holder.itemView.context
                    val intent = Intent(context, CardActivity::class.java).apply {
                        putExtra(CardActivity.INPUT_BOARD_NAME, boardInfo.title)
                        putExtra(CardActivity.CARD_LIST_POSITION, cardListPosition)
                        putExtra(CardActivity.CARD_POSITION, position)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int = cardList.size + 1

    class AddCardListViewHolder(itemView: View) : ViewHolder(itemView) {
        val addCardListAdapterItemBinding = AddCardListAdapterItemBinding.bind(itemView)
    }
    class AddNameCardListViewHolder(itemView: View) : ViewHolder(itemView) {
        val addNameCardListAdapterItemBinding = AddNameCardListAdapterItemBinding.bind(itemView)
    }
    class CardListViewHolder(itemView: View) : ViewHolder(itemView) {
        val cardListAdapterItemBinding = CardListAdapterItemBinding.bind(itemView)
    }
}