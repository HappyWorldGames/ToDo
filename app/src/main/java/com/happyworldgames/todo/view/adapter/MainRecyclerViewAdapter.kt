package com.happyworldgames.todo.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.happyworldgames.todo.BoardActivity
import com.happyworldgames.todo.R
import com.happyworldgames.todo.core.Data
import com.happyworldgames.todo.databinding.MainRecyclerViewAdapterItemBinding

class MainRecyclerViewAdapter(private val boardData: Data) : RecyclerView.Adapter<MainRecyclerViewAdapter.MainViewHolder>() {

    private var deleteMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_recycler_view_adapter_item, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val boardName = boardData.getListBoardInfo()[position]
        holder.mainRecyclerViewAdapterItemBinding.titleView.text = boardName

        holder.mainRecyclerViewAdapterItemBinding.cardView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, BoardActivity::class.java)
            intent.putExtra(BoardActivity.INPUT_BOARD_NAME, boardName)
            context.startActivity(intent)
        }

        if (deleteMode){
            holder.mainRecyclerViewAdapterItemBinding.deleteButton.visibility = View.VISIBLE
            holder.mainRecyclerViewAdapterItemBinding.deleteButton.setOnClickListener {
                AlertDialog.Builder(it.context).apply {
                    setTitle(R.string.delete)

                    setPositiveButton(R.string.delete) { _, _ ->
                        boardData.deleteBoardInfo(boardName)
                        notifyItemRemoved(position)
                    }
                    setNegativeButton(R.string.cancel) { _, _ ->}
                }.show()
            }
        }else holder.mainRecyclerViewAdapterItemBinding.deleteButton.visibility = View.GONE
    }

    override fun getItemCount(): Int = boardData.getListBoardInfo().size

    fun isDeleteMode(): Boolean = deleteMode
    fun deleteModeChange(enable: Boolean) {
        deleteMode = enable
        notifyItemRangeChanged(0, itemCount)
    }

    class MainViewHolder(itemView: View) : ViewHolder(itemView) {
        val mainRecyclerViewAdapterItemBinding = MainRecyclerViewAdapterItemBinding.bind(itemView)
    }
}