package com.happyworldgames.todo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.happyworldgames.todo.R
import com.happyworldgames.todo.databinding.ActivityCardHolderTagsEditItemBinding
import com.happyworldgames.todo.databinding.ActivityCardHolderTagsEditItemBottomSheetDialogBinding
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.CardInfo
import com.happyworldgames.todo.model.DataInterface
import com.happyworldgames.todo.model.TagItem

class EditTagsAdapter(private val boardInfo: BoardInfo, private val posList: Int, private val cardInfo: CardInfo) : RecyclerView.Adapter<EditTagsAdapter.MainViewHolder>() {

    var notifyItemChanged: (() -> Unit)? = null
    var notifyItemRemoved: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_holder_tags_edit_item, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.main.tagButton.apply {
            val tag = boardInfo.tagList[position]
            var haveTag = false
            cardInfo.tagList.forEach { tagItem ->
                if (tag == tagItem) {
                    haveTag = true
                    return@forEach
                }
            }

            text = if (haveTag) tag.name + " *" else tag.name
            setBackgroundColor(tag.color)
            setOnClickListener {
                if(haveTag) cardInfo.tagList.remove(tag)
                else cardInfo.tagList.add(tag)
                notifyItemChanged(position)
            }
        }
        holder.main.tagEdit.setOnClickListener {
            showBottomSheetDialog(holder.main.root.context, boardInfo, position)
        }
    }

    override fun getItemCount(): Int = boardInfo.tagList.size

    fun addTag(context: Context) {
        boardInfo.tagList.add(TagItem())
        DataInterface.getDataInterface(context)
            .saveCard(boardInfo.id, boardInfo.lists[posList].id, cardInfo)
        notifyItemInserted(itemCount - 1)
    }

    private fun showBottomSheetDialog(context: Context, boardInfo: BoardInfo, position: Int) {
        val activityCardHolderTagsEditItemBottomSheetDialogBinding = ActivityCardHolderTagsEditItemBottomSheetDialogBinding.inflate(LayoutInflater.from(context))
        BottomSheetDialog(context).apply {
            activityCardHolderTagsEditItemBottomSheetDialogBinding.apply {
                val colors = arrayListOf<Int>()
                arrayOf(R.color.green, R.color.yellow, R.color.orange, R.color.red, R.color.purple, R.color.dark_blue, R.color.blue, R.color.pink).forEach { colorId ->
                    colors.add(ContextCompat.getColor(context, colorId))
                }

                var colorPosition = 0
                colors.forEachIndexed { colorPos, color ->
                    if (color == boardInfo.tagList[position].color) {
                        colorPosition = colorPos
                        return@forEachIndexed
                    }
                }

                val colorButtons = arrayOf(
                    greenButton, yellowButton, orangeButton, redButton, purpleButton, darkBlueButton, blueButton, pinkButton
                )
                val colorButtonIds = arrayOf(
                    R.id.green_button, R.id.yellow_button, R.id.orange_button, R.id.red_button,
                    R.id.purple_button, R.id.dark_blue_button, R.id.blue_button, R.id.pink_button
                )

                colorButtons[colorPosition].text = "*"
                val colorButtonClickListener = View.OnClickListener { view ->
                    colorButtons[colorPosition].text = ""
                    (view as AppCompatButton).apply {
                        text = "*"
                        colorButtonIds.forEachIndexed { posButton, idButton ->
                            if (id == idButton) {
                                colorPosition = posButton
                                return@forEachIndexed
                            }
                        }
                    }
                }

                tagName.setText(boardInfo.tagList[position].name)
                colorButtons.forEach { button ->
                    button.setOnClickListener(colorButtonClickListener)
                }

                // fun buttons
                deleteTagButton.setOnClickListener {
                    boardInfo.tagList.removeAt(position)
                    DataInterface.getDataInterface(context)
                        .saveCard(boardInfo.id, boardInfo.lists[posList].id, cardInfo)
                    notifyItemRemoved?.invoke()
                    dismiss()
                }
                cancelTagButton.setOnClickListener {
                    dismiss()
                }
                doneTagButton.setOnClickListener {
                    boardInfo.tagList[position].apply {
                        color = colors[colorPosition]
                        name = activityCardHolderTagsEditItemBottomSheetDialogBinding.tagName.text.trim().toString()
                    }
                    notifyItemChanged?.invoke()
                    DataInterface.getDataInterface(context)
                        .saveCard(boardInfo.id, boardInfo.lists[posList].id, cardInfo)
                    dismiss()
                }
            }
            setContentView(activityCardHolderTagsEditItemBottomSheetDialogBinding.root)
        }.show()
    }

    class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val main = ActivityCardHolderTagsEditItemBinding.bind(view)
    }

}