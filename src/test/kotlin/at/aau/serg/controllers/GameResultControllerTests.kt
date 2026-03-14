package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.mockito.Mockito.`when` as whenever

class GameResultControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: GameResultController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = GameResultController(mockedService)
    }

    @Test
    fun test_getGameResult_existingId_returnsResult() {
        val expectedResult = GameResult(1, "Player 1", 50, 10.5)
        whenever(mockedService.getGameResult(1L)).thenReturn(expectedResult)

        
        val actualResult = controller.getGameResult(1L)

        
        verify(mockedService).getGameResult(1L)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun test_getGameResult_nonexistentId_returnsNull() {
        whenever(mockedService.getGameResult(99L)).thenReturn(null)

        
        val actualResult = controller.getGameResult(99L)

        
        verify(mockedService).getGameResult(99L)
        assertNull(actualResult)
    }

    @Test
    fun test_getAllGameResults_returnsList() {
        val expectedList = listOf(
            GameResult(1, "Player 1", 50, 10.5),
            GameResult(2, "Player 2", 40, 11.0)
        )
        whenever(mockedService.getGameResults()).thenReturn(expectedList)

        
        val actualList = controller.getAllGameResults()

        
        verify(mockedService).getGameResults()
        assertEquals(expectedList, actualList)
        assertEquals(2, actualList.size)
    }

    @Test
    fun test_addGameResult_callsService() {
        val newResult = GameResult(0, "New Player", 100, 5.0)

        
        controller.addGameResult(newResult)


        verify(mockedService).addGameResult(newResult)
    }

    @Test
    fun test_deleteGameResult_callsService() {
        val idToDelete = 5L

        
        controller.deleteGameResult(idToDelete)

        verify(mockedService).deleteGameResult(idToDelete)
    }
}