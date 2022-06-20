package com.happyworldgames.todo.view

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.happyworldgames.todo.R
import com.happyworldgames.todo.adapter.CardItemsAdapter
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

        activityCardBinding.cardItems.layoutManager = LinearLayoutManager(this)
        activityCardBinding.cardItems.adapter = CardItemsAdapter(this, cardInfo, boardInfo, posList)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.apply {
            add(0, R.string.delete, 0, getString(R.string.delete))
        }
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.string.delete -> {
                DataInterface.getDataInterface(this).deleteCard(boardInfo.id, boardInfo.lists[posList].id, cardInfo.id)
                finish()
                true
            }
            else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }
}