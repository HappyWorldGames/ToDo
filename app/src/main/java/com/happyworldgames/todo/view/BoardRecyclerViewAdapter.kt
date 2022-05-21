package com.happyworldgames.todo.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.happyworldgames.todo.R
import com.happyworldgames.todo.databinding.ActivityBoardItemAddBinding
import com.happyworldgames.todo.databinding.ActivityBoardItemListBinding
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.CardInfo
import com.happyworldgames.todo.model.DataInterface
import com.happyworldgames.todo.model.ListInfo
import java.util.UUID

class BoardRecyclerViewAdapter(private val dataInterface: DataInterface, private val boardInfo: BoardInfo) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if(position != itemCount - 1) R.layout.activity_board_item_list
                else R.layout.activity_board_item_add
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return if(viewType == R.layout.activity_board_item_list) ListViewHolder(view) else AddViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position == itemCount - 1){
            val activityBoardItemAddBinding = (holder as AddViewHolder).mainView
            val alert = createAlert(holder.mainView.root.context)
            activityBoardItemAddBinding.button.setOnClickListener {
                alert.show()
            }
            return
        }

        val context = holder.itemView.context
        val listInfo = boardInfo.lists[position]
        val activityBoardItemListBinding = (holder as ListViewHolder).mainView
        activityBoardItemListBinding.listName.setText(if(listInfo.name.length > 20)
            listInfo.name.subSequence(0, 20) else listInfo.name)

        val changeEditText: (editText: EditText, isEnabled: Boolean) -> Unit = { editText, isEnabled ->
            val color = editText.currentTextColor
            editText.isEnabled = isEnabled
            editText.setTextColor(color)
        }
        changeEditText(activityBoardItemListBinding.listName, false)
        activityBoardItemListBinding.listName.setOnClickListener { view ->
            changeEditText((view as EditText), true)
        }
        activityBoardItemListBinding.listName.setOnFocusChangeListener { view, b ->
            changeEditText((view as EditText), b)
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
    class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainView = ActivityBoardItemListBinding.bind(view)
    }
}