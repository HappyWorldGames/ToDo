package com.happyworldgames.todo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.happyworldgames.todo.R
import com.happyworldgames.todo.databinding.ActivityCardHolderTagsEditItemBinding
import com.happyworldgames.todo.databinding.ActivityCardHolderTagsEditItemBottomSheetDialogBinding
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.CardInfo
import com.happyworldgames.todo.model.TagItem

/*
* Adapter for edit tag: add, change name/color, delete
* */
class EditTagsAdapter(
    private val boardInfo: BoardInfo,
    private val cardInfo: CardInfo,
    private val saveData: (isBoard: Boolean) -> Unit                  // for save board
) : RecyclerView.Adapter<EditTagsAdapter.MainViewHolder>() {

    init {
        val removeList = arrayListOf<TagItem>()         // for work, create remove list
        cardInfo.tagList.forEach { cardTag ->
            var haveTag = false
            boardInfo.tagList.forEach BoardFor@{ boardTag ->
                if (boardTag == cardTag) {
                    haveTag = true
                    return@BoardFor
                }
            }
            if(!haveTag) removeList.add(cardTag)        // add to remove list
        }
        removeList.forEach { cardTag ->
            cardInfo.tagList.remove(cardTag)            // use remove list for remove from cardInfo tagList
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_holder_tags_edit_item, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.main.tagButton.apply {
            val tag = boardInfo.tagList[position]
            var haveTag = false
            cardInfo.tagList.forEach { tagItem ->           // check have tag
                if (tag == tagItem) {
                    haveTag = true
                    return@forEach
                }
            }

            text = if (haveTag) tag.name + " *" else tag.name       // if have tag select
            setBackgroundColor(tag.color)
            setOnClickListener {                                    // select and unselect tag
                if(haveTag) cardInfo.tagList.remove(tag)
                else cardInfo.tagList.add(tag)
                notifyItemChanged(position)
                saveData(false)

            }
        }
        holder.main.tagEdit.setOnClickListener {
            showBottomSheetDialog(holder.main.root.context, position)        // show bottom dialog
        }
    }

    override fun getItemCount(): Int = boardInfo.tagList.size

    fun addTag(context: Context) {
        showBottomSheetDialog(context, -1)
    }

    /*
    * show bottom dialog, change name/color and delete
    * */
    private fun showBottomSheetDialog(context: Context, position: Int) {
        val isCreate = position == -1
        val activityCardHolderTagsEditItemBottomSheetDialogBinding = ActivityCardHolderTagsEditItemBottomSheetDialogBinding.inflate(LayoutInflater.from(context))

        BottomSheetDialog(context).also { bottomDialog ->           // init bottom dialog

            activityCardHolderTagsEditItemBottomSheetDialogBinding.apply {      // init bottom dialog view
                if(isCreate) deleteTagButton.visibility = View.GONE

                val colors = arrayListOf<Int>()
                arrayOf(R.color.green, R.color.yellow, R.color.orange, R.color.red, R.color.purple, R.color.dark_blue, R.color.blue, R.color.pink).forEach { colorId ->
                    colors.add(ContextCompat.getColor(context, colorId))
                }

                var colorPosition = 0                                       // var for color position
                if(!isCreate) colors.forEachIndexed { colorPos, color ->                  // find current color position
                    if (color == boardInfo.tagList[position].color) {
                        colorPosition = colorPos
                        return@forEachIndexed
                    }
                }

                val colorButtons = arrayOf(                         // for easy access
                    greenButton, yellowButton, orangeButton, redButton, purpleButton, darkBlueButton, blueButton, pinkButton
                )
                val colorButtonIds = arrayOf(                       // for easy find clicked button
                    R.id.green_button, R.id.yellow_button, R.id.orange_button, R.id.red_button,
                    R.id.purple_button, R.id.dark_blue_button, R.id.blue_button, R.id.pink_button
                )

                tagName.setText(if(!isCreate) boardInfo.tagList[position].name else "")                   // set tag name on edittext

                val checkEquals = {
                    val currentTagItem = TagItem(tagName.text.toString(), colors[colorPosition])

                    var hasTag = false
                    boardInfo.tagList.forEach { tagItem ->
                        if (tagItem == currentTagItem){
                            hasTag = true
                            return@forEach
                        }
                    }
                    if (hasTag) {
                        doneTagButton.isEnabled = false
                        tagName.error = context.getString(R.string.tag_has_already)
                    } else {
                        doneTagButton.isEnabled = true
                        tagName.error = null
                    }
                }

                tagName.doAfterTextChanged {
                    checkEquals()
                }
                colorButtons[colorPosition].text = "*"                              // check start color position button
                val colorButtonClickListener = View.OnClickListener { view ->       // click listener for color buttons
                    colorButtons[colorPosition].text = ""                           // uncheck prev color position button
                    (view as AppCompatButton).apply {
                        text = "*"                                                  // check now color position button
                        colorButtonIds.forEachIndexed { posButton, idButton ->      // find now color position button
                            if (id == idButton) {
                                colorPosition = posButton
                                checkEquals()
                                return@forEachIndexed
                            }
                        }
                    }
                }

                colorButtons.forEach { button ->                                    // set color button click listener for all color button
                    button.setOnClickListener(colorButtonClickListener)
                }

                // fun buttons
                if(!isCreate) deleteTagButton.setOnClickListener {            // delete tag button
                    boardInfo.tagList.removeAt(position)
                    saveData(true)
                    notifyItemRemoved(position)
                    bottomDialog.dismiss()
                }
                cancelTagButton.setOnClickListener {            // cancel tag button
                    bottomDialog.dismiss()
                }
                doneTagButton.setOnClickListener {              // done tag button
                    val tagItem = TagItem(tagName.text.trim().toString(), colors[colorPosition])
                    if(isCreate) {
                        boardInfo.tagList.add(tagItem)
                        notifyItemInserted(itemCount)
                    } else {
                        boardInfo.tagList[position] = tagItem
                        notifyItemChanged(position)
                    }
                    saveData(true)
                    bottomDialog.dismiss()
                }
            }
            // set view on bottom dialog
            bottomDialog.setContentView(activityCardHolderTagsEditItemBottomSheetDialogBinding.root)
        }.show()
    }

    class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val main = ActivityCardHolderTagsEditItemBinding.bind(view)
    }

}