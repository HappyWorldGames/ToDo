package com.happyworldgames.todo.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.happyworldgames.todo.R
import com.happyworldgames.todo.databinding.ActivityMainItemBinding
import com.happyworldgames.todo.model.DataInterface
import com.happyworldgames.todo.view.BoardActivity

class MainRecyclerViewAdapter(private val context: Context) :
    RecyclerView.Adapter<MainRecyclerViewAdapter.MainViewHolder>() {

    private val dataInterface = DataInterface.getDataInterface(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_main_item, parent,
            false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val boardInfo = dataInterface.getBoards()[position]
        holder.mainView.textView.text = if(boardInfo.name.length > 20) boardInfo.name.subSequence(0, 20)
                                        else boardInfo.name
        holder.mainView.cardView.setOnClickListener {
            val intent = Intent(context, BoardActivity::class.java)
            intent.putExtra("id", boardInfo.id)
            context.startActivity(intent)
        }
        holder.mainView.cardView.setOnLongClickListener {
            val popupMenu = PopupMenu(context, holder.mainView.cardView)
            popupMenu.menu.add(0, 1, 0, context.getString(R.string.delete))
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    1 -> {
                        dataInterface.deleteBoard(boardInfo.id)
                        notifyItemRemoved(position)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
            true
        }
    }

    override fun getItemCount(): Int = dataInterface.getBoards().size

    class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainView = ActivityMainItemBinding.bind(view)
    }
}