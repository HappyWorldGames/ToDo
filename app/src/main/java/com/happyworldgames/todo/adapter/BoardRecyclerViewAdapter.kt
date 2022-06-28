package com.happyworldgames.todo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.happyworldgames.todo.R
import com.happyworldgames.todo.databinding.*
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.CardInfo
import com.happyworldgames.todo.model.DataInterface
import com.happyworldgames.todo.model.ListInfo
import com.happyworldgames.todo.actionmode.SupportActionModeForEditText
import java.util.*

class BoardRecyclerViewAdapter(
    private val appCompatActivity: AppCompatActivity,
    private val dataInterface: DataInterface,
    private val boardInfo: BoardInfo
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val context = appCompatActivity as Context

    private var isAddListEditText = false   // flag for Add List
    private var isAddCardEditText = false   // flag for Add Card

    override fun getItemViewType(position: Int): Int {
        return if(position != itemCount - 1) R.layout.activity_board_item_list
        else{
            if(!isAddListEditText) R.layout.activity_board_item_add
            else R.layout.activity_board_item_add_edit_text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when(viewType) {
            R.layout.activity_board_item_list -> ListViewHolder(view)
            R.layout.activity_board_item_add -> AddViewHolder(view)
            else -> AddEditTextViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position == itemCount - 1){
            if(!isAddListEditText) {
                val activityBoardItemAddBinding = (holder as AddViewHolder).mainView
                activityBoardItemAddBinding.button.setOnClickListener {
                    isAddListEditText = true
                    notifyItemChanged(position)
                }
            }else {
                val activityBoardItemAddEditTextViewHolder = (holder as AddEditTextViewHolder).mainView
                activityBoardItemAddEditTextViewHolder.root.translationX = 0f       // fix move edittext

                val listInfo = ListInfo(UUID.randomUUID().toString(), position, "")
                val supportActionModeForEditText = SupportActionModeForEditText(
                    context.getString(R.string.create_list),
                    context.getString(R.string.create),
                    context.getString(R.string.blank_text),
                    activityBoardItemAddEditTextViewHolder.editText,
                    fun(data: String){ listInfo.name = data },
                    fun (): String = listInfo.name,
                    fun () {
                        boardInfo.lists.add(listInfo)
                        dataInterface.saveList(boardInfo, listInfo)
                        notifyItemInserted(itemCount)
                    }
                )
                activityBoardItemAddEditTextViewHolder.editText.setOnFocusChangeListener { view, hasFocus ->
                    view.post {
                        supportActionModeForEditText.onFocusChangeListener(
                            appCompatActivity, view, hasFocus
                        )
                        if(!hasFocus) {
                            isAddListEditText = false
                            notifyItemChanged(position)
                        }
                    }
                }
                activityBoardItemAddEditTextViewHolder.editText.setOnKeyListener { _, keyCode, keyEvent ->
                    supportActionModeForEditText.onKeyListener(keyCode, keyEvent)
                }
                activityBoardItemAddEditTextViewHolder.editText.requestFocus()
                SupportActionModeForEditText.softKeyBoard(
                    activityBoardItemAddEditTextViewHolder.editText, true
                )
            }
        }else {
            val context = holder.itemView.context
            val listInfo = boardInfo.lists[position]
            val activityBoardItemListBinding = (holder as ListViewHolder).mainView

            activityBoardItemListBinding.materialToolbar.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId){
                    1 -> {
                        val listInfoDel = boardInfo.lists.removeAt(position)
                        notifyItemRemoved(position)
                        dataInterface.deleteList(boardInfo.id, listInfoDel.id)
                        true
                    }
                    else -> false
                }
            }

            val supportActionModeForEditTextZero = SupportActionModeForEditText(
                context.getString(R.string.edit_list_name),
                context.getString(R.string.save),
                context.getString(R.string.blank_text),
                activityBoardItemListBinding.listName,
                fun(data: String){ listInfo.name = data },
                fun (): String = listInfo.name,
                fun () {
                    dataInterface.saveList(boardInfo, listInfo)
                }
            )
            activityBoardItemListBinding.listName.setText(if(listInfo.name.length > 20)
                listInfo.name.subSequence(0, 20) else listInfo.name)
            activityBoardItemListBinding.listName.setOnFocusChangeListener { view, hasFocus ->
                supportActionModeForEditTextZero.onFocusChangeListener(
                    appCompatActivity, view, hasFocus
                )
            }
            activityBoardItemListBinding.listName.setOnKeyListener { _, keyCode, keyEvent ->
                supportActionModeForEditTextZero.onKeyListener(keyCode, keyEvent)
            }

            val cardAdapter = CardRecyclerViewAdapter(dataInterface, boardInfo, position)
            activityBoardItemListBinding.listRecyclerView.layoutManager = LinearLayoutManager(context)
            activityBoardItemListBinding.listRecyclerView.adapter = cardAdapter

            if(!isAddCardEditText) {
                val addCardLayout = ActivityBoardItemListAddButtonBinding.inflate(LayoutInflater.from(context))
                activityBoardItemListBinding.bottomFragment.addView(addCardLayout.root)
                addCardLayout.addCard.setOnClickListener {
                    isAddCardEditText = true
                    activityBoardItemListBinding.bottomFragment.removeAllViews()
                    //TODO notifyItemChanged(position)
                    onBindViewHolder(holder, position)
                }
            }else {
                val addEditTextLayout = ActivityBoardItemListAddEditTextBinding
                    .inflate(LayoutInflater.from(context))
                activityBoardItemListBinding.bottomFragment.addView(addEditTextLayout.root)

                val cardInfo = CardInfo(
                    position = cardAdapter.itemCount
                )
                val supportActionModeForEditText = SupportActionModeForEditText(
                    context.getString(R.string.create_card),
                    context.getString(R.string.create),
                    context.getString(R.string.blank_text),
                    addEditTextLayout.addEditText,
                    fun(data: String){ cardInfo.name = data },
                    fun (): String = cardInfo.name,
                    fun () {
                        listInfo.cards.add(cardInfo)
                        dataInterface.saveCard(boardInfo.id, listInfo.id, cardInfo)
                        cardAdapter.notifyItemInserted(cardAdapter.itemCount)
                    }
                )

                addEditTextLayout.addEditText.setOnFocusChangeListener { view, hasFocus ->
                    supportActionModeForEditText.onFocusChangeListener(
                        appCompatActivity, view, hasFocus
                    )
                    if(!hasFocus) {
                        isAddCardEditText = false
                        activityBoardItemListBinding.bottomFragment.removeAllViews()
                        //TODO notifyItemChanged(position)
                        onBindViewHolder(holder, position)
                    }
                }
                addEditTextLayout.addEditText.setOnKeyListener { _, keyCode, keyEvent ->
                    supportActionModeForEditText.onKeyListener(keyCode, keyEvent)
                }
                addEditTextLayout.addEditText.requestFocus()
                SupportActionModeForEditText.softKeyBoard(
                    addEditTextLayout.addEditText, true
                )
            }
        }
    }

    override fun getItemCount(): Int = boardInfo.lists.size + 1

    class AddViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainView = ActivityBoardItemAddBinding.bind(view)
    }
    class AddEditTextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainView = ActivityBoardItemAddEditTextBinding.bind(view)
    }
    class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainView = ActivityBoardItemListBinding.bind(view)

        init {
            mainView.materialToolbar.menu.add(0, 1, 0, view.context.getString(R.string.delete))
        }
    }

}