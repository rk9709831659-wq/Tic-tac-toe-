package com.rohit.myapplication.game

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohit.myapplication.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicTacToeScreen(viewModel: TicTacToeViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tic Tac Toe", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ModeSelector(
                selectedMode = state.gameMode,
                onModeSelected = { viewModel.onAction(TicTacToeAction.ChangeGameMode(it)) }
            )

            StatusCard(state = state)

            GameBoard(
                state = state,
                onCellClick = { viewModel.onAction(TicTacToeAction.MakeMove(it)) }
            )

            Button(
                onClick = { viewModel.onAction(TicTacToeAction.ResetGame) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Reset Game", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeSelector(
    selectedMode: GameMode,
    onModeSelected: (GameMode) -> Unit
) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        SegmentedButton(
            selected = selectedMode == GameMode.SinglePlayer,
            onClick = { onModeSelected(GameMode.SinglePlayer) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
        ) {
            Text("Single Player")
        }
        SegmentedButton(
            selected = selectedMode == GameMode.TwoPlayer,
            onClick = { onModeSelected(GameMode.TwoPlayer) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) {
            Text("Two Player")
        }
    }
}

@Composable
fun StatusCard(state: TicTacToeState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = state.gameStatus,
                transitionSpec = {
                    (scaleIn() + fadeIn()).togetherWith(scaleOut() + fadeOut())
                },
                label = "StatusAnimation"
            ) { status ->
                when (status) {
                    is GameStatus.Ongoing -> {
                        val playerText = if (state.currentPlayer == Player.X) "X's Turn" else "O's Turn"
                        val thinkingText = if (state.isAiThinking) " (AI thinking...)" else ""
                        Text(
                            text = playerText + thinkingText,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    is GameStatus.Win -> {
                        Text(
                            text = "${status.player} Wins!",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    GameStatus.Draw -> {
                        Text(
                            text = "It's a Draw!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameBoard(
    state: TicTacToeState,
    onCellClick: (Int) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(12.dp)
    ) {
        val cellSize = maxWidth / 3
        
        // Draw grid lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 4.dp.toPx()
            val color = Color.Gray.copy(alpha = 0.3f)
            
            // Vertical lines
            drawLine(color, Offset(cellSize.toPx(), 0f), Offset(cellSize.toPx(), size.height), strokeWidth, StrokeCap.Round)
            drawLine(color, Offset(cellSize.toPx() * 2, 0f), Offset(cellSize.toPx() * 2, size.height), strokeWidth, StrokeCap.Round)
            
            // Horizontal lines
            drawLine(color, Offset(0f, cellSize.toPx()), Offset(size.width, cellSize.toPx()), strokeWidth, StrokeCap.Round)
            drawLine(color, Offset(0f, cellSize.toPx() * 2), Offset(size.width, cellSize.toPx() * 2), strokeWidth, StrokeCap.Round)
        }

        Column {
            for (row in 0..2) {
                Row {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        val isWinningCell = (state.gameStatus as? GameStatus.Win)?.winPositions?.contains(index) == true
                        
                        GameCell(
                            player = state.board[index],
                            isWinningCell = isWinningCell,
                            onClick = { onCellClick(index) },
                            modifier = Modifier
                                .size(cellSize)
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameCell(
    player: Player?,
    isWinningCell: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isWinningCell -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        tonalElevation = if (isWinningCell) 8.dp else 2.dp,
        shadowElevation = if (isWinningCell) 4.dp else 1.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            AnimatedContent(
                targetState = player,
                transitionSpec = {
                    scaleIn(animationSpec = spring(Spring.DampingRatioMediumBouncy)).togetherWith(fadeOut())
                },
                label = "MarkAnimation"
            ) { targetPlayer ->
                when (targetPlayer) {
                    Player.X -> MarkX(color = if (isWinningCell) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                    Player.O -> MarkO(color = if (isWinningCell) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary)
                    null -> Spacer(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun MarkX(color: Color) {
    Canvas(modifier = Modifier.size(48.dp)) {
        val strokeWidth = 8.dp.toPx()
        drawLine(
            color = color,
            start = Offset(size.width * 0.2f, size.height * 0.2f),
            end = Offset(size.width * 0.8f, size.height * 0.8f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.8f, size.height * 0.2f),
            end = Offset(size.width * 0.2f, size.height * 0.8f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun MarkO(color: Color) {
    Canvas(modifier = Modifier.size(48.dp)) {
        drawCircle(
            color = color,
            radius = size.width * 0.35f,
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TicTacToePreview() {
    MyApplicationTheme {
        TicTacToeScreen(viewModel = TicTacToeViewModel())
    }
}
