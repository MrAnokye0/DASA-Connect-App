package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventSession
import com.example.ui.theme.*
import com.example.ui.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: EventViewModel,
    onNavigateToTab: (Int) -> Unit, // bottom navigation indices: 0=Home, 1=Agenda, 2=Networking, 3=Directory, 4=AI, 5=Badge
    onSelectSession: (EventSession) -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val announcements by viewModel.allAnnouncements.collectAsState()
    val sessions by viewModel.allSessions.collectAsState()

    val profile = userProfile ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // 1. Welcome Executive Top Header / Hero Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DasaDeepNavy, LuxuryDarkBg)
                    )
                )
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DASA CONNECT",
                            style = MaterialTheme.typography.labelMedium,
                            color = DasaGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Welcome Back,",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LuxuryTextMuted
                        )
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = LuxuryTextLight,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Badge classification pill
                    Box(
                        modifier = Modifier
                            .background(DasaGold.copy(alpha = 0.12f), RoundedCornerShape(100.dp))
                            .border(1.dp, DasaGold, RoundedCornerShape(100.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = profile.role.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = DasaGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Golden Card for QR quick launcher
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("qr_quick_launcher")
                        .clickable { onNavigateToTab(5) }, // Navigate to digital badge tab
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxuryCardBg),
                    border = BorderStroke(1.dp, DasaGold.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(DasaGold.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .border(1.dp, DasaGold, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "Quick QR Badge Icon",
                                tint = DasaGold,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Digital Delegate Badge",
                                style = MaterialTheme.typography.titleMedium,
                                color = LuxuryTextLight,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tap to present signature QR Code for networking & scanning",
                                style = MaterialTheme.typography.bodySmall,
                                color = LuxuryTextMuted
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Go to Badge",
                            tint = DasaGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // 2. Announcements & News Highlights Section
        if (announcements.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Official Announcements",
                        style = MaterialTheme.typography.titleMedium,
                        color = LuxuryTextLight,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Announcements Icon",
                        tint = DasaOrange,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Display top announcement
                val topAnnouncement = announcements.first()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("announcement_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxuryCardBg),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(DasaOrange, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = topAnnouncement.category.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = DasaOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = topAnnouncement.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = LuxuryTextLight,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = topAnnouncement.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = LuxuryTextMuted
                        )
                    }
                }
            }
        }

        // 3. Featured Conference Sessions (Day 1 keynotes/summits)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Featured Agenda Sessions",
                    style = MaterialTheme.typography.titleMedium,
                    color = LuxuryTextLight,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = { onNavigateToTab(1) }) {
                    Text("View Full Agenda", color = DasaGold, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal LazyRow of Sessions
            val featuredSessions = sessions.filter { it.category == "Policy" || it.category == "CBDC" }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(featuredSessions) { session ->
                    Card(
                        modifier = Modifier
                            .width(280.dp)
                            .testTag("featured_session_${session.id}")
                            .clickable { onSelectSession(session) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LuxuryCardBg),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            // Category Tag + Time
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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

                                Text(
                                    text = session.startTime,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LuxuryTextMuted,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = session.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = LuxuryTextLight,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.height(48.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Divider line
                            HorizontalDivider(color = BorderColor, thickness = 1.dp)

                            Spacer(modifier = Modifier.height(12.dp))

                            // Speaker & Location row
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
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = session.speakerName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LuxuryTextMuted,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = DasaEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = session.location.split("(").first().trim(),
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

        // 4. Summit Highlights / Live Quick Actions Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Summit Assistant & Quick Services",
                style = MaterialTheme.typography.titleMedium,
                color = LuxuryTextLight,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // AI Assistant Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_assistant")
                        .clickable { onNavigateToTab(4) }, // AI chatbot index
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxuryCardBg),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(DasaEmerald.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Assistant Icon",
                                tint = DasaEmerald,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "DASA AI Assistant",
                            style = MaterialTheme.typography.titleSmall,
                            color = LuxuryTextLight,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ask summit info, summary, guidelines",
                            style = MaterialTheme.typography.bodySmall,
                            color = LuxuryTextMuted
                        )
                    }
                }

                // Connections Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_networking")
                        .clickable { onNavigateToTab(2) }, // Networking index
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxuryCardBg),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(DasaGold.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = "Networking Hub Icon",
                                tint = DasaGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Networking Hub",
                            style = MaterialTheme.typography.titleSmall,
                            color = LuxuryTextLight,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Search delegates, manage requests",
                            style = MaterialTheme.typography.bodySmall,
                            color = LuxuryTextMuted
                        )
                    }
                }
            }
        }
    }
}
