package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Attendee
import com.example.ui.theme.*
import com.example.ui.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkingScreen(
    viewModel: EventViewModel,
    modifier: Modifier = Modifier
) {
    val attendees by viewModel.allAttendees.collectAsState()
    val aiRecommendation by viewModel.aiRecommendation.collectAsState()
    val isRecLoading by viewModel.isRecommendationLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedAttendeeForCard by remember { mutableStateOf<Attendee?>(null) }

    val filteredAttendees = attendees.filter { attendee ->
        attendee.name.contains(searchQuery, ignoreCase = true) ||
        attendee.company.contains(searchQuery, ignoreCase = true) ||
        attendee.title.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
    ) {
        // AI MATCHMAKER HEADER
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("ai_matchmaker_header"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DasaDeepNavy),
            border = BorderStroke(1.dp, DasaGold.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Matchmaker",
                        tint = DasaGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DASA AI INTELLIGENT MATCHMAKER",
                        style = MaterialTheme.typography.labelSmall,
                        color = DasaGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Generate executive networking recommendations tailored specifically to your professional profile using Gemini neural insights.",
                    style = MaterialTheme.typography.bodySmall,
                    color = LuxuryTextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isRecLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = DasaGold, modifier = Modifier.size(24.dp))
                    }
                } else if (aiRecommendation.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LuxuryCardBg, RoundedCornerShape(12.dp))
                            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = aiRecommendation,
                            style = MaterialTheme.typography.bodySmall,
                            color = LuxuryTextLight,
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = { viewModel.generateNetworkingRecommendations() },
                    colors = ButtonDefaults.buttonColors(containerColor = DasaGold, contentColor = DasaDeepNavy),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Text(
                        text = if (aiRecommendation.isNotBlank()) "Regenerate Recommendations" else "Compute Ideal Connections",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name, company, or title...", color = LuxuryTextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DasaGold) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = DasaGold)
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DasaGold,
                unfocusedBorderColor = BorderColor,
                focusedTextColor = LuxuryTextLight,
                unfocusedTextColor = LuxuryTextLight,
                focusedContainerColor = LuxuryCardBg,
                unfocusedContainerColor = LuxuryCardBg
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("attendee_search")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Attendee List Header
        Text(
            text = "ATTENDEE DIRECTORY (${filteredAttendees.size})",
            style = MaterialTheme.typography.labelMedium,
            color = DasaGold,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )

        if (filteredAttendees.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = null,
                    tint = LuxuryTextMuted.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No matching attendees found",
                    color = LuxuryTextLight,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredAttendees, key = { it.id }) { attendee ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedAttendeeForCard = attendee }
                            .testTag("attendee_card_${attendee.id}"),
                        colors = CardDefaults.cardColors(containerColor = LuxuryCardBg),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Monogram Avatar Circle
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(DasaDeepNavy, RoundedCornerShape(8.dp))
                                        .border(1.dp, DasaGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = attendee.name.split(" ").map { it.take(1) }.joinToString("").take(2).uppercase(),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = DasaGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = attendee.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = LuxuryTextLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${attendee.title} at ${attendee.company}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LuxuryTextMuted
                                    )
                                }
                            }

                            // Connection button logic
                            when (attendee.connectionStatus) {
                                "none" -> {
                                    Button(
                                        onClick = { viewModel.sendConnectionRequest(attendee.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = DasaGold, contentColor = DasaDeepNavy),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("connect_button_${attendee.id}")
                                    ) {
                                        Text("Connect", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                "pending_sent" -> {
                                    OutlinedButton(
                                        onClick = { viewModel.removeConnection(attendee.id) },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LuxuryTextMuted),
                                        border = BorderStroke(1.dp, BorderColor),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Pending", fontSize = 11.sp)
                                    }
                                }
                                "pending_received" -> {
                                    Button(
                                        onClick = { viewModel.acceptConnection(attendee.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = DasaEmerald, contentColor = Color.White),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Accept", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                "connected" -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { selectedAttendeeForCard = attendee }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContactMail,
                                            contentDescription = "Business Card Connected",
                                            tint = DasaEmerald,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Card", fontSize = 11.sp, color = DasaEmerald, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Business Card Dialog
    if (selectedAttendeeForCard != null) {
        val attendee = selectedAttendeeForCard!!
        AlertDialog(
            onDismissRequest = { selectedAttendeeForCard = null },
            title = {
                Text(
                    text = "DIGITAL BUSINESS CARD",
                    style = MaterialTheme.typography.titleMedium,
                    color = DasaGold,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DasaGold, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = DasaDeepNavy),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(LuxuryDarkBg, RoundedCornerShape(12.dp))
                                .border(1.5.dp, DasaGold, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = attendee.name.split(" ").map { it.take(1) }.joinToString("").take(2).uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                color = DasaGold,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = attendee.name.uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = LuxuryTextLight,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = attendee.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DasaGold,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = attendee.company,
                            style = MaterialTheme.typography.bodySmall,
                            color = LuxuryTextMuted
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = BorderColor)
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = DasaEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "OFFICIAL ${attendee.role.uppercase()} BADGE",
                                style = MaterialTheme.typography.labelSmall,
                                color = DasaEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Connection Details
                        if (attendee.connectionStatus == "connected") {
                            Text(
                                text = attendee.digitalCard,
                                style = MaterialTheme.typography.bodyMedium,
                                color = LuxuryTextLight,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                text = "🔒 Connect with ${attendee.name} to exchange and unlock electronic business card details securely.",
                                style = MaterialTheme.typography.bodySmall,
                                color = LuxuryTextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedAttendeeForCard = null },
                    colors = ButtonDefaults.buttonColors(containerColor = DasaGold, contentColor = DasaDeepNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close Card Dashboard")
                }
            },
            containerColor = LuxuryCardBg,
            shape = RoundedCornerShape(24.dp)
        )
    }
}
