package com.happyworldgames.todo.adapter

import android.content.Context
import android.text.InputType
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.happyworldgames.todo.R
import com.happyworldgames.todo.actionmode.SupportActionMode
import com.happyworldgames.todo.actionmode.SupportActionModeForEditText
import com.happyworldgames.todo.databinding.ActivityCardHolderEditTextBinding
import com.happyworldgames.todo.databinding.ActivityCardHolderTagsBinding
import com.happyworldgames.todo.databinding.ActivityCardHolderTagsEditBinding
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.CardInfo
import com.happyworldgames.todo.model.DataInterface

class CardItemsAdapter(
    private val activity: AppCompatActivity,                                // for start SupportActionBar
    private val cardInfo: CardInfo,                                         // card info
    private val boardInfo: BoardInfo,                                       // for save card info
    private val posList: Int                                                // for save card info
    ) : RecyclerView.Adapter<CardItemsAdapter.MainViewHolder>() {

    val context = activity as Context                                       // make context from activity
    private var isEditTag = false                                                   // flag mode tag

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
            0, 1 -> onBindEditText(mHolder, position)
            2 -> onBindPos2(mHolder, position)
        }
    }

    private fun onBindEditText(mHolder: MainViewHolder, position: Int) {
        val holder = mHolder as EditTextViewHolder
        holder.main.editText.apply {                                // init edit text
            inputType = when (position) {                           // set input type
                0 -> InputType.TYPE_TEXT_VARIATION_FILTER
                1 -> InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                else -> -1
            }
            hint = context.getString(                               // set hint
                when (position) {
                    0 -> R.string.title_name
                    1 -> R.string.description
                    else -> -1
                }
            )
            setText(                                                // set text
                when (position) {
                    0 -> cardInfo.name
                    1 -> cardInfo.description
                    else -> ""
                }
            )

            val editTextId = when (position) {                      // editTextId for supportActionModeForEditText
                0 -> R.string.edit_title_name
                1 -> R.string.edit_description
                else -> -1
            }
            val doneTextId = when (position) {                      // doneTextId for supportActionModeForEditText
                0, 1 -> R.string.save
                else -> -1
            }
            val blankTextId = when (position) {                     // blankTextId for supportActionModeForEditText
                0 -> R.string.blank_text
                else -> -1
            }
            val setData = fun(data: String) {                       // setData for supportActionModeForEditText
                when (position) {
                    0 -> cardInfo.name = data
                    1 -> cardInfo.description = data
                    else -> throw Exception("Not Set Data")
                }
            }
            val getData = fun(): String {                           // getData for supportActionModeForEditText
                return when (position) {
                    0 -> cardInfo.name
                    1 -> cardInfo.description
                    else -> throw Exception("Not Get Data")
                }
            }
            val supportActionModeForEditText = SupportActionModeForEditText(            // supportActionModeForEditText
                context.getString(editTextId),
                context.getString(doneTextId),
                if(blankTextId != -1) context.getString(blankTextId) else SupportActionModeForEditText.NO_BLANK_CHECK,
                this,
                setData,
                getData,
                fun () {
                    DataInterface.getDataInterface(this@CardItemsAdapter.context)
                        .saveCard(boardInfo.id, boardInfo.lists[posList].id, cardInfo)
                }
            )
            setOnFocusChangeListener { view, hasFocus ->
                supportActionModeForEditText.onFocusChangeListener(
                    this@CardItemsAdapter.activity, view, hasFocus
                )
            }
            setOnKeyListener { _, keyCode, keyEvent ->
                supportActionModeForEditText.onKeyListener(keyCode, keyEvent)
            }
        }
    }
    private fun onBindPos2(mHolder: MainViewHolder, position: Int) {
        if (!isEditTag) {
            val holder = mHolder as TagsViewHolder
            holder.main.root.setOnClickListener {
                isEditTag = true
                notifyItemChanged(position)
            }

            val spannableStringBuilder = SpannableStringBuilder()
            var spanPos = 0
            cardInfo.tagList.forEach { tagItem ->
                val tempSpanPos = spanPos + if (tagItem.name.isNotEmpty()) tagItem.name.length else 1

                spannableStringBuilder.append(tagItem.name.ifEmpty { " " })
                spannableStringBuilder.setSpan(tagItem.color, spanPos, tempSpanPos, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

                spanPos = tempSpanPos
            }

            holder.main.tagsTextView.text = spannableStringBuilder.ifEmpty { context.getString(R.string.tags) }
        } else {
            val holder = mHolder as TagsEditViewHolder

            SupportActionMode(context.getString(R.string.edit_tags)).apply {
                setOnDestroyActionMode {
                    isEditTag = false
                    notifyItemChanged(position)
                }
            }.startActionMode(activity)

            val editTagsAdapter = EditTagsAdapter(boardInfo, cardInfo) { isBoard ->
                val dataInterface = DataInterface.getDataInterface(context)

                if(isBoard) dataInterface.saveBoard(boardInfo)
                else dataInterface.saveCard(boardInfo.id, boardInfo.lists[posList].id, cardInfo)
            }

            holder.main.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = editTagsAdapter
            }
            holder.main.createTag.setOnClickListener {
                editTagsAdapter.addTag(context)
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