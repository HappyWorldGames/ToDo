package com.happyworldgames.todo.adapter

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.happyworldgames.todo.R
import com.happyworldgames.todo.databinding.ActivityCardHolderEditTextBinding
import com.happyworldgames.todo.databinding.ActivityCardHolderTagsBinding
import com.happyworldgames.todo.databinding.ActivityCardHolderTagsEditBinding
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.CardInfo
import com.happyworldgames.todo.model.DataInterface
import com.happyworldgames.todo.view.SupportActionModeForEditText

class CardItemsAdapter(
    private val activity: AppCompatActivity,                                // for start SupportActionBar
    private val cardInfo: CardInfo,                                         // card info
    private val boardInfo: BoardInfo,                                       // for save card info
    private val posList: Int                                                // for save card info
    ) : RecyclerView.Adapter<CardItemsAdapter.MainViewHolder>() {
    val context = activity as Context                                       // make context from activity
    var isEditTag = false

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0, 1 -> R.layout.activity_card_holder_edit_text
            2 -> if (!isEditTag) R.layout.activity_card_holder_tags else R.layout.activity_card_holder_tags_edit
            3 -> TODO("Check list")
            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.activity_card_holder_edit_text -> EditTextViewHolder(view)
            R.layout.activity_card_holder_tags -> TagsViewHolder(view)
            R.layout.activity_card_holder_tags_edit -> TagsEditViewHolder(view)
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

            2 -> {
                if (!isEditTag) {
                    val holder = mHolder as TagsViewHolder
                    holder.main.tagsTextView.setOnClickListener {
                        isEditTag = true
                        notifyItemChanged(position)
                    }
                } else {
                    val holder = mHolder as TagsEditViewHolder
                    val editTagsAdapter = EditTagsAdapter(
                        boardInfo, cardInfo,
                        { DataInterface.getDataInterface(context)
                            .saveCard(boardInfo.id, boardInfo.lists[posList].id, cardInfo) },       // save card
                        { notifyItemChanged(position) },                                            // update item in view
                        { notifyItemRemoved(position) }                                             // remove item in view
                    )
                    holder.main.recyclerView.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = editTagsAdapter
                    }
                    holder.main.createTag.setOnClickListener {
                        editTagsAdapter.addTag()
                    }
                }
            }

        }
    }

    override fun getItemCount(): Int = 3 // TODO("Update when add tag and check list to 4")

    open class MainViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class EditTextViewHolder(view: View) : MainViewHolder(view) {
        val main = ActivityCardHolderEditTextBinding.bind(view)
    }
    class TagsViewHolder(view: View) : MainViewHolder(view) {
        val main = ActivityCardHolderTagsBinding.bind(view)
    }
    class TagsEditViewHolder(view: View) : MainViewHolder(view) {
        val main = ActivityCardHolderTagsEditBinding.bind(view)
    }
}