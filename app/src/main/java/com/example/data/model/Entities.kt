package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val email: String,
    val name: String,
    val company: String,
    val title: String,
    val role: String, // "Delegate", "Speaker", "Sponsor", "Volunteer", "Admin"
    val qrCodeContent: String,
    val isLoggedIn: Boolean = false
)

@Entity(tableName = "sessions")
data class EventSession(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val date: String, // "Day 1 - Oct 12", "Day 2 - Oct 13"
    val speakerId: String,
    val speakerName: String,
    val location: String, // "Plenary Hall", "Room A", etc.
    val category: String, // "Web3", "CBDC", "DeFi", "Policy"
    val isBookmarked: Boolean = false,
    val rating: Float = 0f,
    val feedbackComment: String = ""
)

@Entity(tableName = "speakers")
data class Speaker(
    @PrimaryKey val id: String,
    val name: String,
    val title: String,
    val company: String,
    val bio: String,
    val linkedin: String,
    val avatarUrl: String = ""
)

@Entity(tableName = "sponsors")
data class Sponsor(
    @PrimaryKey val id: String,
    val name: String,
    val tier: String, // "Platinum", "Gold", "Silver", "Bronze"
    val description: String,
    val logoUrl: String = "",
    val website: String = ""
)

@Entity(tableName = "attendees")
data class Attendee(
    @PrimaryKey val id: String,
    val name: String,
    val company: String,
    val title: String,
    val role: String,
    val connectionStatus: String = "none", // "none", "pending_sent", "pending_received", "connected"
    val digitalCard: String = "" // JSON or text containing contact details
)

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = "General" // "General", "Schedule", "Room Change"
)
