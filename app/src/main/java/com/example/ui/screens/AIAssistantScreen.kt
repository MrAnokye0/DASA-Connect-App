package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.EventViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    viewModel: EventViewModel,
    modifier: Modifier = Modifier
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val suggestedQuestions = listOf(
        "Who is Dr. Elikem Adonoo?",
        "Recommend policy sessions",
        "Who are the Platinum sponsors?",
        "Explain CBDC lessons"
    )

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(chatMessages.size, isChatLoading) {
        if (chatMessages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(chatMessages.size - 1)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
    ) {
        // AI Header / Quick Stats
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DasaDeepNavy)
                .border(BorderStroke(1.dp, BorderColor))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(DasaEmerald.copy(alpha = 0.15f), CircleShape)
                        .border(1.dp, DasaEmerald, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Assistant Active",
                        tint = DasaEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "DASA SUMMIT INTELLECT",
                        style = MaterialTheme.typography.titleMedium,
                        color = LuxuryTextLight,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(DasaEmerald, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Gemini 3.5 Flash Engine Online",
                            style = MaterialTheme.typography.labelSmall,
                            color = DasaEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Chat Message Area
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(chatMessages) { message ->
                val isAi = message.sender == "ai"
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
                ) {
                    if (isAi) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(DasaGold.copy(alpha = 0.15f), CircleShape)
                                .border(1.dp, DasaGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, tint = DasaGold, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Card Bubble representation
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isAi) 4.dp else 16.dp,
                            bottomEnd = if (isAi) 16.dp else 4.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAi) LuxuryCardBg else DasaDeepNavy
                        ),
                        border = BorderStroke(1.dp, if (isAi) BorderColor else DasaGold.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .testTag(if (isAi) "ai_message_bubble" else "user_message_bubble")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = LuxuryTextLight,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            if (isChatLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(DasaGold.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = DasaGold, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Analyzing database models...",
                            style = MaterialTheme.typography.bodySmall,
                            color = LuxuryTextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Quick suggestions Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(suggestedQuestions) { question ->
                Box(
                    modifier = Modifier
                        .background(LuxuryCardBg, RoundedCornerShape(100.dp))
                        .border(1.dp, BorderColor, RoundedCornerShape(100.dp))
                        .clickable { viewModel.askAiAssistant(question) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = question,
                        style = MaterialTheme.typography.bodySmall,
                        color = DasaGold,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Text input row
        Surface(
            color = DasaDeepNavy,
            tonalElevation = 4.dp,
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding()
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Ask anything about DASA 2026...", color = LuxuryTextMuted) },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DasaGold,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = LuxuryTextLight,
                        unfocusedTextColor = LuxuryTextLight,
                        focusedContainerColor = LuxuryDarkBg,
                        unfocusedContainerColor = LuxuryDarkBg
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_chat_input")
                )

                Spacer(modifier = Modifier.width(12.dp))

                FloatingActionButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.askAiAssistant(textInput)
                            textInput = ""
                        }
                    },
                    containerColor = DasaGold,
                    contentColor = DasaDeepNavy,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("ai_send_button")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send prompt", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
