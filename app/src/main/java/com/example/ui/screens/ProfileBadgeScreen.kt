package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.QrCodeCanvas
import com.example.ui.theme.*
import com.example.ui.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBadgeScreen(
    viewModel: EventViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current

    var badgeViewTab by remember { mutableStateOf(0) } // 0=My Badge, 1=Scan Badge, 2=Settings
    val tabs = listOf("My Pass", "Scan Reader", "Settings")

    val profile = userProfile ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
    ) {
        // Top Navigation Toggle
        TabRow(
            selectedTabIndex = badgeViewTab,
            containerColor = DasaDeepNavy,
            contentColor = DasaGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[badgeViewTab]),
                    color = DasaGold
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = badgeViewTab == index,
                    onClick = { badgeViewTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (badgeViewTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    },
                    selectedContentColor = DasaGold,
                    unselectedContentColor = LuxuryTextMuted
                )
            }
        }

        when (badgeViewTab) {
            0 -> {
                // MY DIGITAL DELEGATE BADGE CARD
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // The Premium Physical-Looking Badge container
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, DasaGold, RoundedCornerShape(24.dp))
                            .testTag("digital_badge_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = DasaDeepNavy)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Badge Header
                            Text(
                                text = "DIGITAL ASSETS SUMMIT AFRICA",
                                style = MaterialTheme.typography.labelSmall,
                                color = DasaGold,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "DASA 2026 • OFFICIAL PASS",
                                style = MaterialTheme.typography.labelMedium,
                                color = LuxuryTextMuted,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Deterministic QR Code frame
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                QrCodeCanvas(
                                    contentString = profile.qrCodeContent,
                                    qrColor = DasaDeepNavy
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Delegate Name & Bio details
                            Text(
                                text = profile.name.uppercase(),
                                style = MaterialTheme.typography.headlineSmall,
                                color = LuxuryTextLight,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = profile.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = DasaGold,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = profile.company,
                                style = MaterialTheme.typography.bodySmall,
                                color = LuxuryTextMuted,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Hologram Chip decoration
                            Box(
                                modifier = Modifier
                                    .height(30.dp)
                                    .width(60.dp)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(DasaGold, Color(0xFFE9D5A1), DasaGold)
                                        ),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .border(0.5.dp, BorderColor, RoundedCornerShape(6.dp))
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Role Banner Strip
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        when (profile.role) {
                                            "Speaker" -> DasaGold
                                            "Sponsor" -> DasaGold
                                            "Volunteer" -> DasaEmerald
                                            else -> DasaGold
                                        },
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = profile.role.uppercase(),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = DasaDeepNavy,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Encrypted JWT digital signature verified by DASA gateway.",
                        style = MaterialTheme.typography.labelSmall,
                        color = LuxuryTextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            1 -> {
                // SCAN QR DIGITAL READER SIMULATOR
                val infiniteTransition = rememberInfiniteTransition()
                val scanLineY by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 240f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "SECURE BADGE SCANNER",
                        style = MaterialTheme.typography.titleMedium,
                        color = DasaGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Position another delegate's QR code within the boundaries below to securely exchange networking signatures instantly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = LuxuryTextMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Scanner HUD Frame
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .border(3.dp, DasaGold, RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .background(LuxuryCardBg),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        // Floating laser scanner line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .offset(y = scanLineY.dp)
                                .background(DasaEmerald)
                        )

                        // Center guides
                        Icon(
                            imageVector = Icons.Default.FilterCenterFocus,
                            contentDescription = null,
                            tint = DasaGold.copy(alpha = 0.3f),
                            modifier = Modifier
                                .size(120.dp)
                                .align(Alignment.Center)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "SIMULATOR CONTROLS",
                        style = MaterialTheme.typography.labelSmall,
                        color = DasaGold,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick scan selectors for simulation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.acceptConnection("att1") // Kofi Boateng
                                Toast.makeText(context, "✓ Scanned Kofi Boateng: Connection Connected!", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DasaGold, contentColor = DasaDeepNavy),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Scan Kofi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.acceptConnection("att4") // Zola Mtetwa
                                Toast.makeText(context, "✓ Scanned Zola Mtetwa: Connection Connected!", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DasaGold, contentColor = DasaDeepNavy),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Scan Zola", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            2 -> {
                // SETTINGS VIEW
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    Text(
                        text = "EXECUTIVE ACCOUNT & SECURITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = DasaGold,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = LuxuryCardBg),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Registered Email:", color = LuxuryTextMuted, style = MaterialTheme.typography.bodyMedium)
                                Text(profile.email, color = LuxuryTextLight, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Asymmetric Key Hash:", color = LuxuryTextMuted, style = MaterialTheme.typography.bodyMedium)
                                Text("ECDSA-SHA256", color = DasaEmerald, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Cyber-Ops Status:", color = LuxuryTextMuted, style = MaterialTheme.typography.bodyMedium)
                                Text("SECURED", color = DasaEmerald, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { viewModel.logout() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626), contentColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("logout_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Logout, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Secure Terminal Log-out", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
