package com.happyworldgames.todo.view

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.happyworldgames.todo.R
import com.happyworldgames.todo.databinding.ActivityCardHolderEditTextBinding
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.CardInfo
import com.happyworldgames.todo.model.DataInterface

class CardItemsAdapter(private val activity: AppCompatActivity, private val cardInfo: CardInfo, private val boardInfo: BoardInfo, private val posList: Int) : RecyclerView.Adapter<CardItemsAdapter.MainViewHolder>() {
    val context = activity as Context

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0, 1 -> R.layout.activity_card_holder_edit_text
            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.activity_card_holder_edit_text -> EditTextViewHolder(view)
            else -> MainViewHolder(view)
        }
    }

    override fun onBindViewHolder(mHolder: MainViewHolder, position: Int) {
        when (position) {
            0 -> {
                val holder = mHolder as EditTextViewHolder
                holder.main.editText.apply {
                    inputType = InputType.TYPE_TEXT_VARIATION_FILTER
                    hint = context.getString(R.string.title_name)
                    setText(cardInfo.name)

                    val supportActionModeForEditTextTitle = SupportActionModeForEditText(
                        R.string.edit_title_name,
                        R.string.save,
                        R.string.blank_text,
                        this,
                        fun(data: String){ cardInfo.name = data },
                        fun (): String = cardInfo.name,
                        fun () {
                            DataInterface.getDataInterface(this@CardItemsAdapter.context)
                                .saveCard(boardInfo.id, boardInfo.lists[posList].id, cardInfo)
                        }
                    )
                    setOnFocusChangeListener { view, hasFocus ->
                        SupportActionModeForEditText.onFocusChangeListener(
                            this@CardItemsAdapter.activity, view, hasFocus,
                            supportActionModeForEditTextTitle
                        )
                    }
                    setOnKeyListener { _, keyCode, keyEvent ->
                        supportActionModeForEditTextTitle.onKeyListener(keyCode, keyEvent)
                    }
                }
            }
            1 -> {
                val holder = mHolder as EditTextViewHolder
                holder.main.editText.apply {
                    inputType = InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                    hint = context.getString(R.string.description)
                    setText(cardInfo.description)

                    val supportActionModeForEditTextDescription = SupportActionModeForEditText(
                        R.string.edit_description,
                        R.string.save,
                        SupportActionModeForEditText.NO_BLANK_CHECK,
                        this,
                        fun(data: String){ cardInfo.description = data },
                        fun (): String = cardInfo.description,
                        fun () {
                            DataInterface.getDataInterface(this@CardItemsAdapter.context)
                                .saveCard(boardInfo.id, boardInfo.lists[posList].id, cardInfo)
                        }
                    )
                    setOnFocusChangeListener { view, hasFocus ->
                        SupportActionModeForEditText.onFocusChangeListener(
                            this@CardItemsAdapter.activity, view, hasFocus,
                            supportActionModeForEditTextDescription
                        )
                    }
                    setOnKeyListener { _, keyCode, keyEvent ->
                        supportActionModeForEditTextDescription.onKeyListener(keyCode, keyEvent)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = 2

    open class MainViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class EditTextViewHolder(view: View) : MainViewHolder(view) {
        val main = ActivityCardHolderEditTextBinding.bind(view)
    }
}