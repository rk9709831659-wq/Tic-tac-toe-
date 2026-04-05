package com.rohit.myapplication.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class Player { X, O }
enum class GameMode { SinglePlayer, TwoPlayer }

// 🔥 IMPORTANT CHANGE (winPositions added)
sealed class GameStatus {
    object Ongoing : GameStatus()
    data class Win(
        val player: Player,
        val winPositions: List<Int>
    ) : GameStatus()
    object Draw : GameStatus()
}

data class TicTacToeState(
    val board: List<Player?> = List(9) { null },
    val currentPlayer: Player = Player.X,
    val gameStatus: GameStatus = GameStatus.Ongoing,
    val gameMode: GameMode = GameMode.SinglePlayer
)

class TicTacToeViewModel : ViewModel() {

    private val _state = MutableStateFlow(TicTacToeState())
    val state: StateFlow<TicTacToeState> = _state.asStateFlow()

    fun onAction(action: TicTacToeAction) {
        when (action) {
            is TicTacToeAction.MakeMove -> makeMove(action.position)
            TicTacToeAction.ResetGame -> resetGame()
            is TicTacToeAction.ChangeMode -> changeMode(action.mode)
        }
    }

    private fun makeMove(position: Int) {
        val current = _state.value

        if (current.board[position] != null || current.gameStatus !is GameStatus.Ongoing) return

        val newBoard = current.board.toMutableList()
        newBoard[position] = current.currentPlayer

        val status = checkWinner(newBoard)
        val nextPlayer = if (current.currentPlayer == Player.X) Player.O else Player.X

        _state.value = current.copy(
            board = newBoard,
            currentPlayer = nextPlayer,
            gameStatus = status
        )

        // 🤖 AI only in single player
        if (status is GameStatus.Ongoing &&
            _state.value.gameMode == GameMode.SinglePlayer &&
            nextPlayer == Player.O
        ) {
            aiMove()
        }
    }

    private fun aiMove() {
        viewModelScope.launch {
            delay(500)

            val board = _state.value.board.toMutableList()
            val empty = board.indices.filter { board[it] == null }

            if (empty.isNotEmpty()) {
                val move = empty[Random.nextInt(empty.size)]
                board[move] = Player.O

                val status = checkWinner(board)

                _state.value = _state.value.copy(
                    board = board,
                    currentPlayer = Player.X,
                    gameStatus = status
                )
            }
        }
    }

    private fun checkWinner(board: List<Player?>): GameStatus {
        val wins = listOf(
            listOf(0,1,2), listOf(3,4,5), listOf(6,7,8),
            listOf(0,3,6), listOf(1,4,7), listOf(2,5,8),
            listOf(0,4,8), listOf(2,4,6)
        )

        for (w in wins) {
            if (board[w[0]] != null &&
                board[w[0]] == board[w[1]] &&
                board[w[1]] == board[w[2]]
            ) {
                // 🔥 return win with positions
                return GameStatus.Win(board[w[0]]!!, w)
            }
        }

        if (board.all { it != null }) return GameStatus.Draw

        return GameStatus.Ongoing
    }

    private fun resetGame() {
        _state.value = TicTacToeState(gameMode = _state.value.gameMode)
    }

    private fun changeMode(mode: GameMode) {
        _state.value = TicTacToeState(gameMode = mode)
    }
}

sealed class TicTacToeAction {
    data class MakeMove(val position: Int) : TicTacToeAction()
    object ResetGame : TicTacToeAction()
    data class ChangeMode(val mode: GameMode) : TicTacToeAction()
}