package com.happyworldgames.todo.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.happyworldgames.todo.R
import com.happyworldgames.todo.databinding.ActivityBoardBinding
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.DataInterface

class BoardActivity : AppCompatActivity() {
    private val activityBoardBinding by lazy { ActivityBoardBinding.inflate(layoutInflater) }

    private val dataInterface by lazy { DataInterface.getDataInterface(this) }
    private val boardInfo by lazy { BoardInfo.getBoardInfo(this, intent.getStringExtra("id")!!) }
    private val adapter by lazy { BoardRecyclerViewAdapter(dataInterface, boardInfo) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityBoardBinding.root)

        activityBoardBinding.viewPager.adapter = adapter
        activityBoardBinding.viewPager.offscreenPageLimit = 3
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
        }

    }
}