package com.happyworldgames.todo.view

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.happyworldgames.todo.R
import com.happyworldgames.todo.databinding.ActivityCardBinding
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.DataInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

class CardActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    private val activityCardBinding by lazy { ActivityCardBinding.inflate(layoutInflater) }

    private val cardInfo by lazy { boardInfo.lists[posList].cards[posCard] }
    private val boardInfo by lazy {
        BoardInfo.getBoardInfo(this as Context, intent.getStringExtra("id_board")!!)
    }
    private val posList by lazy { intent.getIntExtra("pos_list", -1) }
    private val posCard by lazy { intent.getIntExtra("pos_card", -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }
        setContentView(activityCardBinding.root)

        val supportActionModeForEditTextTitle = SupportActionModeForEditText(
            R.string.edit_title_name,
            R.string.save,
            R.string.blank_text,
            activityCardBinding.titleName,
            fun(data: String){ cardInfo.name = data },
            fun (): String = cardInfo.name,
            fun () {
                DataInterface.getDataInterface(this)
                    .saveCard(boardInfo.id, boardInfo.lists[posList].id, cardInfo)
            }
        )
        activityCardBinding.titleName.setText(cardInfo.name)
        activityCardBinding.titleName.setOnFocusChangeListener { view, hasFocus ->
             SupportActionModeForEditText.onFocusChangeListener(
                 this as AppCompatActivity, view, hasFocus,
                supportActionModeForEditTextTitle
             )
        }
        activityCardBinding.titleName.setOnKeyListener { _, keyCode, keyEvent ->
            supportActionModeForEditTextTitle.onKeyListener(keyCode, keyEvent)
        }

        val supportActionModeForEditTextDescription = SupportActionModeForEditText(
            R.string.edit_description,
            R.string.save,
            SupportActionModeForEditText.NO_BLANK_CHECK,
            activityCardBinding.description,
            fun(data: String){ cardInfo.description = data },
            fun (): String = cardInfo.description,
            fun () {
                DataInterface.getDataInterface(this)
                    .saveCard(boardInfo.id, boardInfo.lists[posList].id, cardInfo)
            }
        )
        activityCardBinding.description.setText(cardInfo.description)
        activityCardBinding.description.setOnFocusChangeListener { view, hasFocus ->
            SupportActionModeForEditText.onFocusChangeListener(
                this as AppCompatActivity, view, hasFocus,
                supportActionModeForEditTextDescription
            )
        }
        activityCardBinding.description.setOnKeyListener { _, keyCode, keyEvent ->
            supportActionModeForEditTextDescription.onKeyListener(keyCode, keyEvent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }
}