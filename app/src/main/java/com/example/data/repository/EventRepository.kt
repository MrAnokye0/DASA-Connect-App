package com.example.data.repository

import com.example.data.db.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class EventRepository(private val db: AppDatabase) {

    val userProfile: Flow<UserProfile?> = db.userProfileDao().getUserProfile()
    val allSessions: Flow<List<EventSession>> = db.eventSessionDao().getAllSessions()
    val bookmarkedSessions: Flow<List<EventSession>> = db.eventSessionDao().getBookmarkedSessions()
    val allSpeakers: Flow<List<Speaker>> = db.speakerDao().getAllSpeakers()
    val allSponsors: Flow<List<Sponsor>> = db.sponsorDao().getAllSponsors()
    val allAttendees: Flow<List<Attendee>> = db.attendeeDao().getAllAttendees()
    val allAnnouncements: Flow<List<Announcement>> = db.announcementDao().getAllAnnouncements()

    suspend fun getUserProfileDirect(): UserProfile? = db.userProfileDao().getUserProfileDirect()

    suspend fun saveUserProfile(profile: UserProfile) {
        db.userProfileDao().saveUserProfile(profile)
    }

    suspend fun logout() {
        db.userProfileDao().clearUserProfile()
    }

    suspend fun toggleBookmark(id: String, bookmarked: Boolean) {
        db.eventSessionDao().updateBookmark(id, bookmarked)
    }

    suspend fun submitFeedback(id: String, rating: Float, comment: String) {
        db.eventSessionDao().submitFeedback(id, rating, comment)
    }

    suspend fun updateConnectionStatus(id: String, status: String) {
        db.attendeeDao().updateConnectionStatus(id, status)
    }

    suspend fun addAnnouncement(title: String, content: String, category: String = "General") {
        db.announcementDao().insertAnnouncement(
            Announcement(title = title, content = content, category = category)
        )
    }

    suspend fun seedDataIfEmpty() {
        // Only seed if speakers is empty
        val currentSpeakers = db.speakerDao().getAllSpeakers().first()
        if (currentSpeakers.isEmpty()) {
            // Seed Speakers
            val speakersList = listOf(
                Speaker(
                    id = "spk1",
                    name = "Dr. Elikem Adonoo",
                    title = "Director of Financial Technology",
                    company = "Bank of Ghana",
                    bio = "Dr. Elikem is a leading pioneer in CBDC frameworks across Sub-Saharan Africa. He leads the eCedi project and regulates retail payment frameworks.",
                    linkedin = "linkedin.com/in/elikem-adonoo",
                    avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=200"
                ),
                Speaker(
                    id = "spk2",
                    name = "Fatoumata Diallo",
                    title = "Founder & CEO",
                    company = "Nile Digital Capital",
                    bio = "Fatoumata manages a $50M venture fund backing infrastructure startups in East and West Africa. She holds degrees from INSEAD.",
                    linkedin = "linkedin.com/in/fatoumata-diallo",
                    avatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=200"
                ),
                Speaker(
                    id = "spk3",
                    name = "Chidi Okechukwu",
                    title = "Chief Policy Strategist",
                    company = "Digital Assets Africa Initiative",
                    bio = "Chidi advises ministries of finance and tech regulators on crypto asset taxonomy, anti-money laundering, and cross-border settlement architectures.",
                    linkedin = "linkedin.com/in/chidi-okechukwu",
                    avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=200"
                ),
                Speaker(
                    id = "spk4",
                    name = "Amara Mensah",
                    title = "Head of Blockchain Strategy",
                    company = "Standard Capital Group",
                    bio = "Amara has 12 years of investment banking experience and currently oversees asset tokenization pilots on private Ethereum networks.",
                    linkedin = "linkedin.com/in/amara-mensah",
                    avatarUrl = "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&q=80&w=200"
                )
            )
            db.speakerDao().insertSpeakers(speakersList)

            // Seed Sponsors
            val sponsorsList = listOf(
                Sponsor(
                    id = "spon1",
                    name = "Apex Clearing Bank",
                    tier = "Platinum",
                    description = "Apex Clearing Bank is a tier-1 banking institution driving wholesale liquid digital clearing across the African continent.",
                    logoUrl = "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?auto=format&fit=crop&q=80&w=200",
                    website = "https://apexclearing.com"
                ),
                Sponsor(
                    id = "spon2",
                    name = "Vanguard Digital Ledger",
                    tier = "Platinum",
                    description = "Providing institutional-grade custody systems, nodes management, and compliant asset fractionalization architectures.",
                    logoUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&q=80&w=200",
                    website = "https://vanguardledger.io"
                ),
                Sponsor(
                    id = "spon3",
                    name = "ePay Network",
                    tier = "Gold",
                    description = "ePay Network connects instant mobile money schemes with global stablecoin settlement layers seamlessly.",
                    logoUrl = "https://images.unsplash.com/photo-1563013544-824ae1d704d3?auto=format&fit=crop&q=80&w=200",
                    website = "https://epaynetwork.org"
                ),
                Sponsor(
                    id = "spon4",
                    name = "TrustTech Custody",
                    tier = "Silver",
                    description = "Cold storage and enterprise multisig wallet integration for commercial entities.",
                    logoUrl = "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?auto=format&fit=crop&q=80&w=200",
                    website = "https://trusttech.io"
                )
            )
            db.sponsorDao().insertSponsors(sponsorsList)

            // Seed Sessions
            val sessionsList = listOf(
                EventSession(
                    id = "ses1",
                    title = "Opening Plenary: Africa's Role in the Global Web3 Ecosystem",
                    description = "An executive opening panel looking at standardizing cross-border digital payments, digital dollarization risks, and sub-saharan growth trajectories.",
                    startTime = "09:00 AM",
                    endTime = "10:30 AM",
                    date = "Day 1 - Oct 12",
                    speakerId = "spk2",
                    speakerName = "Fatoumata Diallo",
                    location = "Plenary Hall (Level 3)",
                    category = "Policy"
                ),
                EventSession(
                    id = "ses2",
                    title = "Central Bank Digital Currencies: CBDC Pilots & Practical Lessons",
                    description = "A deep dive into the technology choices (e.g. Hyperledger vs Corda) of African central banks. Real operational data from eCedi and eNaira implementations.",
                    startTime = "11:00 AM",
                    endTime = "12:30 PM",
                    date = "Day 1 - Oct 12",
                    speakerId = "spk1",
                    speakerName = "Dr. Elikem Adonoo",
                    location = "Executive Boardroom A",
                    category = "CBDC"
                ),
                EventSession(
                    id = "ses3",
                    title = "Asset Tokenization & The Future of African Debt Markets",
                    description = "Analyzing how fractionalizing bonds and treasury bills onto public ledgers increases domestic retail access and global institutional liquidity pool inputs.",
                    startTime = "02:00 PM",
                    endTime = "03:30 PM",
                    date = "Day 1 - Oct 12",
                    speakerId = "spk4",
                    speakerName = "Amara Mensah",
                    location = "Exhibition Theater",
                    category = "DeFi"
                ),
                EventSession(
                    id = "ses4",
                    title = "Fireside Chat: Regulating Crypto Assets without Stifling Innovation",
                    description = "A standard policy panel balancing consumer protection compliance with sandboxing strategies for high-frequency trading platforms and wallet providers.",
                    startTime = "04:00 PM",
                    endTime = "05:00 PM",
                    date = "Day 1 - Oct 12",
                    speakerId = "spk3",
                    speakerName = "Chidi Okechukwu",
                    location = "Plenary Hall (Level 3)",
                    category = "Policy"
                ),
                EventSession(
                    id = "ses5",
                    title = "Day 2 Keynote: Cross-Border Liquidity and Stablecoin Rails",
                    description = "Analyzing the friction point of traditional correspondent banking and presenting the active scaling of stablecoin networks in liquidity-scarce hubs.",
                    startTime = "09:30 AM",
                    endTime = "10:45 AM",
                    date = "Day 2 - Oct 13",
                    speakerId = "spk2",
                    speakerName = "Fatoumata Diallo",
                    location = "Plenary Hall (Level 3)",
                    category = "DeFi"
                ),
                EventSession(
                    id = "ses6",
                    title = "Technical Deep Dive: Multi-Sig Safes and Cold Storage Architectures",
                    description = "A highly developer-oriented session on protecting institutional digital treasury assets from hacking vectors and physical compromise protocols.",
                    startTime = "11:15 AM",
                    endTime = "12:30 PM",
                    date = "Day 2 - Oct 13",
                    speakerId = "spk4",
                    speakerName = "Amara Mensah",
                    location = "Tech Lab (Room 202)",
                    category = "Web3"
                )
            )
            db.eventSessionDao().insertSessions(sessionsList)

            // Seed Attendees (Networking)
            val attendeesList = listOf(
                Attendee(
                    id = "att1",
                    name = "Kofi Boateng",
                    company = "Ghana Web3 Hub",
                    title = "Managing Director",
                    role = "Delegate",
                    connectionStatus = "none",
                    digitalCard = "Email: kofi@ghanaweb3.org | Telegram: @kofiboat"
                ),
                Attendee(
                    id = "att2",
                    name = "Nneka Ndlovu",
                    company = "Nile Digital Capital",
                    title = "Senior Associate",
                    role = "Sponsor",
                    connectionStatus = "pending_received",
                    digitalCard = "Email: nneka@niledigital.cap"
                ),
                Attendee(
                    id = "att3",
                    name = "Tariq Yusuf",
                    company = "Sovereign Ledger Inc.",
                    title = "Lead Security Architect",
                    role = "Delegate",
                    connectionStatus = "connected",
                    digitalCard = "Email: tariq@sovereignledger.io | GitHub: tyusuf"
                ),
                Attendee(
                    id = "att4",
                    name = "Zola Mtetwa",
                    company = "Fintech Association of SA",
                    title = "Executive Director",
                    role = "Delegate",
                    connectionStatus = "none",
                    digitalCard = "Email: zola@fintechassociation.za"
                )
            )
            db.attendeeDao().insertAttendees(attendeesList)

            // Seed initial announcements
            db.announcementDao().insertAnnouncement(
                Announcement(
                    title = "Welcome to DASA Connect!",
                    content = "Welcome to the Digital Assets Summit Africa (DASA) companion application. Enjoy high-speed networking, live session bookmarks, and your personalized digital QR badge.",
                    category = "General"
                )
            )
            db.announcementDao().insertAnnouncement(
                Announcement(
                    title = "Opening Plenary Starts Soon",
                    content = "Please make your way to the Plenary Hall (Level 3). The opening panel on Africa's Web3 Ecosystem will start in 10 minutes.",
                    category = "Schedule"
                )
            )
        }
    }
}
