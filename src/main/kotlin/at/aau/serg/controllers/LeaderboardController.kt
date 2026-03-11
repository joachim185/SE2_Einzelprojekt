package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.math.max
import kotlin.math.min

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    fun getLeaderboard(
        @RequestParam(required = false) rank: Int?
    ): ResponseEntity<List<GameResult>> {
        val sortedResults = gameResultService.getGameResults()
            .sortedWith(compareBy({ -it.score }, { it.timeInSeconds }))

        //wenn kein Rang angegeben ist, wird das gesamte Leaderbord zurückgegeben
        if (rank == null) {
            return ResponseEntity.ok(sortedResults)
        }

        // Wenn der Rang ungültig ist (zu kleine oder zu groß) wird HTTP 400 zurückgegeben
        if (rank <= 0 || rank > sortedResults.size) {
            return ResponseEntity.badRequest().build()
        }

        //Wenn der Rang passt: liste berechnen
        val index = rank - 1
        val startIndex = max(0, index - 3)
        val endIndex = min(sortedResults.size, index + 4) // +4, da endIndex bei subList exklusiv ist

        return ResponseEntity.ok(sortedResults.subList(startIndex, endIndex))
    }
}