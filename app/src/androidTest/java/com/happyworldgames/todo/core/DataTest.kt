package com.happyworldgames.todo.core

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataTest {

    private val context: Context = InstrumentationRegistry.getInstrumentation().context

    private fun getTestBoardInfo(): BoardInfo {
        val boardInfo = BoardInfo("TestBoard")

        val cardListInfo1 = CardListInfo(0, "Test1").apply {
            cardInfoList.add(CardInfo(0, "TestCard2", "TestDescription2"))
            cardInfoList.add(CardInfo(1, "TestCard3", "TestDescription3"))
            cardInfoList.add(CardInfo(2, "TestCard4", "TestDescription4"))
            cardInfoList.add(CardInfo(3, "TestCard5", "TestDescription5"))

        }
        val cardListInfo0 = CardListInfo(1, "TestCard0").apply {
            cardInfoList.add(CardInfo(0, "TestCard0", "TestDescription0"))
            cardInfoList.add(CardInfo(1, "TestCard1", "TestDescription1"))
        }

        boardInfo.cardListInfoList.add(cardListInfo0)
        boardInfo.cardListInfoList.add(cardListInfo1)

        boardInfo.cardListInfoList.sortBy { cardListInfo ->
            cardListInfo.position
        }

        return boardInfo
    }

    @Test
    fun saveBoardInfoTest() {
        val boardInfo = getTestBoardInfo()
        Data(context).saveBoardInfo(boardInfo)

        assert(true)

        getListBoardInfoTest()
        loadBoardInfoTestSuccessful()
        loadBoardInfoTestFailure()
    }

    private fun loadBoardInfoTestSuccessful() {
        val testBoardInfo = getTestBoardInfo()
        val boardInfo = Data(context).loadBoardInfo(testBoardInfo.title)

        Assert.assertEquals(testBoardInfo.title, boardInfo.title)

        boardInfo.cardListInfoList.forEachIndexed { index, cardListInfo ->
            Assert.assertEquals(testBoardInfo.cardListInfoList[index].title, cardListInfo.title)
            cardListInfo.cardInfoList.forEachIndexed { index2, cardInfo ->
                Assert.assertEquals(testBoardInfo.cardListInfoList[index].cardInfoList[index2].title, cardInfo.title)
                Assert.assertEquals(testBoardInfo.cardListInfoList[index].cardInfoList[index2].description, cardInfo.description)
            }
        }
    }

    private fun loadBoardInfoTestFailure() {
        val testBoardInfo = getTestBoardInfo()
        val boardInfo = Data(context).loadBoardInfo("Null")

        Assert.assertNotEquals(testBoardInfo.title, boardInfo.title)
        Assert.assertEquals(arrayListOf<CardListInfo>(), boardInfo.cardListInfoList)
        Assert.assertNotEquals(testBoardInfo.cardListInfoList, boardInfo.cardListInfoList)
    }

    private fun getListBoardInfoTest() {
        val testBoardInfo = getTestBoardInfo()
        val list = Data(context).getListBoardInfo()
        var found = false

        list.iterator().forEach { projectName ->
            if (projectName == testBoardInfo.title) {
                found = true
                return@forEach
            }
        }

        assert(found)
    }
}