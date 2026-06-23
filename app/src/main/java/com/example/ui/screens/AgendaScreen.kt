package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventSession
import com.example.ui.theme.*
import com.example.ui.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    viewModel: EventViewModel,
    selectedSessionState: EventSession?,
    onSelectSession: (EventSession?) -> Unit,
    modifier: Modifier = Modifier
) {
    val allSessions by viewModel.allSessions.collectAsState()
    val bookmarkedSessions by viewModel.bookmarkedSessions.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0=Day 1, 1=Day 2, 2=Bookmarks
    val tabs = listOf("Day 1 (Oct 12)", "Day 2 (Oct 13)", "Bookmarked")

    val displayedSessions = when (selectedTab) {
        0 -> allSessions.filter { it.date.contains("Day 1") }
        1 -> allSessions.filter { it.date.contains("Day 2") }
        else -> bookmarkedSessions
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
    ) {
        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DasaDeepNavy,
            contentColor = DasaGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = DasaGold
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    },
                    selectedContentColor = DasaGold,
                    unselectedContentColor = LuxuryTextMuted
                )
            }
        }

        if (displayedSessions.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (selectedTab == 2) Icons.Default.BookmarkBorder else Icons.Default.Event,
                    contentDescription = "Empty State Icon",
                    tint = LuxuryTextMuted.copy(alpha = 0.5f),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (selectedTab == 2) "No Bookmarks Yet" else "No Sessions Scheduled",
                    style = MaterialTheme.typography.titleMedium,
                    color = LuxuryTextLight,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (selectedTab == 2) 
                        "Tap the bookmark icon on any agenda session to save it to your personal schedule." 
                        else "Stay tuned for official schedule releases.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LuxuryTextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("sessions_list")
            ) {
                items(displayedSessions, key = { it.id }) { session ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectSession(session) }
                            .testTag("session_card_${session.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LuxuryCardBg),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Header: Category, Time slot & Bookmark icon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(DasaGold.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = session.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DasaGold,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${session.startTime} - ${session.endTime}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LuxuryTextMuted,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.toggleBookmark(session.id, !session.isBookmarked) },
                                    modifier = Modifier.testTag("bookmark_button_${session.id}")
                                ) {
                                    Icon(
                                        imageVector = if (session.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Toggle Bookmark",
                                        tint = if (session.isBookmarked) DasaGold else LuxuryTextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Session Title
                            Text(
                                text = session.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = LuxuryTextLight,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Session description preview
                            Text(
                                text = session.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = LuxuryTextMuted,
                                maxLines = 2,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = BorderColor, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Speaker & Location Footer
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = DasaGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = session.speakerName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LuxuryTextLight,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = DasaEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = session.location,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LuxuryTextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Session Details Dialog with Ratings & Feedback
    if (selectedSessionState != null) {
        val session = selectedSessionState
        var localRating by remember(session.id) { mutableStateOf(session.rating) }
        var localComment by remember(session.id) { mutableStateOf(session.feedbackComment) }
        var showRatingConfirmation by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { onSelectSession(null) },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Session Intelligence",
                        color = DasaGold,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { onSelectSession(null) }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = LuxuryTextMuted)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .background(LuxuryCardBg)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = session.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = LuxuryTextLight,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "📍 ${session.location}",
                            style = MaterialTheme.typography.bodySmall,
                            color = DasaGold,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "🗓️ ${session.date}",
                            style = MaterialTheme.typography.bodySmall,
                            color = LuxuryTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "EXECUTIVE SUMMARY",
                        style = MaterialTheme.typography.labelSmall,
                        color = DasaGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = session.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LuxuryTextLight,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "FEATURED SPEAKER",
                        style = MaterialTheme.typography.labelSmall,
                        color = DasaGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = LuxuryDarkBg),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(DasaGold.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = DasaGold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = session.speakerName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LuxuryTextLight,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Summit Distinguished Expert",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LuxuryTextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = BorderColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Feedback Segment
                    Text(
                        text = "DELEGATE SESSION RATINGS & FEEDBACK",
                        style = MaterialTheme.typography.labelSmall,
                        color = DasaGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 5-Star interactive system
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        (1..5).forEach { index ->
                            val isSelected = index <= localRating
                            Icon(
                                imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Rate $index Stars",
                                tint = if (isSelected) DasaGold else LuxuryTextMuted,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { localRating = index.toFloat() }
                                    .padding(4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = localComment,
                        onValueChange = { localComment = it },
                        label = { Text("Executive Comments / Feedback") },
                        placeholder = { Text("Enter your session review here...") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DasaGold,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = LuxuryTextLight,
                            unfocusedTextColor = LuxuryTextLight,
                            unfocusedLabelColor = LuxuryTextMuted
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.submitFeedback(session.id, localRating, localComment)
                            showRatingConfirmation = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DasaGold, contentColor = DasaDeepNavy),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Submit Evaluation Signature", fontWeight = FontWeight.Bold)
                    }

                    if (showRatingConfirmation) {
                        Text(
                            text = "✓ Assessment submitted & securely cataloged.",
                            color = DasaEmerald,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {},
            containerColor = LuxuryCardBg,
            shape = RoundedCornerShape(24.dp)
        )
    }
}
