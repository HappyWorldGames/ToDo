package com.happyworldgames.todo.core

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class Data(context: Context) {

    companion object {
        const val spacePosition = "]_["
        const val cardFileName = "CardFile.txt"
    }

    private val projectsDir = File(context.filesDir, "projects")

    fun saveBoardInfo(boardInfo: BoardInfo) {
        deleteBoardInfo(boardInfo.title) // TODO Костыль

        val projectDir = File(projectsDir, boardInfo.title)
        if (!projectDir.exists()) projectDir.mkdirs()

        boardInfo.cardListInfoList.forEach { cardListInfo ->
            val cardListDir = File(projectDir, cardListInfo.title + spacePosition + cardListInfo.position)
            if (!cardListDir.exists()) cardListDir.mkdirs()
            cardListInfo.cardInfoList.forEach { cardInfo ->
                val cardDir = File(cardListDir, cardInfo.title)
                if (!cardDir.exists()) cardDir.mkdirs()
                val cardFile = File(cardDir, cardFileName)
                if (!cardFile.exists()) cardFile.createNewFile()
                cardFile.writeText(Json.encodeToString(cardInfo))
            }
        }
    }

    fun loadBoardInfo(title: String): BoardInfo {
        val projectDir = File(projectsDir, title)
        if (!projectDir.exists()) return BoardInfo(title)

        val boardInfo = BoardInfo(title)
        projectDir.listFiles()?.forEach { cardListDir ->
            if (!cardListDir.exists()) return@forEach

            val cardListTitle = cardListDir.name.split(spacePosition)[0]
            val cardListPosition = cardListDir.name.split(spacePosition)[1].toInt()

            val cardListInfo = CardListInfo(cardListPosition, cardListTitle)
            boardInfo.cardListInfoList.add(cardListInfo)

            cardListDir.listFiles()?.forEach cardForEach@ { cardDir ->
                val cardFile = File(cardDir, cardFileName)
                if (!cardDir.exists() || !cardFile.exists()) return@cardForEach

                val readText = cardFile.readText()
                val cardInfo = Json.decodeFromString<CardInfo>(readText)

                cardListInfo.cardInfoList.add(cardInfo)
            }
        }

        return boardInfo
    }

    fun existsBoard(title: String): Boolean = File(projectsDir, title).exists()

    fun createBoardInfo(title: String) {
        if (existsBoard(title)) return

        val boardInfo = BoardInfo(title)
        saveBoardInfo(boardInfo)
    }

    fun deleteBoardInfo(title: String) {
        val projectDir = File(projectsDir, title)
        if (!projectDir.exists()) return

        projectDir.deleteRecursively()
    }

    fun getListBoardInfo(): Array<String> = projectsDir.list()?: arrayOf()
}