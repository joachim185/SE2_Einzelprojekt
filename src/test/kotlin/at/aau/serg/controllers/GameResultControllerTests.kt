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

        // Aktion
        val actualResult = controller.getGameResult(1L)

        // Verifizieren
        verify(mockedService).getGameResult(1L)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun test_getGameResult_nonexistentId_returnsNull() {
        whenever(mockedService.getGameResult(99L)).thenReturn(null)

        // Aktion
        val actualResult = controller.getGameResult(99L)

        // Verifizieren
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

        // Aktion
        val actualList = controller.getAllGameResults()

        // Verifizieren
        verify(mockedService).getGameResults()
        assertEquals(expectedList, actualList)
        assertEquals(2, actualList.size)
    }

    @Test
    fun test_addGameResult_callsService() {
        val newResult = GameResult(0, "New Player", 100, 5.0)

        // Aktion
        controller.addGameResult(newResult)

        // Verifizieren, dass der Service mit genau diesem Objekt aufgerufen wurde
        verify(mockedService).addGameResult(newResult)
    }

    @Test
    fun test_deleteGameResult_callsService() {
        val idToDelete = 5L

        // Aktion
        controller.deleteGameResult(idToDelete)

        // Verifizieren, dass der Service mit genau dieser ID aufgerufen wurde
        verify(mockedService).deleteGameResult(idToDelete)
    }
}