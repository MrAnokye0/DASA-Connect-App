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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.QrCodeCanvas
import com.example.ui.theme.*
import com.example.ui.viewmodel.EventViewModel
import java.io.File
import android.os.Environment
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

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
                            .border(
                                width = 1.5.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(DasaGold, Color(0xFFFFF1C2), DasaGold)
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .testTag("digital_badge_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF070E16)) // Darker luxury shade
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
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 2.sp
                                ),
                                color = DasaGold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "DASA 2026 • OFFICIAL PLATINUM PASS",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                ),
                                color = LuxuryTextLight,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Deterministic QR Code frame with premium gold corners
                            Box(
                                modifier = Modifier
                                    .size(210.dp)
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .border(2.dp, DasaGold, RoundedCornerShape(16.dp))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                QrCodeCanvas(
                                    contentString = profile.qrCodeContent,
                                    qrColor = Color(0xFF070E16)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Dashed tear-off separator line
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                repeat(20) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(1.dp)
                                            .background(DasaGold.copy(alpha = 0.35f))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Boarding Pass Sector Metadata
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(text = "METRIC", style = MaterialTheme.typography.labelSmall, color = LuxuryTextMuted)
                                    Text(text = "VIP • ALL-ACCESS", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = DasaGold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "PASS NO", style = MaterialTheme.typography.labelSmall, color = LuxuryTextMuted)
                                    Text(text = "DASA-9943-Z", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = DasaGold)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Delegate Name & Bio details
                            Text(
                                text = profile.name.uppercase(),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = LuxuryTextLight,
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

                            // Gold Hologram chip decoration + role
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Hologram Chip decoration
                                Box(
                                    modifier = Modifier
                                        .height(28.dp)
                                        .width(55.dp)
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(DasaGold, Color(0xFFFFF1C2), DasaGold)
                                            ),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .border(0.5.dp, BorderColor, RoundedCornerShape(6.dp))
                                )

                                // Role Banner Strip (Pill)
                                Box(
                                    modifier = Modifier
                                        .background(
                                            when (profile.role) {
                                                "Speaker" -> DasaGold
                                                "Sponsor" -> DasaGold
                                                "Volunteer" -> DasaEmerald
                                                else -> DasaGold
                                            },
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 20.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = profile.role.uppercase(),
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        color = DasaDeepNavy,
                                        letterSpacing = 1.5.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // SECTION HEADER
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = DasaGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VIRTUAL NETWORKING CARD",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = DasaGold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Theme and color configuration based on user's choice
                    val theme = profile.cardTheme
                    val cardBgBrush = when (theme) {
                        "Cyberpunk" -> Brush.linearGradient(
                            colors = listOf(Color(0xFF0F051D), Color(0xFF1E0B36), Color(0xFF050F1D))
                        )
                        "Minimalist Slate" -> Brush.linearGradient(
                            colors = listOf(Color(0xFF1A1D20), Color(0xFF2B3035), Color(0xFF111315))
                        )
                        "Midnight Emerald" -> Brush.linearGradient(
                            colors = listOf(Color(0xFF021B10), Color(0xFF063A22), Color(0xFF010F0A))
                        )
                        else -> Brush.linearGradient( // Gold Premium
                            colors = listOf(Color(0xFF070E16), Color(0xFF0B1724), Color(0xFF03070C))
                        )
                    }

                    val accentColor = when (theme) {
                        "Cyberpunk" -> Color(0xFF06F3EC) // Electric cyan
                        "Minimalist Slate" -> Color.White
                        "Midnight Emerald" -> Color(0xFF2BE092) // Emerald mint
                        else -> DasaGold // Gold
                    }

                    val cardBorderColor = when (theme) {
                        "Cyberpunk" -> Color(0xFFF43F5E) // Neon Rose/Pink
                        "Minimalist Slate" -> Color(0xFF495057) // Slate Gray
                        "Midnight Emerald" -> Color(0xFF059669) // Emerald Green
                        else -> DasaGold // Gold
                    }

                    val textMutedColor = when (theme) {
                        "Cyberpunk" -> Color(0xFFA5B4FC)
                        "Minimalist Slate" -> Color(0xFFADB5BD)
                        "Midnight Emerald" -> Color(0xFF6EE7B7)
                        else -> LuxuryTextMuted
                    }

                    val textLightColor = when (theme) {
                        "Minimalist Slate" -> Color.White
                        else -> LuxuryTextLight
                    }

                    var isDownloading by remember { mutableStateOf(false) }
                    var downloadProgress by remember { mutableStateOf(0f) }
                    val coroutineScope = rememberCoroutineScope()

                    // Card Container
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(cardBorderColor, cardBorderColor.copy(alpha = 0.5f), cardBorderColor)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("virtual_business_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(cardBgBrush)
                                .padding(20.dp)
                        ) {
                            Column {
                                // Card Top: Header & Theme Style name
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "SUMMIT CONNECT CARD",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        ),
                                        color = accentColor
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = theme.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            ),
                                            color = accentColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Attendee Primary Identity Info
                                Text(
                                    text = profile.name,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = textLightColor
                                )
                                Text(
                                    text = "${profile.title} @ ${profile.company}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = accentColor
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Role Indicator Badge
                                Box(
                                    modifier = Modifier
                                        .background(accentColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .border(0.5.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = profile.role.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = textLightColor
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Divider Line
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 1.dp,
                                    color = cardBorderColor.copy(alpha = 0.25f)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Professional Bio
                                Text(
                                    text = "BIO",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = accentColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (profile.bio.isNotBlank()) profile.bio else "No professional biography has been provided yet. Update your profile in Settings to share your bio!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textLightColor,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Networking preferences segment
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(8.dp))
                                        .border(0.5.dp, cardBorderColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Handshake,
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "NETWORKING STATUS",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                            color = textMutedColor
                                        )
                                        Text(
                                            text = profile.networkingPrefs,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = textLightColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Contact Info Items
                                Text(
                                    text = "CONTACT & SOCIAL HANDLES",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = accentColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Phone Info
                                if (profile.phone.isNotBlank()) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = profile.phone, style = MaterialTheme.typography.bodySmall, color = textLightColor)
                                    }
                                }

                                // Email Info
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = profile.email, style = MaterialTheme.typography.bodySmall, color = textLightColor)
                                }

                                // LinkedIn Social Info
                                if (profile.linkedin.isNotBlank()) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = profile.linkedin, style = MaterialTheme.typography.bodySmall, color = textLightColor)
                                    }
                                }

                                // Twitter Social Info
                                if (profile.twitter.isNotBlank()) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.AlternateEmail, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = profile.twitter, style = MaterialTheme.typography.bodySmall, color = textLightColor)
                                    }
                                }

                                // GitHub Social Info
                                if (profile.github.isNotBlank()) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = profile.github, style = MaterialTheme.typography.bodySmall, color = textLightColor)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Animated Download Button & Progress Bar
                    if (isDownloading) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LinearProgressIndicator(
                                progress = { downloadProgress },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = DasaGold,
                                trackColor = BorderColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Generating card assets & compiling vCard: ${(downloadProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = DasaGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                isDownloading = true
                                coroutineScope.launch {
                                    // Progress bar animation loop
                                    for (i in 1..20) {
                                        kotlinx.coroutines.delay(75)
                                        downloadProgress = i / 20f
                                    }
                                    
                                    // Write the actual vCard file
                                    try {
                                        val vcfContent = """
                                            BEGIN:VCARD
                                            VERSION:3.0
                                            FN:${profile.name}
                                            ORG:${profile.company}
                                            TITLE:${profile.title}
                                            TEL;TYPE=CELL:${profile.phone}
                                            EMAIL;TYPE=PREF,INTERNET:${profile.email}
                                            NOTE:Bio: ${profile.bio}\nNetworking: ${profile.networkingPrefs}
                                            URL;TYPE=LinkedIn:${profile.linkedin}
                                            URL;TYPE=Twitter:${profile.twitter}
                                            URL;TYPE=GitHub:${profile.github}
                                            END:VCARD
                                        """.trimIndent()

                                        val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                                        val fileName = "${profile.name.replace(" ", "_")}_business_card.vcf"
                                        val file = File(downloadsDir, fileName)
                                        file.writeText(vcfContent)

                                        Toast.makeText(
                                            context, 
                                            "✓ Virtual Card downloaded successfully to device local files: $fileName", 
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "✗ Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isDownloading = false
                                        downloadProgress = 0f
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DasaGold, contentColor = DasaDeepNavy),
                            modifier = Modifier.fillMaxWidth().testTag("download_business_card_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Download Virtual Business Card", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Card generated in DASA gateway and verified.",
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
                // SETTINGS VIEW (WITH INTEGRATED PROFILE EDITOR)
                var nameInput by remember { mutableStateOf(profile.name) }
                var companyInput by remember { mutableStateOf(profile.company) }
                var titleInput by remember { mutableStateOf(profile.title) }
                var roleInput by remember { mutableStateOf(profile.role) }
                var bioInput by remember { mutableStateOf(profile.bio) }
                var networkingPrefsInput by remember { mutableStateOf(profile.networkingPrefs) }
                var phoneInput by remember { mutableStateOf(profile.phone) }
                var linkedinInput by remember { mutableStateOf(profile.linkedin) }
                var twitterInput by remember { mutableStateOf(profile.twitter) }
                var githubInput by remember { mutableStateOf(profile.github) }
                var cardThemeInput by remember { mutableStateOf(profile.cardTheme) }
                
                var isDropdownExpanded by remember { mutableStateOf(false) }
                var isNetworkDropdownExpanded by remember { mutableStateOf(false) }
                var isThemeDropdownExpanded by remember { mutableStateOf(false) }
                
                val rolesList = listOf("Delegate", "Speaker", "Sponsor", "Volunteer", "Admin")
                val networkingList = listOf("Open to Network", "Looking for Partnerships", "Speaking / Panelists", "Just Browsing")
                val themeList = listOf("Gold Premium", "Cyberpunk", "Minimalist Slate", "Midnight Emerald")

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    Text(
                        text = "EDIT DELEGATE PROFILE",
                        style = MaterialTheme.typography.labelSmall,
                        color = DasaGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = LuxuryCardBg),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Name Input
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Full Name", color = DasaGold) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DasaGold,
                                    unfocusedBorderColor = BorderColor,
                                    focusedLabelColor = DasaGold,
                                    unfocusedLabelColor = LuxuryTextMuted,
                                    focusedTextColor = LuxuryTextLight,
                                    unfocusedTextColor = LuxuryTextLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("edit_profile_name")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Company Input
                            OutlinedTextField(
                                value = companyInput,
                                onValueChange = { companyInput = it },
                                label = { Text("Company / Organization", color = DasaGold) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DasaGold,
                                    unfocusedBorderColor = BorderColor,
                                    focusedLabelColor = DasaGold,
                                    unfocusedLabelColor = LuxuryTextMuted,
                                    focusedTextColor = LuxuryTextLight,
                                    unfocusedTextColor = LuxuryTextLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("edit_profile_company")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Title Input
                            OutlinedTextField(
                                value = titleInput,
                                onValueChange = { titleInput = it },
                                label = { Text("Executive Title", color = DasaGold) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DasaGold,
                                    unfocusedBorderColor = BorderColor,
                                    focusedLabelColor = DasaGold,
                                    unfocusedLabelColor = LuxuryTextMuted,
                                    focusedTextColor = LuxuryTextLight,
                                    unfocusedTextColor = LuxuryTextLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("edit_profile_title")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Role Selection Dropdown (Material 3)
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
                                        focusedTextColor = LuxuryTextLight,
                                        unfocusedTextColor = LuxuryTextLight
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
                                                roleInput = roleOption
                                                isDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Bio Input
                            OutlinedTextField(
                                value = bioInput,
                                onValueChange = { bioInput = it },
                                label = { Text("Professional Bio", color = DasaGold) },
                                singleLine = false,
                                maxLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DasaGold,
                                    unfocusedBorderColor = BorderColor,
                                    focusedLabelColor = DasaGold,
                                    unfocusedLabelColor = LuxuryTextMuted,
                                    focusedTextColor = LuxuryTextLight,
                                    unfocusedTextColor = LuxuryTextLight
                                ),
                                modifier = Modifier.fillMaxWidth().height(90.dp).testTag("edit_profile_bio")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Networking Preferences Dropdown (Material 3)
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = networkingPrefsInput,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Networking Preferences", color = DasaGold) },
                                    trailingIcon = {
                                        IconButton(onClick = { isNetworkDropdownExpanded = !isNetworkDropdownExpanded }) {
                                            Icon(
                                                imageVector = if (isNetworkDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                                contentDescription = "Expand preferences",
                                                tint = DasaGold
                                            )
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = DasaGold,
                                        unfocusedBorderColor = BorderColor,
                                        focusedTextColor = LuxuryTextLight,
                                        unfocusedTextColor = LuxuryTextLight
                                    ),
                                    modifier = Modifier.fillMaxWidth().clickable { isNetworkDropdownExpanded = !isNetworkDropdownExpanded }
                                )

                                DropdownMenu(
                                    expanded = isNetworkDropdownExpanded,
                                    onDismissRequest = { isNetworkDropdownExpanded = false },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(LuxuryCardBg)
                                        .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                                ) {
                                    networkingList.forEach { netOption ->
                                        DropdownMenuItem(
                                            text = { 
                                                Text(
                                                    text = netOption, 
                                                    color = if (networkingPrefsInput == netOption) DasaGold else LuxuryTextLight,
                                                    fontWeight = if (networkingPrefsInput == netOption) FontWeight.Bold else FontWeight.Normal
                                                ) 
                                            },
                                            onClick = {
                                                networkingPrefsInput = netOption
                                                isNetworkDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Phone Input
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
                                label = { Text("Phone Number", color = DasaGold) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DasaGold,
                                    unfocusedBorderColor = BorderColor,
                                    focusedLabelColor = DasaGold,
                                    unfocusedLabelColor = LuxuryTextMuted,
                                    focusedTextColor = LuxuryTextLight,
                                    unfocusedTextColor = LuxuryTextLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("edit_profile_phone")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // LinkedIn Input
                            OutlinedTextField(
                                value = linkedinInput,
                                onValueChange = { linkedinInput = it },
                                label = { Text("LinkedIn Profile Link", color = DasaGold) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DasaGold,
                                    unfocusedBorderColor = BorderColor,
                                    focusedLabelColor = DasaGold,
                                    unfocusedLabelColor = LuxuryTextMuted,
                                    focusedTextColor = LuxuryTextLight,
                                    unfocusedTextColor = LuxuryTextLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("edit_profile_linkedin")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Twitter Input
                            OutlinedTextField(
                                value = twitterInput,
                                onValueChange = { twitterInput = it },
                                label = { Text("Twitter / X Link", color = DasaGold) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DasaGold,
                                    unfocusedBorderColor = BorderColor,
                                    focusedLabelColor = DasaGold,
                                    unfocusedLabelColor = LuxuryTextMuted,
                                    focusedTextColor = LuxuryTextLight,
                                    unfocusedTextColor = LuxuryTextLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("edit_profile_twitter")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // GitHub Input
                            OutlinedTextField(
                                value = githubInput,
                                onValueChange = { githubInput = it },
                                label = { Text("GitHub Link", color = DasaGold) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DasaGold,
                                    unfocusedBorderColor = BorderColor,
                                    focusedLabelColor = DasaGold,
                                    unfocusedLabelColor = LuxuryTextMuted,
                                    focusedTextColor = LuxuryTextLight,
                                    unfocusedTextColor = LuxuryTextLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("edit_profile_github")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Business Card Theme Selection
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = cardThemeInput,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Business Card Theme", color = DasaGold) },
                                    trailingIcon = {
                                        IconButton(onClick = { isThemeDropdownExpanded = !isThemeDropdownExpanded }) {
                                            Icon(
                                                imageVector = if (isThemeDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                                contentDescription = "Expand themes",
                                                tint = DasaGold
                                            )
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = DasaGold,
                                        unfocusedBorderColor = BorderColor,
                                        focusedTextColor = LuxuryTextLight,
                                        unfocusedTextColor = LuxuryTextLight
                                    ),
                                    modifier = Modifier.fillMaxWidth().clickable { isThemeDropdownExpanded = !isThemeDropdownExpanded }
                                )

                                DropdownMenu(
                                    expanded = isThemeDropdownExpanded,
                                    onDismissRequest = { isThemeDropdownExpanded = false },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(LuxuryCardBg)
                                        .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                                ) {
                                    themeList.forEach { themeOption ->
                                        DropdownMenuItem(
                                            text = { 
                                                Text(
                                                    text = themeOption, 
                                                    color = if (cardThemeInput == themeOption) DasaGold else LuxuryTextLight,
                                                    fontWeight = if (cardThemeInput == themeOption) FontWeight.Bold else FontWeight.Normal
                                                ) 
                                            },
                                            onClick = {
                                                cardThemeInput = themeOption
                                                isThemeDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Save Button
                            Button(
                                onClick = {
                                    if (nameInput.isNotBlank()) {
                                        viewModel.registerUser(
                                            name = nameInput,
                                            email = profile.email,
                                            company = companyInput,
                                            title = titleInput,
                                            role = roleInput,
                                            bio = bioInput,
                                            networkingPrefs = networkingPrefsInput,
                                            phone = phoneInput,
                                            linkedin = linkedinInput,
                                            twitter = twitterInput,
                                            github = githubInput,
                                            cardTheme = cardThemeInput
                                        )
                                        Toast.makeText(context, "✓ Profile saved and synchronized with central database!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "✗ Full Name cannot be empty", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DasaGold, contentColor = DasaDeepNavy),
                                modifier = Modifier.fillMaxWidth().testTag("save_profile_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Save and Sync Profile", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "EXECUTIVE ACCOUNT & SECURITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = DasaGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
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

                    Spacer(modifier = Modifier.height(24.dp))

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
