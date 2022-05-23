package com.happyworldgames.todo.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.happyworldgames.todo.R
import com.happyworldgames.todo.databinding.ActivityBoardItemAddBinding
import com.happyworldgames.todo.databinding.ActivityBoardItemAddEditTextBinding
import com.happyworldgames.todo.databinding.ActivityBoardItemListBinding
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.CardInfo
import com.happyworldgames.todo.model.DataInterface
import com.happyworldgames.todo.model.ListInfo
import java.util.UUID

class BoardRecyclerViewAdapter(private val appCompatActivity: AppCompatActivity,
                               private val dataInterface: DataInterface,
                               private val boardInfo: BoardInfo
                               ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isAddEditText = false

    override fun getItemViewType(position: Int): Int {
        return if(position != itemCount - 1) R.layout.activity_board_item_list
        else if(!isAddEditText) R.layout.activity_board_item_add else R.layout.activity_board_item_add_edit_text
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
            if(!isAddEditText) {
                val activityBoardItemAddBinding = (holder as AddViewHolder).mainView
                val alert = createAlert(holder.mainView.root.context)
                activityBoardItemAddBinding.button.setOnClickListener {
                    //alert.show()
                    isAddEditText = true
                    notifyItemChanged(position)
                }
            }else {
                val listInfo = ListInfo(UUID.randomUUID().toString(), position, "")
                val activityBoardItemAddEditTextViewHolder = (holder as AddEditTextViewHolder).mainView
                activityBoardItemAddEditTextViewHolder.editText.setOnFocusChangeListener { view, hasFocus ->
                    SupportActionModeForEditText.onFocusChangeListener(appCompatActivity, view, hasFocus,
                        SupportActionModeForEditText(
                            R.string.create_list,
                            R.string.create,
                            activityBoardItemAddEditTextViewHolder.editText,
                            fun(data: String){ listInfo.name = data },
                            fun (): String = listInfo.name,
                            fun () {
                                boardInfo.lists.add(listInfo)
                                DataInterface.getDataInterface(
                                    activityBoardItemAddEditTextViewHolder.root.context
                                ).saveList(boardInfo, listInfo)
                                notifyItemInserted(itemCount)
                            }
                        )
                    )
                    if(!hasFocus) {
                        isAddEditText = false
                        notifyItemChanged(position)
                    }
                }
                activityBoardItemAddEditTextViewHolder.editText.requestFocus()
                SupportActionModeForEditText.softKeyBoard(
                    activityBoardItemAddEditTextViewHolder.editText, true
                )
            }
            return
        }

        val context = holder.itemView.context
        val listInfo = boardInfo.lists[position]
        val activityBoardItemListBinding = (holder as ListViewHolder).mainView
        activityBoardItemListBinding.listName.setText(if(listInfo.name.length > 20)
            listInfo.name.subSequence(0, 20) else listInfo.name)
        activityBoardItemListBinding.listName.setOnFocusChangeListener { view, hasFocus ->
            SupportActionModeForEditText.onFocusChangeListener(appCompatActivity, view, hasFocus,
                SupportActionModeForEditText(
                    R.string.edit_list_name,
                    R.string.save,
                    activityBoardItemListBinding.listName,
                    fun(data: String){ listInfo.name = data },
                    fun (): String = listInfo.name,
                    fun () {
                        DataInterface.getDataInterface(context).saveList(boardInfo, listInfo)
                    }
                )
            )
        }

        activityBoardItemListBinding.more.setOnClickListener {
            val popupMenu = PopupMenu(context, activityBoardItemListBinding.more)
            popupMenu.menu.add(0, 1, 0, context.getString(R.string.delete))
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    1 -> {
                        val listInfoDel = boardInfo.lists.removeAt(position)
                        notifyItemRemoved(position)
                        dataInterface.deleteList(boardInfo.id, listInfoDel.id)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        val cardAdapter = CardRecyclerViewAdapter(dataInterface, boardInfo, position)
        activityBoardItemListBinding.listRecyclerView.layoutManager = LinearLayoutManager(context)
        activityBoardItemListBinding.listRecyclerView.adapter = cardAdapter

        val alertCardFun = fun(context: Context): AlertDialog {
            val editTextName = EditText(context).apply {
                hint = context.getString(R.string.card_name)
            }
            return AlertDialog.Builder(context).apply {
                setTitle(context.getString(R.string.create_card))
                setView(editTextName)
                setPositiveButton(context.getString(R.string.create)){ _, _ ->
                    val cardInfo = CardInfo(UUID.randomUUID().toString(), cardAdapter.itemCount,
                        editTextName.text.toString(), "")
                    listInfo.cards.add(cardInfo)
                    dataInterface.saveCard(boardInfo.id, listInfo.id, cardInfo)
                    cardAdapter.notifyItemInserted(cardAdapter.itemCount)
                }
                setNeutralButton(context.getString(R.string.cancel), null)
            }.create()
        }
        val alertCard = alertCardFun(holder.itemView.context)
        activityBoardItemListBinding.addCard.setOnClickListener {
            alertCard.show()
        }
    }

    override fun getItemCount(): Int = boardInfo.lists.size + 1

    private fun createAlert(context: Context): AlertDialog {
        val editTextName = EditText(context).apply {
            hint = context.getString(R.string.list_name)
        }
        return AlertDialog.Builder(context).apply {
            setTitle(context.getString(R.string.create_list))
            setView(editTextName)
            setPositiveButton(context.getString(R.string.create)){ _, _ ->
                val listInfo = ListInfo(UUID.randomUUID().toString(), itemCount - 1,
                    editTextName.text.toString())
                boardInfo.lists.add(listInfo)
                dataInterface.saveList(boardInfo, listInfo)
                notifyItemInserted(itemCount - 1)
            }
            setNeutralButton(context.getString(R.string.cancel), null)
        }.create()
    }

    class AddViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainView = ActivityBoardItemAddBinding.bind(view)
    }
    class AddEditTextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainView = ActivityBoardItemAddEditTextBinding.bind(view)
    }
    class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainView = ActivityBoardItemListBinding.bind(view)
    }
}