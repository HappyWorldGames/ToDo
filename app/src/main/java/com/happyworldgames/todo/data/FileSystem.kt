package com.happyworldgames.todo.data

import android.content.Context
import com.happyworldgames.todo.model.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.charset.Charset

class FileSystem(context: Context) : DataInterface {
    private val charset = "UTF-8"
    private val dirPath = context.getExternalFilesDir("")

    override fun getBoards(): Array<InfoInterface> {
        val list = dirPath?.listFiles()?: return arrayOf()
        val infoList = arrayListOf<InfoInterface>()
        for(item in list){
            val text = getDataFromFile(item)
            val cl = Json.decodeFromString<BoardInfo>(text)
            if(cl.id != "" && cl.name != "") infoList.add(cl)
        }
        return infoList.toArray(arrayOfNulls(infoList.size))
    }
    override fun getBoard(id: String): BoardInfo {
        val infoFolder = File(dirPath, id)
        var text = getDataFromFile(infoFolder)

        val boardInfo = Json.decodeFromString<BoardInfo>(text)

        for(listFolder in infoFolder.listFiles()!!) {
            if(listFolder.isFile) continue
            text = getDataFromFile(listFolder)

            val listInfo = Json.decodeFromString<ListInfo>(text)

            for(cardFolder in listFolder.listFiles()!!){
                if(cardFolder.isFile) continue
                text = getDataFromFile(cardFolder)

                val cardInfo = Json.decodeFromString<CardInfo>(text)
                listInfo.cards.add(cardInfo)
            }

            boardInfo.lists.add(listInfo)
        }

        return boardInfo
    }

    override fun saveBoard(boardInfo: BoardInfo) {
        val folder = File(dirPath, boardInfo.id)
        folder.mkdirs()
        val file = File(folder, ".info")
        val text = Json.encodeToString(boardInfo)

        saveDataOnFile(file, text)
    }
    override fun saveList(boardInfo: BoardInfo, listInfo: ListInfo) {
        val folder = File(dirPath, boardInfo.id + "/" + listInfo.id)
        folder.mkdirs()
        val file = File(folder, ".info")
        val text = Json.encodeToString(listInfo)

        saveDataOnFile(file, text)
    }
    override fun saveCard(idBoard: String, idList: String, cardInfo: CardInfo) {
        val folder = File(dirPath, idBoard + "/" + idList + "/" + cardInfo.id)
        folder.mkdirs()
        val file = File(folder, ".info")
        val text = Json.encodeToString(cardInfo)

        saveDataOnFile(file, text)
    }

    override fun deleteBoard(id: String) {
        val folder = File(dirPath, id)
        delete(folder)
    }

    private fun getDataFromFile(filePath: File) : String {
        val file = if(filePath.isDirectory) File(filePath, ".info") else filePath
        return file.readText(Charset.forName(charset))
    }
    private fun saveDataOnFile(filePath: File, data: String) {
        val file = if(filePath.isDirectory) File(filePath, ".info") else filePath
        return file.writeText(data, Charset.forName(charset))
    }

    private fun delete(file: File) {
        if(file.isDirectory) file.listFiles()!!.forEach { f ->
            delete(f)
        }
        file.delete()
    }
}