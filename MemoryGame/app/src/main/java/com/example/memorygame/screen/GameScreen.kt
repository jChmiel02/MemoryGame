package com.example.memorygame.screen

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.memorygame.R
import com.example.memorygame.data.DatabaseHelper
import com.example.memorygame.data.Quadruple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameScreen(databaseHelper: DatabaseHelper) {
    var currentScreen by remember { mutableStateOf("menu") }
    var playerName by remember { mutableStateOf("") }
    var showEndScreen by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler(enabled = true) {
        if (currentScreen == "menu") {
            (context as? Activity)?.finish()
        } else {
            currentScreen = "menu"
            showEndScreen = false
        }
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background_image),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            when (currentScreen) {
                "menu" -> MainMenuScreen(
                    onStartGame = { currentScreen = "nameInput" },
                    onShowScores = { currentScreen = "scores" }
                )
                "nameInput" -> NameInputScreen(
                    defaultName = playerName,
                    onNameEntered = { name ->
                        playerName = name
                        currentScreen = "game"
                    },
                    onCancel = { currentScreen = "menu" }
                )
                "game" -> GamePlayScreen(
                    databaseHelper = databaseHelper,
                    playerName = playerName,
                    onGameEnd = { showEndScreen = true }
                )
                "scores" -> BestScoresScreen(
                    databaseHelper = databaseHelper,
                    onBack = { currentScreen = "menu" }
                )
            }
            if (showEndScreen) {
                EndGameScreen(
                    onRestart = {
                        currentScreen = "nameInput"
                        showEndScreen = false
                    },
                    onExit = {
                        currentScreen = "menu"
                        showEndScreen = false
                    }
                )
            }

        }
    }
}

@Composable
fun MainMenuScreen(onStartGame: () -> Unit, onShowScores: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Card(
            onClick = onStartGame,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.start_game),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Card(
            onClick = onShowScores,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.show_scores),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

@Composable
fun NameInputScreen(defaultName: String = "", onNameEntered: (String) -> Unit, onCancel: () -> Unit) {
    var name by remember { mutableStateOf(defaultName) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.enter_name),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.player_name)) },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { if (name.isNotBlank()) onNameEntered(name) },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text(text = stringResource(R.string.confirm))
        }
        Button(onClick = onCancel, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(text = stringResource(R.string.cancel))
        }
    }
}


@Composable
fun GamePlayScreen(
    databaseHelper: DatabaseHelper,
    playerName: String,
    onGameEnd: () -> Unit
) {
    val context = LocalContext.current
    val cards by remember { mutableStateOf(generateShuffledCards()) }
    val flippedCards = remember { mutableStateListOf<Int>() }
    val matchedCards = remember { mutableStateListOf<Int>() }
    var moves by remember { mutableIntStateOf(0) }
    var time by remember { mutableIntStateOf(0) }
    var gameStarted by remember { mutableStateOf(false) }
    var gameFinished by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var errors by remember { mutableIntStateOf(0) }

    LaunchedEffect(gameStarted, gameFinished) {
        if (gameStarted && !gameFinished) {
            while (!gameFinished) {
                delay(1000)
                time++
            }
        }
    }

    LaunchedEffect(Unit) {
        flippedCards.addAll(cards.indices)
        delay(1000)
        flippedCards.clear()
    }

    fun onCardClick(index: Int) {
        if (!gameStarted) {
            gameStarted = true
        }

        if (flippedCards.size < 2 && !flippedCards.contains(index) && !matchedCards.contains(index)) {
            flippedCards.add(index)

            if (flippedCards.size == 2) {
                moves++
                val firstIndex = flippedCards[0]
                val secondIndex = flippedCards[1]

                if (cards[firstIndex] == cards[secondIndex]) {
                    matchedCards.addAll(flippedCards)
                    flippedCards.clear()

                    if (matchedCards.size == cards.size) {
                        gameFinished = true
                        coroutineScope.launch {
                            gameOver(context, databaseHelper, playerName, moves, time, errors)
                        }
                        onGameEnd()
                    }
                } else {
                    errors++
                    coroutineScope.launch {
                        delay(1000)
                        flippedCards.clear()
                    }
                }
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.moves, moves),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.time, time),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.errors, errors),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            cards.chunked(4).forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    row.forEachIndexed { columnIndex, card ->
                        val cardIndex = rowIndex * 4 + columnIndex
                        val isFlipped = flippedCards.contains(cardIndex) || matchedCards.contains(cardIndex)

                        Card(
                            onClick = { if (!gameFinished) onCardClick(cardIndex) },
                            modifier = Modifier.size(60.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isFlipped) card else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
@Composable
fun EndGameScreen(onRestart: () -> Unit, onExit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onRestart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = stringResource(R.string.restart))
            }
            Button(
                onClick = onExit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = stringResource(R.string.exit))
            }
        }
    }
}
@Composable
fun BestScoresScreen(databaseHelper: DatabaseHelper, onBack: () -> Unit) {
    val scores = remember { mutableStateListOf<Quadruple<String, Int, Int, Int>>() }
    LaunchedEffect(Unit) {
        scores.addAll(databaseHelper.getTopScores())
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = stringResource(R.string.best_scores), style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn {
            items(scores.size) { index ->
                val (playerName, moves, time, errors) = scores[index]
                Text(
                    text = stringResource(R.string.player_score, playerName, moves, time, errors),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onBack) {
            Text(text = stringResource(R.string.back_to_menu))
        }
    }
}


private fun generateShuffledCards(): List<Color> {
    val colors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow,
        Color.Magenta, Color.Cyan, Color.Gray, Color.Black
    )
    return (colors + colors).shuffled()
}
@Suppress("DEPRECATION")
private suspend fun gameOver(context: Context, databaseHelper: DatabaseHelper, playerName: String, moves: Int, time: Int, errors: Int) {
    val gameOverMessage = context.getString(R.string.game_over_message, moves, time, errors)
    val toast = Toast.makeText(context, gameOverMessage, Toast.LENGTH_LONG)
    val textView = TextView(context)
    textView.text = gameOverMessage
    textView.textSize = 24f
    textView.gravity = Gravity.CENTER
    toast.view = textView
    toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER, 0, 300)
    toast.show()
    databaseHelper.insertRecord(playerName, moves, time, errors)
}


