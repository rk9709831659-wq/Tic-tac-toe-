package com.rohit.myapplication.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class Player { X, O }
enum class GameMode { SinglePlayer, TwoPlayer }

sealed class GameStatus {
    object Ongoing : GameStatus()
    data class Win(val player: Player, val winPositions: List<Int>) : GameStatus()
    object Draw : GameStatus()
}

data class TicTacToeState(
    val board: List<Player?> = List(9) { null },
    val currentPlayer: Player = Player.X,
    val gameStatus: GameStatus = GameStatus.Ongoing,
    val gameMode: GameMode = GameMode.SinglePlayer,
    val isAiThinking: Boolean = false
)

class TicTacToeViewModel : ViewModel() {

    private val _state = MutableStateFlow(TicTacToeState())
    val state: StateFlow<TicTacToeState> = _state.asStateFlow()

    fun onAction(action: TicTacToeAction) {
        when (action) {
            is TicTacToeAction.MakeMove -> makeMove(action.position)
            TicTacToeAction.ResetGame -> resetGame()
            is TicTacToeAction.ChangeGameMode -> changeGameMode(action.mode)
        }
    }

    private fun makeMove(position: Int) {
        val currentState = _state.value
        if (currentState.board[position] != null || currentState.gameStatus !is GameStatus.Ongoing || currentState.isAiThinking) {
            return
        }

        val newBoard = currentState.board.toMutableList()
        newBoard[position] = currentState.currentPlayer

        val newStatus = checkGameStatus(newBoard)
        val nextPlayer = if (currentState.currentPlayer == Player.X) Player.O else Player.X

        _state.update {
            it.copy(
                board = newBoard,
                currentPlayer = nextPlayer,
                gameStatus = newStatus
            )
        }

        if (newStatus is GameStatus.Ongoing && _state.value.gameMode == GameMode.SinglePlayer && nextPlayer == Player.O) {
            triggerAiMove()
        }
    }

    private fun triggerAiMove() {
        viewModelScope.launch {
            _state.update { it.copy(isAiThinking = true) }
            delay(600) // Simulate thinking
            val aiMove = getAiMove(_state.value.board)
            if (aiMove != -1) {
                val newBoard = _state.value.board.toMutableList()
                newBoard[aiMove] = Player.O
                val newStatus = checkGameStatus(newBoard)
                _state.update {
                    it.copy(
                        board = newBoard,
                        currentPlayer = Player.X,
                        gameStatus = newStatus,
                        isAiThinking = false
                    )
                }
            } else {
                _state.update { it.copy(isAiThinking = false) }
            }
        }
    }

    private fun getAiMove(board: List<Player?>): Int {
        // 1. Try to win
        findWinningMove(board, Player.O)?.let { return it }
        // 2. Block player X
        findWinningMove(board, Player.X)?.let { return it }
        // 3. Take center
        if (board[4] == null) return 4
        // 4. Random available
        val available = board.indices.filter { board[it] == null }
        return if (available.isNotEmpty()) available[Random.nextInt(available.size)] else -1
    }

    private fun findWinningMove(board: List<Player?>, player: Player): Int? {
        val winPatterns = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Cols
            listOf(0, 4, 8), listOf(2, 4, 6) // Diagonals
        )
        for (pattern in winPatterns) {
            val count = pattern.count { board[it] == player }
            val emptyIndex = pattern.firstOrNull { board[it] == null }
            if (count == 2 && emptyIndex != null) {
                return emptyIndex
            }
        }
        return null
    }

    private fun checkGameStatus(board: List<Player?>): GameStatus {
        val winPatterns = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )

        for (pattern in winPatterns) {
            if (board[pattern[0]] != null &&
                board[pattern[0]] == board[pattern[1]] &&
                board[pattern[0]] == board[pattern[2]]
            ) {
                return GameStatus.Win(board[pattern[0]]!!, pattern)
            }
        }

        if (board.all { it != null }) {
            return GameStatus.Draw
        }

        return GameStatus.Ongoing
    }

    private fun resetGame() {
        _state.update {
            TicTacToeState(gameMode = it.gameMode)
        }
    }

    private fun changeGameMode(mode: GameMode) {
        _state.update {
            TicTacToeState(gameMode = mode)
        }
    }
}

sealed class TicTacToeAction {
    data class MakeMove(val position: Int) : TicTacToeAction()
    object ResetGame : TicTacToeAction()
    data class ChangeGameMode(val mode: GameMode) : TicTacToeAction()
}
