package com.example.ui.screens

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventSession
import com.example.ui.theme.*
import com.example.ui.viewmodel.EventViewModel
import com.example.ui.components.AfricaNetworkGlobe

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

    var showAiDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    // Smooth page load fade-in animation
    val alphaAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = EaseInOutQuad)
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LuxuryDarkBg)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {
        // 1. Welcome Executive Top Header / Hero Banner
        val calendar = remember { java.util.Calendar.getInstance() }
        val hour = remember { calendar.get(java.util.Calendar.HOUR_OF_DAY) }
        val greetingPrefix = when {
            hour in 5..11 -> "Good Morning"
            hour in 12..16 -> "Good Afternoon"
            hour in 17..21 -> "Good Evening"
            else -> "Welcome Back"
        }

        val firstName = remember(profile.name) {
            val tokens = profile.name.trim().split(Regex("\\s+"))
            val filteredTokens = tokens.filter { token ->
                val lower = token.lowercase()
                lower != "executive" && lower != "delegate" && lower != "vip" && lower != "speaker" && lower != "sponsor" && lower != "volunteer" && lower != "admin"
            }
            filteredTokens.firstOrNull() ?: "Attendee"
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { alpha = alphaAnim.value }
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DASA CONNECT",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.5.sp
                            ),
                            color = DasaGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$greetingPrefix, $firstName 👋",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = Color.White,
                            modifier = Modifier.testTag("dashboard_greeting")
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Welcome to Digital Assets Summit Africa",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = LuxuryTextMuted
                        )
                    }

                    // Badge classification pill & Edit Profile Button
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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

                        TextButton(
                            onClick = { showEditProfileDialog = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = DasaGold),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier
                                .background(LuxuryCardBg, RoundedCornerShape(8.dp))
                                .border(1.dp, DasaGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .testTag("top_right_edit_profile_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    modifier = Modifier.size(14.dp),
                                    tint = DasaGold
                                )
                                Text(
                                    text = "Edit Profile",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = DasaGold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Dynamic Context-Aware Alert Bar
                val bookmarkedCount = remember(sessions) { sessions.count { it.isBookmarked } }
                val alertText = when {
                    bookmarkedCount > 0 -> "You have $bookmarkedCount session${if (bookmarkedCount > 1) "s" else ""} bookmarked today."
                    else -> "Welcome to Day 2 of DASA 2026."
                }
                
                val alertTransition = rememberInfiniteTransition(label = "AlertPulse")
                val alertAlpha by alertTransition.animateFloat(
                    initialValue = 0.4f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "AlertAlpha"
                )
                
                val alertBorderColor = if (bookmarkedCount > 0) DasaGold.copy(alpha = 0.5f) else DasaEmerald.copy(alpha = 0.5f)
                val alertDotColor = if (bookmarkedCount > 0) DasaGold else DasaEmerald
                val alertTextColor = if (bookmarkedCount > 0) DasaGold else DasaEmerald

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DasaDeepNavy.copy(alpha = 0.8f)),
                    border = BorderStroke(1.dp, alertBorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(alertDotColor.copy(alpha = alertAlpha), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = alertText,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = alertTextColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Premium live countdown widget
                SummitCountdownWidget()

                Spacer(modifier = Modifier.height(20.dp))

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

        Spacer(modifier = Modifier.height(20.dp))

        // Signature Africa Network Globe
        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
            AfricaNetworkGlobe()
        }

        Spacer(modifier = Modifier.height(20.dp))

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

        Spacer(modifier = Modifier.height(24.dp))

        // Today's Highlights
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Today's DASA Highlights",
                style = MaterialTheme.typography.titleMedium,
                color = LuxuryTextLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            val highlights = listOf(
                "Opening Keynote",
                "Digital Assets Regulation Panel",
                "Innovation Showcase",
                "Investor Networking Session"
            )

            highlights.forEach { highlight ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxuryCardBg),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed highlight",
                            tint = DasaGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = highlight,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = LuxuryTextLight
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Live Event Pulse
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Event Pulse",
                    style = MaterialTheme.typography.titleMedium,
                    color = LuxuryTextLight,
                    fontWeight = FontWeight.Bold
                )
                
                // Pulse dot
                val infinitePulse = rememberInfiniteTransition(label = "LivePulseDot")
                val pulseAlpha by infinitePulse.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "PulseAlpha"
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFF00A676).copy(alpha = pulseAlpha), CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            val pulseStats = listOf(
                Triple("420", "Delegates Active", Icons.Default.Group),
                Triple("12", "Networking Meetings Today", Icons.Default.Hub),
                Triple("4", "Sessions Running", Icons.Default.PlayCircle),
                Triple("850", "Check-ins Completed", Icons.Default.AssignmentTurnedIn)
            )

            // 2x2 grid using columns and rows
            for (i in pulseStats.indices step 2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    for (j in 0..1) {
                        val stat = pulseStats[i + j]
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = LuxuryCardBg),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = stat.third,
                                    contentDescription = stat.second,
                                    tint = DasaGold,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = stat.first,
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = LuxuryTextLight
                                )
                                Text(
                                    text = stat.second,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LuxuryTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Smart Networking Experience - recommended connections
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Recommended Connection",
                style = MaterialTheme.typography.titleMedium,
                color = LuxuryTextLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            var connectedState by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DasaDeepNavy),
                border = BorderStroke(1.dp, DasaGold.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(LuxuryCardBg, CircleShape)
                                    .border(1.dp, DasaGold, CircleShape),
                                contentAlignment = Alignment.Center
                              ) {
                                Text(
                                    text = "KM",
                                    fontWeight = FontWeight.Bold,
                                    color = DasaGold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Kwame Mensah",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = LuxuryTextLight
                                )
                                Text(
                                    text = "Senior Policy Advisor • Central Bank",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LuxuryTextMuted
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(DasaGold.copy(alpha = 0.15f), RoundedCornerShape(100.dp))
                                .border(1.dp, DasaGold, RoundedCornerShape(100.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "87% MATCH",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = DasaGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "SHARED INTERESTS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                        color = DasaGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Blockchain Regulation", "CBDCs", "Digital Finance").forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .background(LuxuryCardBg, RoundedCornerShape(6.dp))
                                    .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LuxuryTextLight
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { connectedState = !connectedState },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (connectedState) DasaEmerald else DasaGold,
                            contentColor = if (connectedState) Color.White else DasaDeepNavy
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (connectedState) Icons.Default.Check else Icons.Default.PersonAdd,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (connectedState) "Request Sent" else "Connect with Kwame",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        val sponsorsList by viewModel.allSponsors.collectAsState()
        Spacer(modifier = Modifier.height(24.dp))

        // Sponsor Showcase Carousel
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Elite Sponsor Showcase",
                style = MaterialTheme.typography.titleMedium,
                color = LuxuryTextLight,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (sponsorsList.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(sponsorsList) { sponsor ->
                        Card(
                            modifier = Modifier
                                .width(280.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = LuxuryCardBg),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = sponsor.tier.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = DasaGold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = DasaGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(DasaDeepNavy, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Business,
                                            contentDescription = sponsor.name,
                                            tint = DasaGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = sponsor.name,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = LuxuryTextLight
                                        )
                                        Text(
                                            text = "Fintech Pioneer",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = LuxuryTextMuted
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = sponsor.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LuxuryTextMuted,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Interactive Event Map
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Interactive Event Map",
                style = MaterialTheme.typography.titleMedium,
                color = LuxuryTextLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Locate sessions, lounges, and exhibition halls",
                style = MaterialTheme.typography.bodySmall,
                color = LuxuryTextMuted
            )
            Spacer(modifier = Modifier.height(12.dp))

            var selectedSpace by remember { mutableStateOf("Main Stage") }
            val spaces = listOf(
                "Main Stage" to "Keynotes & regulation panels",
                "Exhibition Hall" to "Sponsor showcase booths",
                "Networking Lounge" to "One-on-one executive deal making",
                "VIP Area" to "Central banks & regulators lounge",
                "Registration Area" to "Delegate badging & welcome desk",
                "Speaker Lounge" to "Speaker prep & media center"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuxuryCardBg),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(DasaDeepNavy, RoundedCornerShape(12.dp))
                            .border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = this.size.width
                            val h = this.size.height
                            
                            drawLine(color = BorderColor.copy(alpha = 0.5f), start = Offset(w * 0.1f, h * 0.1f), end = Offset(w * 0.9f, h * 0.1f), strokeWidth = 2f)
                            drawLine(color = BorderColor.copy(alpha = 0.5f), start = Offset(w * 0.9f, h * 0.1f), end = Offset(w * 0.9f, h * 0.9f), strokeWidth = 2f)
                            drawLine(color = BorderColor.copy(alpha = 0.5f), start = Offset(w * 0.9f, h * 0.9f), end = Offset(w * 0.1f, h * 0.9f), strokeWidth = 2f)
                            drawLine(color = BorderColor.copy(alpha = 0.5f), start = Offset(w * 0.1f, h * 0.9f), end = Offset(w * 0.1f, h * 0.1f), strokeWidth = 2f)
                            
                            drawLine(color = BorderColor.copy(alpha = 0.3f), start = Offset(w * 0.5f, h * 0.1f), end = Offset(w * 0.5f, h * 0.9f), strokeWidth = 1f)
                            drawLine(color = BorderColor.copy(alpha = 0.3f), start = Offset(w * 0.1f, h * 0.5f), end = Offset(w * 0.9f, h * 0.5f), strokeWidth = 1f)
                        }

                        val spacesCoordinates = mapOf(
                            "Main Stage" to Offset(0.2f, 0.2f),
                            "Exhibition Hall" to Offset(0.6f, 0.2f),
                            "Networking Lounge" to Offset(0.2f, 0.6f),
                            "VIP Area" to Offset(0.6f, 0.6f),
                            "Registration Area" to Offset(0.4f, 0.4f),
                            "Speaker Lounge" to Offset(0.4f, 0.1f)
                        )

                        spacesCoordinates.forEach { (name, offset) ->
                            val isSelected = name == selectedSpace
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(
                                        x = (offset.x * 280).dp,
                                        y = (offset.y * 140).dp
                                    )
                                    .size(if (isSelected) 14.dp else 8.dp)
                                    .background(
                                        if (isSelected) DasaGold else DasaEmerald.copy(alpha = 0.6f),
                                        CircleShape
                                    )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                                .background(LuxuryCardBg.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                                .border(1.dp, DasaGold, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Selected: $selectedSpace",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = DasaGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(spaces) { space ->
                                val isSelected = space.first == selectedSpace
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isSelected) DasaGold.copy(alpha = 0.15f) else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) DasaGold else BorderColor,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedSpace = space.first }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = space.first,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) DasaGold else LuxuryTextLight
                                        )
                                        Text(
                                            text = space.second,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = LuxuryTextMuted,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Floating DASA AI concierge button
    ExtendedFloatingActionButton(
        onClick = { showAiDialog = true },
        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "DASA AI", tint = DasaDeepNavy) },
        text = { Text("Ask DASA AI", fontWeight = FontWeight.Bold, color = DasaDeepNavy) },
        containerColor = DasaGold,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)
            .testTag("floating_ai_button")
    )

    // DASA AI Concierge Dialog
    if (showAiDialog) {
        AlertDialog(
            onDismissRequest = { showAiDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI",
                            tint = DasaGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DASA AI CONCIERGE",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = DasaGold
                        )
                    }
                    IconButton(onClick = { showAiDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = LuxuryTextMuted)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Your elite, intelligent DASA voice & neural assistant is ready for integration.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LuxuryTextLight
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "CORE CAPABILITIES",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                        color = DasaGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val capabilities = listOf(
                        "Recommend Tailored Sessions" to "Analyzes interests to select relevant digital assets keynotes.",
                        "Find Speakers & Biographies" to "Locate regulators, central banks, and VC profiles instantly.",
                        "Summarize Panel Keynotes" to "Retrieve concise executive briefings of critical policies.",
                        "Suggest VIP Networking" to "Pairs you with decision makers, founders, and investors."
                    )

                    capabilities.forEach { cap ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(6.dp)
                                    .background(DasaEmerald, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = cap.first,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = LuxuryTextLight
                                )
                                Text(
                                    text = cap.second,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LuxuryTextMuted
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAiDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DasaGold, contentColor = DasaDeepNavy)
                ) {
                    Text("Acknowledge", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = LuxuryCardBg
        )
    }

    if (showEditProfileDialog) {
        val context = LocalContext.current
        var nameInput by remember { mutableStateOf(profile.name) }
        var companyInput by remember { mutableStateOf(profile.company) }
        var titleInput by remember { mutableStateOf(profile.title) }
        var roleInput by remember { mutableStateOf(profile.role) }
        var isDropdownExpanded by remember { mutableStateOf(false) }
        val rolesList = listOf("Delegate", "Speaker", "Sponsor", "Volunteer", "Admin")

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = "EDIT EXECUTIVE PROFILE",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = DasaGold,
                    letterSpacing = 1.2.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Update your credentials for Digital Assets Summit Africa 2026. This dynamically syncs with your encrypted secure Pass and Home greeting.",
                        style = MaterialTheme.typography.bodySmall,
                        color = LuxuryTextMuted
                    )

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Full Name", color = DasaGold) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DasaGold,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("dialog_edit_name")
                    )

                    OutlinedTextField(
                        value = companyInput,
                        onValueChange = { companyInput = it },
                        label = { Text("Company / Organization", color = DasaGold) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DasaGold,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("dialog_edit_company")
                    )

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Executive Title", color = DasaGold) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DasaGold,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("dialog_edit_title")
                    )

                    // Role dropdown selector
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = roleInput,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Summit Role", color = DasaGold) },
                            trailingIcon = {
                                IconButton(onClick = { isDropdownExpanded = !isDropdownExpanded }) {
                                    Icon(
                                        imageVector = if (isDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = "Expand roles",
                                        tint = DasaGold
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DasaGold,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth().clickable { isDropdownExpanded = !isDropdownExpanded }
                        )

                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LuxuryCardBg)
                                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                        ) {
                            rolesList.forEach { roleOption ->
                                    DropdownMenuItem(
                                        text = { 
                                            Text(
                                                text = roleOption, 
                                                color = if (roleInput == roleOption) DasaGold else LuxuryTextLight,
                                                fontWeight = if (roleInput == roleOption) FontWeight.Bold else FontWeight.Normal
                                            ) 
                                        },
                                        onClick = {
                                            roleOption?.let { roleInput = it }
                                            isDropdownExpanded = false
                                        }
                                    )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            viewModel.registerUser(
                                name = nameInput,
                                email = profile.email,
                                company = companyInput,
                                title = titleInput,
                                role = roleInput
                            )
                            Toast.makeText(context, "✓ Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            showEditProfileDialog = false
                        } else {
                            Toast.makeText(context, "✗ Full Name cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DasaGold, contentColor = DasaDeepNavy),
                    modifier = Modifier.testTag("dialog_save_profile_button")
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditProfileDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = LuxuryTextMuted)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = LuxuryCardBg,
            textContentColor = LuxuryTextLight,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(1.dp, BorderColor, RoundedCornerShape(16.dp))
        )
    }
}
}

@Composable
fun SummitCountdownWidget(
    modifier: Modifier = Modifier
) {
    val targetTimeMs = remember {
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, 2026)
            set(java.util.Calendar.MONTH, java.util.Calendar.OCTOBER) // October is 9 (0-indexed)
            set(java.util.Calendar.DAY_OF_MONTH, 12)
            set(java.util.Calendar.HOUR_OF_DAY, 9)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        calendar.timeInMillis
    }

    var timeLeftMs by remember { mutableStateOf(targetTimeMs - System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (timeLeftMs > 0) {
            timeLeftMs = targetTimeMs - System.currentTimeMillis()
            kotlinx.coroutines.delay(1000)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("summit_countdown_widget"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LuxuryCardBg.copy(alpha = 0.75f)),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    DasaGold,
                    DasaGold.copy(alpha = 0.2f),
                    DasaGold
                )
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .graphicsLayer {
                                    scaleX = pulseScale
                                    scaleY = pulseScale
                                }
                                .background(DasaGold.copy(alpha = pulseAlpha), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(DasaGold, CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LIVE COUNTDOWN",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.8.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = DasaGold
                    )
                }

                Text(
                    text = "SUMMIT INAUGURATION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = LuxuryTextMuted
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (timeLeftMs > 0) {
                val seconds = (timeLeftMs / 1000) % 60
                val minutes = (timeLeftMs / (1000 * 60)) % 60
                val hours = (timeLeftMs / (1000 * 60 * 60)) % 24
                val days = timeLeftMs / (1000 * 60 * 60 * 24)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CountdownTimeUnit(value = days, label = "DAYS")
                    CountdownDivider()
                    CountdownTimeUnit(value = hours, label = "HOURS")
                    CountdownDivider()
                    CountdownTimeUnit(value = minutes, label = "MINUTES")
                    CountdownDivider()
                    CountdownTimeUnit(value = seconds, label = "SECONDS")
                }
            } else {
                Text(
                    text = "THE SUMMIT IS NOW LIVE",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    ),
                    color = DasaGold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun CountdownTimeUnit(value: Long, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val formattedValue = String.format("%02d", value.coerceAtLeast(0))
        Text(
            text = formattedValue,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            ),
            color = DasaGold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            ),
            color = LuxuryTextMuted
        )
    }
}

@Composable
fun CountdownDivider() {
    Text(
        text = ":",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold
        ),
        color = DasaGold.copy(alpha = 0.5f),
        modifier = Modifier.offset(y = (-8).dp)
    )
}

