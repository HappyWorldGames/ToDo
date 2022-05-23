package com.happyworldgames.todo.model

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class BoardInfo(
    override var id: String,
    override var position: Int,
    override var name: String,
    @Transient val lists: ArrayList<ListInfo> = arrayListOf()                   //list lists
): InfoInterface {
    companion object {
        private var boardInfo: BoardInfo? = null                                // global boardInfo
        fun getBoardInfo(context: Context, id: String): BoardInfo {             // for easy access
            if(boardInfo == null || boardInfo!!.id != id)
                boardInfo = DataInterface.getDataInterface(context).getBoard(id)
            return boardInfo!!
        }
    }
}