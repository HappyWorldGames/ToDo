package com.happyworldgames.todo.model

import android.content.Context
import com.happyworldgames.todo.data.FileSystem

interface DataInterface {

    companion object {
        fun getDataInterface(context: Context): DataInterface = FileSystem(context) // for easy access
    }

    fun getBoards(): Array<InfoInterface>

    fun getBoard(id: String): BoardInfo

    fun saveBoard(boardInfo: BoardInfo)
    fun saveList(boardInfo: BoardInfo, listInfo: ListInfo)
    fun saveCard(idBoard: String, idList: String, cardInfo: CardInfo)

    fun deleteBoard(idBoard: String)
    fun deleteList(idBoard: String, idList: String)
    fun deleteCard(idBoard: String, idList: String, idCard: String)
}