package com.happyworldgames.todo.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.happyworldgames.todo.R
import com.happyworldgames.todo.databinding.ActivityBoardBinding
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.DataInterface
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class BoardActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + Job()

    private val activityBoardBinding by lazy { ActivityBoardBinding.inflate(layoutInflater) }

    private val dataInterface by lazy { DataInterface.getDataInterface(this) }
    private val boardInfo by lazy { BoardInfo.getBoardInfo(this, intent.getStringExtra("id")!!) }
    private val adapter by lazy { BoardRecyclerViewAdapter(this, dataInterface, boardInfo) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityBoardBinding.root)

        launch(Dispatchers.Main) {
            onCreate()
        }
    }

    private suspend fun onCreate() {
        title = withContext(Dispatchers.IO) {
            boardInfo.name
        }
        initViewPager()
    }

    private fun initViewPager() {
        activityBoardBinding.viewPager.adapter = adapter
        activityBoardBinding.viewPager.offscreenPageLimit = 3

        /*
            For view 3 pagers on screen
         */
        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin)
        val offsetPx = resources.getDimensionPixelOffset(R.dimen.offset)
        activityBoardBinding.viewPager.setPageTransformer { page, position ->
            val viewPager = page.parent.parent as ViewPager2
            val offset = position * -(2 * offsetPx + pageMarginPx)
            if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    page.translationX = -offset
                } else {
                    page.translationX = offset
                }
            } else {
                page.translationY = offset
            }
            /* TODO("future zoom")
            page.scaleX = 0.75f
            page.scaleY = 0.75f*/
        }
        /*
            End
         */
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }
}