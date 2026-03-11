package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.mockito.Mockito.`when` as whenever

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    // --- 1. Tests für "Kein Rang" (angepasste Original-Tests) ---

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        // null übergeben und ResponseEntity prüfen
        val response = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(HttpStatus.OK, response.statusCode)

        val res = response.body!!
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_sameScore_CorrectTimeInSecondsSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        // null übergeben und ResponseEntity prüfen
        val response = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(HttpStatus.OK, response.statusCode)

        val res = response.body!!
        assertEquals(3, res.size)
        assertEquals(first, res[2])
        assertEquals(second, res[0])
        assertEquals(third, res[1])
    }

    // --- 2. Tests für "Rang passt" (gültige Parameter & Sublisten) ---

    @Test
    fun test_getLeaderboard_validRank_returnsCorrectSubset() {
        // Mock-Daten: 10 Spieler generieren, absteigend sortiert (Score 99 bis 90)
        val results = (1..10).map {
            GameResult(it.toLong(), "Player $it", 100 - it, it.toDouble())
        }
        whenever(mockedService.getGameResults()).thenReturn(results)

        // Abfrage für Platz 5. Erwartet: Plätze 2, 3, 4, [5], 6, 7, 8 (Insgesamt 7 Elemente)
        val response = controller.getLeaderboard(5)

        assertEquals(HttpStatus.OK, response.statusCode)

        val res = response.body!!
        assertEquals(7, res.size)
        assertEquals("Player 2", res[0].playerName) // Oberer Rand (-3)
        assertEquals("Player 5", res[3].playerName) // Angefragter Spieler (Mitte)
        assertEquals("Player 8", res[6].playerName) // Unterer Rand (+3)
    }

    @Test
    fun test_getLeaderboard_validRankAtEdge_returnsBoundedSubset() {
        val results = (1..5).map {
            GameResult(it.toLong(), "Player $it", 100 - it, it.toDouble())
        }
        whenever(mockedService.getGameResults()).thenReturn(results)

        // Abfrage für Platz 1 (Randfall: Keine oberen Nachbarn existieren)
        val response = controller.getLeaderboard(1)

        assertEquals(HttpStatus.OK, response.statusCode)

        val res = response.body!!
        assertEquals(4, res.size) // Nur Platz 1, 2, 3, 4
        assertEquals("Player 1", res[0].playerName)
        assertEquals("Player 4", res[3].playerName)
    }

    // --- 3. Tests für "Falscher Rang" (Fehlerfälle -> HTTP 400) ---

    @Test
    fun test_getLeaderboard_invalidRankTooSmall_returnsBadRequest() {
        val results = listOf(GameResult(1, "p1", 10, 5.0))
        whenever(mockedService.getGameResults()).thenReturn(results)

        // Ränge <= 0 sind fachlich ungültig
        val responseZero = controller.getLeaderboard(0)
        val responseNegative = controller.getLeaderboard(-1)

        assertEquals(HttpStatus.BAD_REQUEST, responseZero.statusCode)
        assertNull(responseZero.body)

        assertEquals(HttpStatus.BAD_REQUEST, responseNegative.statusCode)
        assertNull(responseNegative.body)
    }

    @Test
    fun test_getLeaderboard_invalidRankTooLarge_returnsBadRequest() {
        val results = listOf(
            GameResult(1, "p1", 10, 5.0),
            GameResult(2, "p2", 10, 5.0),
            GameResult(3, "p3", 10, 5.0)
        )
        whenever(mockedService.getGameResults()).thenReturn(results)

        // Abfrage für Platz 4 bei einer Listenlänge von 3
        val response = controller.getLeaderboard(4)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertNull(response.body)
    }
}