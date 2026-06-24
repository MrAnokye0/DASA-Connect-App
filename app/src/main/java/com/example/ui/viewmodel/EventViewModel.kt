package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.EventRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EventRepository
    
    val userProfile: StateFlow<UserProfile?>
    val allSessions: StateFlow<List<EventSession>>
    val bookmarkedSessions: StateFlow<List<EventSession>>
    val allSpeakers: StateFlow<List<Speaker>>
    val allSponsors: StateFlow<List<Sponsor>>
    val allAttendees: StateFlow<List<Attendee>>
    val allAnnouncements: StateFlow<List<Announcement>>

    // UI States
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(ChatMessage("ai", "Hello! I am your DASA Connect AI Event Assistant. How can I help you navigate the Digital Assets Summit Africa? You can ask me about sessions, speakers, sponsors, or Web3 policy!"))
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _aiRecommendation = MutableStateFlow<String>("")
    val aiRecommendation: StateFlow<String> = _aiRecommendation.asStateFlow()

    private val _isRecommendationLoading = MutableStateFlow(false)
    val isRecommendationLoading: StateFlow<Boolean> = _isRecommendationLoading.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = EventRepository(database)

        userProfile = repository.userProfile.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        allSessions = repository.allSessions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        bookmarkedSessions = repository.bookmarkedSessions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allSpeakers = repository.allSpeakers.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allSponsors = repository.allSponsors.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allAttendees = repository.allAttendees.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allAnnouncements = repository.allAnnouncements.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.seedDataIfEmpty()
            repository.syncWithRemoteBackend()
        }
    }

    fun registerUser(
        name: String, 
        email: String, 
        company: String, 
        title: String, 
        role: String,
        bio: String = "",
        networkingPrefs: String = "Open to Network",
        phone: String = "",
        linkedin: String = "",
        twitter: String = "",
        github: String = "",
        cardTheme: String = "Gold Premium"
    ) {
        viewModelScope.launch {
            val badgeContent = "DASA-2026|$name|$email|$company|$role|$networkingPrefs|$linkedin"
            val profile = UserProfile(
                email = email,
                name = name,
                company = company,
                title = title,
                role = role,
                qrCodeContent = badgeContent,
                isLoggedIn = true,
                bio = bio,
                networkingPrefs = networkingPrefs,
                phone = phone,
                linkedin = linkedin,
                twitter = twitter,
                github = github,
                cardTheme = cardTheme
            )
            repository.saveUserProfile(profile)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun toggleBookmark(sessionId: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleBookmark(sessionId, isBookmarked)
        }
    }

    fun submitFeedback(sessionId: String, rating: Float, comment: String) {
        viewModelScope.launch {
            repository.submitFeedback(sessionId, rating, comment)
        }
    }

    fun sendConnectionRequest(attendeeId: String) {
        viewModelScope.launch {
            repository.updateConnectionStatus(attendeeId, "pending_sent")
        }
    }

    fun acceptConnection(attendeeId: String) {
        viewModelScope.launch {
            repository.updateConnectionStatus(attendeeId, "connected")
        }
    }

    fun removeConnection(attendeeId: String) {
        viewModelScope.launch {
            repository.updateConnectionStatus(attendeeId, "none")
        }
    }

    fun broadcastAnnouncement(title: String, content: String, category: String) {
        viewModelScope.launch {
            repository.addAnnouncement(title, content, category)
        }
    }

    // --- Gemini AI Actions ---

    fun askAiAssistant(query: String) {
        if (query.isBlank()) return

        val userMsg = ChatMessage("user", query)
        _chatMessages.update { it + userMsg }

        viewModelScope.launch {
            _isChatLoading.value = true

            // Gather context to help Gemini give a perfect answers
            val sessionsText = allSessions.value.joinToString("\n") { 
                "- ${it.title} by ${it.speakerName} at ${it.startTime} (${it.location})" 
            }
            val speakersText = allSpeakers.value.joinToString("\n") {
                "- ${it.name} (${it.title} at ${it.company}): ${it.bio}"
            }
            val sponsorsText = allSponsors.value.joinToString("\n") {
                "- ${it.name} (${it.tier} Sponsor)"
            }

            val systemPrompt = """
                You are the official AI Event Assistant for Digital Assets Summit Africa (DASA) 2026.
                Your role is to act as an executive, helpful, and friendly concierge.
                Here is the verified list of sessions:
                $sessionsText
                
                Here is the verified list of speakers:
                $speakersText
                
                Here is the verified list of sponsors:
                $sponsorsText
                
                Answer the user's questions about DASA accurately using this information. If they ask about something not in the list, provide a helpful general response about fintech/blockchain in Africa, but mention that you don't have official summit details on it. Keep your tone professional, executive, and highly polished.
            """.trimIndent()

            val responseText = GeminiClient.queryGemini(query, systemPrompt)
            
            _chatMessages.update { it + ChatMessage("ai", responseText) }
            _isChatLoading.value = false
        }
    }

    fun generateNetworkingRecommendations() {
        val profile = userProfile.value ?: return
        viewModelScope.launch {
            _isRecommendationLoading.value = true

            val attendeesList = allAttendees.value.joinToString("\n") {
                "- ${it.name} (${it.title} at ${it.company})"
            }

            val prompt = """
                My profile is:
                Name: ${profile.name}
                Title: ${profile.title}
                Company: ${profile.company}
                Role: ${profile.role}
                
                Here is a list of other attendees:
                $attendeesList
                
                Recommend 2 or 3 attendees I should connect with and suggest an icebreaker for each based on their titles and companies. Make it look professional, appealing, and ready to read in a clean layout.
            """.trimIndent()

            val response = GeminiClient.queryGemini(prompt, "You are a networking matchmaker at the DASA summit.")
            _aiRecommendation.value = response
            _isRecommendationLoading.value = false
        }
    }
}
