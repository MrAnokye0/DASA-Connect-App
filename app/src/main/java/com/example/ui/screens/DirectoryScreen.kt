package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.data.model.Speaker
import com.example.data.model.Sponsor
import com.example.ui.theme.*
import com.example.ui.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryScreen(
    viewModel: EventViewModel,
    modifier: Modifier = Modifier
) {
    val speakers by viewModel.allSpeakers.collectAsState()
    val sponsors by viewModel.allSponsors.collectAsState()

    var directoryTab by remember { mutableStateOf(0) } // 0=Speakers, 1=Sponsors
    val tabs = listOf("Speakers Directory", "Sponsor Showcase")

    var selectedSpeaker by remember { mutableStateOf<Speaker?>(null) }
    var selectedSponsor by remember { mutableStateOf<Sponsor?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
    ) {
        TabRow(
            selectedTabIndex = directoryTab,
            containerColor = DasaDeepNavy,
            contentColor = DasaGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[directoryTab]),
                    color = DasaGold
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = directoryTab == index,
                    onClick = { directoryTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (directoryTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    },
                    selectedContentColor = DasaGold,
                    unselectedContentColor = LuxuryTextMuted
                )
            }
        }

        if (directoryTab == 0) {
            // SPEAKERS LIST
            if (speakers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DasaGold)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("speakers_list")
                ) {
                    items(speakers, key = { it.id }) { speaker ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSpeaker = speaker }
                                .testTag("speaker_card_${speaker.id}"),
                            colors = CardDefaults.cardColors(containerColor = LuxuryCardBg),
                            border = BorderStroke(1.dp, BorderColor),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar Placeholder
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(DasaDeepNavy, RoundedCornerShape(12.dp))
                                        .border(1.5.dp, DasaGold, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = speaker.name,
                                        tint = DasaGold,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = speaker.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = LuxuryTextLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = speaker.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = DasaGold,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = speaker.company,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LuxuryTextMuted
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "View Bio",
                                    tint = DasaGold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // SPONSORS SHOWCASE LIST
            if (sponsors.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DasaGold)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("sponsors_list")
                ) {
                    items(sponsors, key = { it.id }) { sponsor ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSponsor = sponsor }
                                .testTag("sponsor_card_${sponsor.id}"),
                            colors = CardDefaults.cardColors(containerColor = LuxuryCardBg),
                            border = BorderStroke(1.dp, BorderColor),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Tier Badge
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                when (sponsor.tier) {
                                                    "Platinum" -> DasaGold.copy(alpha = 0.15f)
                                                    "Gold" -> DasaGold.copy(alpha = 0.1f)
                                                    else -> DasaEmerald.copy(alpha = 0.1f)
                                                },
                                                RoundedCornerShape(8.dp)
                                            )
                                            .border(
                                                1.dp,
                                                when (sponsor.tier) {
                                                    "Platinum" -> DasaGold
                                                    "Gold" -> DasaGold.copy(alpha = 0.6f)
                                                    else -> DasaEmerald
                                                },
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = sponsor.tier.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = when (sponsor.tier) {
                                                "Platinum" -> DasaGold
                                                "Gold" -> DasaGold
                                                else -> DasaEmerald
                                            },
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ArrowOutward,
                                        contentDescription = "Visit Website",
                                        tint = DasaGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(DasaDeepNavy, RoundedCornerShape(8.dp))
                                            .border(1.dp, BorderColor, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Business,
                                            contentDescription = sponsor.name,
                                            tint = DasaGold,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = sponsor.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = LuxuryTextLight,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = sponsor.website,
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
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // SPEAKER BIO POPUP
    if (selectedSpeaker != null) {
        val speaker = selectedSpeaker!!
        AlertDialog(
            onDismissRequest = { selectedSpeaker = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SPEAKER PROFILES", style = MaterialTheme.typography.titleSmall, color = DasaGold)
                    IconButton(onClick = { selectedSpeaker = null }) {
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
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .align(Alignment.CenterHorizontally)
                            .background(DasaDeepNavy, RoundedCornerShape(16.dp))
                            .border(2.dp, DasaGold, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = DasaGold, modifier = Modifier.size(44.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = speaker.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = LuxuryTextLight,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = speaker.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = DasaGold,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = speaker.company,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LuxuryTextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = BorderColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "PROFESSIONAL BIOGRAPHY",
                        style = MaterialTheme.typography.labelSmall,
                        color = DasaGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = speaker.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LuxuryTextLight,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LuxuryDarkBg, RoundedCornerShape(8.dp))
                            .clickable { }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Link, contentDescription = "LinkedIn", tint = DasaGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(speaker.linkedin, style = MaterialTheme.typography.bodyMedium, color = DasaGold)
                    }
                }
            },
            confirmButton = {},
            containerColor = LuxuryCardBg,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // SPONSOR BIO POPUP
    if (selectedSponsor != null) {
        val sponsor = selectedSponsor!!
        AlertDialog(
            onDismissRequest = { selectedSponsor = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SPONSOR PORTFOLIO", style = MaterialTheme.typography.titleSmall, color = DasaGold)
                    IconButton(onClick = { selectedSponsor = null }) {
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
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .align(Alignment.CenterHorizontally)
                            .background(DasaDeepNavy, RoundedCornerShape(12.dp))
                            .border(1.5.dp, DasaGold, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = DasaGold, modifier = Modifier.size(36.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = sponsor.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = LuxuryTextLight,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "${sponsor.tier} Partner",
                        style = MaterialTheme.typography.titleMedium,
                        color = DasaGold,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = BorderColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "CORPORATE STATEMENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = DasaGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = sponsor.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LuxuryTextLight,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LuxuryDarkBg, RoundedCornerShape(8.dp))
                            .clickable { }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Language, contentDescription = "Website", tint = DasaGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(sponsor.website, style = MaterialTheme.typography.bodyMedium, color = DasaGold)
                    }
                }
            },
            confirmButton = {},
            containerColor = LuxuryCardBg,
            shape = RoundedCornerShape(24.dp)
        )
    }
}
