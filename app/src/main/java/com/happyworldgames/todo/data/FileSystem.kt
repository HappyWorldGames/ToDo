package com.happyworldgames.todo.data

import android.content.Context
import com.happyworldgames.todo.model.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.charset.Charset

class FileSystem(context: Context) : DataInterface {
    private val charset = "UTF-8"                                   // standard charset
    private val dirPath = context.getExternalFilesDir("")           // main folder path
    private val infoFileName = "info.xml"                           // name info file

    override fun getBoards(): Array<InfoInterface> {
        val list = dirPath?.listFiles()?: return arrayOf()      // read files from main folder
        val infoList = arrayListOf<InfoInterface>()             // array list for result
        for(item in list){
            val text = getDataFromFile(item)                    // read text from file
            if(text == "") continue                             // skip if text null

            val cl = Json.decodeFromString<BoardInfo>(text)     // init BoardInfo
            if(cl.id != "" && cl.name != "") infoList.add(cl)   // add to result array
        }
        return infoList.toArray(arrayOfNulls(infoList.size))    // return result array
    }

    override fun getBoard(id: String): BoardInfo {
        val infoFolder = File(dirPath, id)                              // init board folder
        var text = getDataFromFile(infoFolder)                          // get text from file

        val boardInfo = Json.decodeFromString<BoardInfo>(text)          // init BoardInfo

        for(listFolder in infoFolder.listFiles()!!) {
            if(listFolder.isFile) continue                              // search folder so skip file
            text = getDataFromFile(listFolder)                          // get text from file
            if(text == "") continue                                     // skip if text null

            val listInfo = Json.decodeFromString<ListInfo>(text)        // init ListInfo

            for(cardFolder in listFolder.listFiles()!!){
                if(cardFolder.isFile) continue                          // search folder so skip file
                text = getDataFromFile(cardFolder)                      // get text from file
                if(text == "") continue                                 // skip if text null

                val cardInfo = Json.decodeFromString<CardInfo>(text)    // init CardInfo
                listInfo.cards.add(cardInfo)                            // add card to cards
            }

            boardInfo.lists.add(listInfo)                               // add listInfo to BoardInfo
        }

        return boardInfo        // return result BoardInfo
    }

    override fun saveBoard(boardInfo: BoardInfo) {
        val folder = File(dirPath, boardInfo.id)        // init folder board
        if(!folder.exists()) folder.mkdirs()            // if no exists folder create
        val text = Json.encodeToString(boardInfo)       // encode BoardInfo to json string

        saveDataToFile(folder, text)                    // save to file BoardInfo
    }
    override fun saveList(boardInfo: BoardInfo, listInfo: ListInfo) {
        val folder = File(dirPath, boardInfo.id + "/" + listInfo.id)       // init folder list
        if(!folder.exists()) folder.mkdirs()                                    // if no exists folder create
        val text = Json.encodeToString(listInfo)                                // encode ListInfo to json string

        saveDataToFile(folder, text)                                            // save to file ListInfo
    }
    override fun saveCard(idBoard: String, idList: String, cardInfo: CardInfo) {
        val folder = File(dirPath, idBoard + "/" + idList + "/" + cardInfo.id) // init folder card
        if(!folder.exists()) folder.mkdirs()                    // if no exists folder create
        val text = Json.encodeToString(cardInfo)                // encode CardInfo to json string

        saveDataToFile(folder, text)                            // save to file CardInfo
    }

    override fun deleteBoard(idBoard: String) {
        val folder = File(dirPath, idBoard)     // init folder board
        delete(folder)                          // delete board
    }
    override fun deleteList(idBoard: String, idList: String) {
        val folder = File(dirPath, "$idBoard/$idList")      // init folder list
        delete(folder)                                           // delete list
    }
    override fun deleteCard(idBoard: String, idList: String, idCard: String) {
        val folder = File(dirPath, "$idBoard/$idList/$idCard")      // init folder card
        delete(folder)                                                   // delete card
    }

    private fun getDataFromFile(filePath: File) : String {
        // check if filePath folder when init file else give filePath
        val file = if(filePath.isDirectory) File(filePath, infoFileName) else filePath
        // return text if file exists else null
        return if(file.exists()) file.readText(Charset.forName(charset)) else ""
    }
    private fun saveDataToFile(filePath: File, data: String) {
        // check if filePath folder when init file else give filePath
        val file = if(filePath.isDirectory) File(filePath, infoFileName) else filePath
        // write text to file
        file.writeText(data, Charset.forName(charset))
    }

    private fun delete(file: File) {
        // if file is directory, scan directory
        if(file.isDirectory) file.listFiles()!!.forEach { f ->
            delete(f) // call delete fun
        }
        file.delete() // delete file/folder
    }
}