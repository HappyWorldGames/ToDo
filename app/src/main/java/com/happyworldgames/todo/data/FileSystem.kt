package com.happyworldgames.todo.data

import android.content.Context
import com.happyworldgames.todo.model.*
import java.io.File
import java.nio.charset.Charset

class FileSystem(context: Context) : DataInterface {
    private val charset = "UTF-8"
    private val dirPath = context.getExternalFilesDir("")

    override fun getBoards(): Array<InfoInterface> {
        val list = dirPath?.listFiles()?: return arrayOf()
        val infoList = arrayListOf<InfoInterface>()
        for(item in list){
            val textLines = getDataFromFile(item)
            val cl = parseInfo(textLines)
            if(cl.id != "" && cl.name != "") infoList.add(cl)
        }
        return infoList.toArray(arrayOfNulls(infoList.size))
    }
    override fun getBoard(id: String): BoardInfo {
        val infoFolder = File(dirPath, id)
        var text = getDataFromFile(infoFolder)

        var info = parseInfo(text)
        var textLines = text.split("\n")

        val boardInfo = BoardInfo(info.id, info.position, info.name)

        for(listFolder in infoFolder.listFiles()!!) {
            if(listFolder.isFile) continue
            text = getDataFromFile(listFolder)

            info = parseInfo(text)
            val listInfo = ListInfo(info.id, info.position, info.name)

            for(cardFolder in listFolder.listFiles()!!){
                if(cardFolder.isFile) continue
                text = getDataFromFile(cardFolder)

                info = parseInfo(text)
                textLines = text.split("\n")

                var description = ""
                for(line in textLines){
                    when{
                        line.startsWith("description") -> {
                            description = line.split(" ")[1]
                        }
                    }
                }

                val cardInfo = CardInfo(info.id, info.position, info.name, description)
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
        val text = collectInfo(boardInfo)

        saveDataOnFile(file, text.toString())
    }
    override fun saveList(boardInfo: BoardInfo, listInfo: ListInfo) {
        val folder = File(dirPath, boardInfo.id + "/" + listInfo.id)
        folder.mkdirs()
        val file = File(folder, ".info")
        val text = collectInfo(listInfo)

        saveDataOnFile(file, text.toString())
    }
    override fun saveCard(idBoard: String, idList: String, cardInfo: CardInfo) {
        val folder = File(dirPath, idBoard + "/" + idList + "/" + cardInfo.id)
        folder.mkdirs()
        val file = File(folder, ".info")
        val text = collectInfo(cardInfo)
        text.append("description ").append(cardInfo.description).appendLine()

        saveDataOnFile(file, text.toString())
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
    private fun parseInfo(text: String): InfoInterface {
        val textLines = text.split("\n")
        val cl = object : InfoInterface {
            override var id: String = ""
            override var position: Int = -1
            override var name: String = ""
        }
        for(line in textLines){
            when {
                line.startsWith("id") -> {
                    cl.id = line.split(" ")[1]
                }
                line.startsWith("position") -> {
                    cl.position = line.split(" ")[1].toInt()
                }
                line.startsWith("name") -> {
                    cl.name = line.split(" ")[1]
                }
            }
        }
        return cl
    }
    private fun collectInfo(infoInterface: InfoInterface): StringBuilder {
        val text = StringBuilder()

        text.append("id ").append(infoInterface.id).appendLine()
        text.append("position ").append(infoInterface.position).appendLine()
        text.append("name ").append(infoInterface.name).appendLine()

        return text
    }
    private fun delete(file: File) {
        if(file.isDirectory) file.listFiles()!!.forEach { f ->
            delete(f)
        }
        file.delete()
    }
}