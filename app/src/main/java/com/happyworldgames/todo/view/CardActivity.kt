package com.happyworldgames.todo.view

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.happyworldgames.todo.R
import com.happyworldgames.todo.databinding.ActivityCardBinding
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.DataInterface

class CardActivity : AppCompatActivity() {
    private val activityCardBinding by lazy { ActivityCardBinding.inflate(layoutInflater) }

    private val cardInfo by lazy { boardInfo.lists[posList].cards[posCard] }
    private val boardInfo by lazy{BoardInfo.getBoardInfo(this, intent.getStringExtra("id_board")!!) }
    private val posList by lazy { intent.getIntExtra("pos_list", -1) }
    private val posCard by lazy { intent.getIntExtra("pos_card", -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityCardBinding.root)

        activityCardBinding.titleName.setText(cardInfo.name)
        activityCardBinding.titleName.setOnFocusChangeListener { view, b ->
            if(SupportActionModeForEditText.actionMode == null && b)
                SupportActionModeForEditText.actionMode = startSupportActionMode(
                    SupportActionModeForEditText(
                        this,
                        R.string.edit_title_name,
                        activityCardBinding.titleName,
                        fun(data: String){ cardInfo.name = data },
                        fun (): String = cardInfo.name,
                        fun () {
                            DataInterface.getDataInterface(this)
                            .saveCard(boardInfo.id, boardInfo.lists[posList].id, cardInfo)
                        }
                    )
                )
            (view as EditText).isCursorVisible = b
            if(!b) SupportActionModeForEditText.actionMode?.finish()
        }

        activityCardBinding.description.setText(cardInfo.description)
        activityCardBinding.description.setOnFocusChangeListener { view, b ->
            if(SupportActionModeForEditText.actionMode == null && b)
                SupportActionModeForEditText.actionMode = startSupportActionMode(
                    SupportActionModeForEditText(
                        this,
                        R.string.edit_description,
                        activityCardBinding.description,
                        fun(data: String){ cardInfo.description = data },
                        fun (): String = cardInfo.description,
                        fun () {
                            DataInterface.getDataInterface(this)
                                .saveCard(boardInfo.id, boardInfo.lists[posList].id, cardInfo)
                        }
                    )
                )
            (view as EditText).isCursorVisible = b
            if(!b) SupportActionModeForEditText.actionMode?.finish()
        }
    }

}