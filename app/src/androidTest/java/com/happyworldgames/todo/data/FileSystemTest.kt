package com.happyworldgames.todo.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.happyworldgames.todo.model.BoardInfo
import com.happyworldgames.todo.model.ListInfo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class FileSystemTest {
    private lateinit var fileSystem: FileSystem

    @Before
    fun createFileSystem() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        fileSystem = FileSystem(appContext)
    }

    private fun testGetBoards(): Boolean {
        var isTestBoard = false
        fileSystem.getBoards().forEach { infoInterface ->
            if(infoInterface.id == "TestId") isTestBoard = true
        }
        return isTestBoard
    }
    @Test
    fun saveAndLoad() {
        assertEquals(false, testGetBoards())            // check for have board
        val saveBoardInfo = BoardInfo("TestId", 255, "TestName")
        fileSystem.saveBoard(saveBoardInfo)                     // save board
        assertEquals(true, testGetBoards())             // check for have board

        val loadBoardInfo = fileSystem.getBoard("TestId")   // load board
        assertEquals(saveBoardInfo, loadBoardInfo)             // check save with load board

        val saveListInfo = ListInfo("TestListId", 2, "TestListName")
        fileSystem.saveList(saveBoardInfo, saveListInfo)
        //assertEquals()
        TODO()
    }

    @After
    fun deleteBoard() {
        assertEquals(true, testGetBoards())
        fileSystem.deleteBoard("TestId")
        assertEquals(false, testGetBoards())
    }
}