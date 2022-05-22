package com.happyworldgames.todo.model

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class BoardInfo(override var id: String, override var position: Int, override var name: String,
                     @Transient val lists: ArrayList<ListInfo> = arrayListOf()): InfoInterface {
    companion object {
        private var boardInfo: BoardInfo? = null
        fun getBoardInfo(context: Context, id: String): BoardInfo {
            if(boardInfo == null || boardInfo!!.id != id)
                boardInfo = DataInterface.getDataInterface(context).getBoard(id)
            return boardInfo!!
        }
    }
}