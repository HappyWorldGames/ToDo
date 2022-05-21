package com.happyworldgames.todo.model

import android.content.Context
import com.happyworldgames.todo.data.FileSystem

interface DataInterface {

    companion object {
        fun getDataInterface(context: Context): DataInterface = FileSystem(context)
    }

    fun getBoards(): Array<InfoInterface>

    fun getBoard(id: String): BoardInfo

    fun saveBoard(boardInfo: BoardInfo)
    fun saveList(boardInfo: BoardInfo, listInfo: ListInfo)
    fun saveCard(idBoard: String, idList: String, cardInfo: CardInfo)

    fun deleteBoard(id: String)
}