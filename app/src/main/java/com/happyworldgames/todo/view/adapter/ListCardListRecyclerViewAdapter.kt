package com.happyworldgames.todo.view.adapter

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.happyworldgames.todo.R
import com.happyworldgames.todo.core.BoardInfo
import com.happyworldgames.todo.core.CardInfo
import com.happyworldgames.todo.core.CardListInfo
import com.happyworldgames.todo.core.Data
import com.happyworldgames.todo.databinding.CardListAddRecyclerViewAdapterItemBinding
import com.happyworldgames.todo.databinding.CardListFieldRecyclerViewAdapterItemBinding
import com.happyworldgames.todo.databinding.CardListRecyclerViewAdapterItemBinding

class ListCardListRecyclerViewAdapter(private val boardData: Data, private val boardInfo: BoardInfo) : Adapter<ViewHolder>() {

    companion object {
        const val ADD_TYPE = R.layout.card_list_add_recycler_view_adapter_item
        const val ADD_NAME_TYPE = R.layout.card_list_field_recycler_view_adapter_item
        const val VIEW_TYPE = R.layout.card_list_recycler_view_adapter_item
    }

    private val cardListInfoList: ArrayList<CardListInfo> get() = boardInfo.cardListInfoList
    private lateinit var recyclerView: RecyclerView

    var isAddEditListName = false
    var editPos = -1

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        recyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (isAddEditListName && newState == SCROLL_STATE_DRAGGING) {
                    isAddEditListName = false
                    notifyItemChanged(itemCount - 1)
                }
            }
        })
    }

    override fun getItemViewType(position: Int): Int = when (position) {
        itemCount - 1 -> when {
            isAddEditListName -> ADD_NAME_TYPE
            else -> ADD_TYPE
        }
        else -> VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        setLayoutParams(itemView)
        return when (viewType) {
            ADD_TYPE -> CardListAddViewHolder(itemView)
            ADD_NAME_TYPE -> CardListFieldViewHolder(itemView)
            else -> CardListViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is CardListAddViewHolder -> {
                holder.cardListAddRecyclerViewAdapterItemBinding.addListButton.setOnClickListener {
                    isAddEditListName = true
                    notifyItemChanged(position)
                }
            }
            is CardListFieldViewHolder -> {
                holder.cardListFieldRecyclerViewAdapterItemBinding.listNameEditText.apply {
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
                            cardListInfoList.forEach {
                                if (listName == it.title) {
                                    listNameField.error = resources.getString(R.string.exists_text)
                                    return@forEach
                                }
                            }
                            if (listNameField.error.isNullOrBlank()) {
                                cardListInfoList.add(position, CardListInfo(position, listName))
                                isAddEditListName = false
                                listNameField.setText("")
                                notifyItemInserted(position)
                                recyclerView.smoothScrollToPosition(position)

                                boardData.saveBoardInfo(boardInfo)
                            }
                            return@setOnKeyListener true
                        }
                        false
                    }
                }
            }
            is CardListViewHolder -> {
                val disableEditText = fun(view: View) {
                    if (editPos == position) editPos = -1

                    view.visibility = View.GONE
                    holder.cardListRecyclerViewAdapterItemBinding.listTitleName.visibility = View.VISIBLE
                }
                val enableEditText = fun(view: View) {
                    editPos = position

                    view.visibility = View.GONE
                    holder.cardListRecyclerViewAdapterItemBinding.listTitleEditName.visibility = View.VISIBLE
                }
                holder.cardListRecyclerViewAdapterItemBinding.listTitleName.apply {
                    text = cardListInfoList[position].title
                    setOnClickListener { view ->
                        enableEditText(view)

                        holder.cardListRecyclerViewAdapterItemBinding.listTitleEditName.setText(cardListInfoList[position].title)
                        holder.cardListRecyclerViewAdapterItemBinding.listTitleEditName.post {
                            holder.cardListRecyclerViewAdapterItemBinding.listTitleEditName.requestFocus()
                        }
                    }
                }
                holder.cardListRecyclerViewAdapterItemBinding.listTitleEditName.apply {
                    setOnFocusChangeListener { view, isFocus ->
                        if (!isFocus) {
                            disableEditText(view)
                        }
                    }
                    setOnKeyListener { view, keyCode, keyEvent ->
                        if (keyEvent.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                            val newTitle = text.toString()

                            if (newTitle.isBlank()){
                                error = context.getString(R.string.blank_text)
                                return@setOnKeyListener false
                            }else cardListInfoList.forEach {
                                if (newTitle == it.title && newTitle != cardListInfoList[position].title) {
                                    error = context.getString(R.string.exists_text)
                                    return@setOnKeyListener false
                                }
                            }

                            cardListInfoList[position].title = newTitle

                            disableEditText(view)
                            return@setOnKeyListener true
                        }
                        false
                    }
                }

                holder.cardListRecyclerViewAdapterItemBinding.listToolbar.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.deleteItem -> {
                            AlertDialog.Builder(holder.itemView.context).apply {
                                setTitle(R.string.delete)
                                setMessage(cardListInfoList[position].title)
                                setPositiveButton(R.string.delete) { _, _ ->
                                    cardListInfoList.removeAt(position)
                                    notifyItemRemoved(position)

                                    boardData.saveBoardInfo(boardInfo)
                                }
                                setNegativeButton(R.string.cancel, null)
                            }.show()
                            true
                        }
                        else -> false
                    }
                }

                val cardListRecyclerViewAdapter = CardListRecyclerViewAdapter(boardData, boardInfo, position) // TODO Adapter
                holder.cardListRecyclerViewAdapterItemBinding.cardListRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = cardListRecyclerViewAdapter
                }
            }
        }
    }

    override fun getItemCount(): Int = cardListInfoList.size + 1

    private fun setLayoutParams(itemView: View) {
        val screenWidth = itemView.resources.displayMetrics.widthPixels
        val itemMargin = (screenWidth * 0.12f).toInt()
        val currentItemWidth: Int = screenWidth - itemMargin * 2

        val height: Int = itemView.layoutParams.height
        itemView.layoutParams = ViewGroup.LayoutParams(currentItemWidth, height)
    }

    class CardListAddViewHolder(itemView: View) : ViewHolder(itemView) {
        val cardListAddRecyclerViewAdapterItemBinding = CardListAddRecyclerViewAdapterItemBinding.bind(itemView)
    }
    class CardListFieldViewHolder(itemView: View) : ViewHolder(itemView) {
        val cardListFieldRecyclerViewAdapterItemBinding = CardListFieldRecyclerViewAdapterItemBinding.bind(itemView)
    }
    class CardListViewHolder(itemView: View) : ViewHolder(itemView) {
        val cardListRecyclerViewAdapterItemBinding = CardListRecyclerViewAdapterItemBinding.bind(itemView)
    }
}