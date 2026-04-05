package com.rohit.myapplication.game

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TicTacToeScreen(viewModel: TicTacToeViewModel) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // MODE SELECTOR
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    viewModel.onAction(TicTacToeAction.ChangeMode(GameMode.SinglePlayer))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.gameMode == GameMode.SinglePlayer)
                        Color.Green else Color.Gray
                )
            ) {
                Text("Single")
            }

            Button(
                onClick = {
                    viewModel.onAction(TicTacToeAction.ChangeMode(GameMode.TwoPlayer))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.gameMode == GameMode.TwoPlayer)
                        Color.Green else Color.Gray
                )
            ) {
                Text("Two Player")
            }
        }

        // STATUS TEXT (🔥 celebration)
        Text(
            text = when (val status = state.gameStatus) {
                is GameStatus.Ongoing -> "Turn: ${state.currentPlayer}"
                is GameStatus.Win -> "🎉 ${status.player} Wins!"
                GameStatus.Draw -> "😅 Draw Game"
            },
            color = if (state.gameStatus is GameStatus.Win) Color.Yellow else Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // BOARD + WIN LINE
        Box {

            Column {
                for (row in 0..2) {
                    Row {
                        for (col in 0..2) {
                            val index = row * 3 + col
                            val cell = state.board[index]

                            val isWinning =
                                (state.gameStatus as? GameStatus.Win)
                                    ?.winPositions?.contains(index) == true

                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(6.dp)
                                    .background(
                                        if (isWinning) Color(0xFF22C55E)
                                        else Color(0xFF1E293B),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        viewModel.onAction(
                                            TicTacToeAction.MakeMove(index)
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {

                                AnimatedContent(
                                    targetState = cell,
                                    transitionSpec = {
                                        (scaleIn(spring()) + fadeIn())
                                            .togetherWith(fadeOut())
                                    },
                                    label = ""
                                ) {
                                    Text(
                                        text = it?.name ?: "",
                                        color = if (it == Player.X) Color.Cyan else Color.Magenta,
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 🔥 WINNING LINE
            if (state.gameStatus is GameStatus.Win) {

                val win = state.gameStatus as GameStatus.Win

                Canvas(
                    modifier = Modifier
                        .size(320.dp)
                ) {

                    val cellSize = size.width / 3

                    fun center(index: Int): Offset {
                        val row = index / 3
                        val col = index % 3
                        return Offset(
                            col * cellSize + cellSize / 2,
                            row * cellSize + cellSize / 2
                        )
                    }

                    val start = center(win.winPositions.first())
                    val end = center(win.winPositions.last())

                    drawLine(
                        color = Color.Yellow,
                        start = start,
                        end = end,
                        strokeWidth = 12f
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                viewModel.onAction(TicTacToeAction.ResetGame)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF22C55E)
            )
        ) {
            Text("Restart Game", color = Color.Black)
        }
    }
}