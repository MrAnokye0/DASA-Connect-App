package com.example.data.db

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUserProfileDirect(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    @Query("DELETE FROM user_profile")
    suspend fun clearUserProfile()
}

@Dao
interface EventSessionDao {
    @Query("SELECT * FROM sessions ORDER BY date ASC, startTime ASC")
    fun getAllSessions(): Flow<List<EventSession>>

    @Query("SELECT * FROM sessions WHERE isBookmarked = 1 ORDER BY date ASC, startTime ASC")
    fun getBookmarkedSessions(): Flow<List<EventSession>>

    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun getSessionById(id: String): EventSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<EventSession>)

    @Query("UPDATE sessions SET isBookmarked = :bookmarked WHERE id = :id")
    suspend fun updateBookmark(id: String, bookmarked: Boolean)

    @Query("UPDATE sessions SET rating = :rating, feedbackComment = :comment WHERE id = :id")
    suspend fun submitFeedback(id: String, rating: Float, comment: String)

    @Query("DELETE FROM sessions")
    suspend fun clearSessions()
}

@Dao
interface SpeakerDao {
    @Query("SELECT * FROM speakers ORDER BY name ASC")
    fun getAllSpeakers(): Flow<List<Speaker>>

    @Query("SELECT * FROM speakers WHERE id = :id")
    suspend fun getSpeakerById(id: String): Speaker?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeakers(speakers: List<Speaker>)

    @Query("DELETE FROM speakers")
    suspend fun clearSpeakers()
}

@Dao
interface SponsorDao {
    @Query("SELECT * FROM sponsors ORDER BY tier ASC, name ASC")
    fun getAllSponsors(): Flow<List<Sponsor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSponsors(sponsors: List<Sponsor>)

    @Query("DELETE FROM sponsors")
    suspend fun clearSponsors()
}

@Dao
interface AttendeeDao {
    @Query("SELECT * FROM attendees ORDER BY name ASC")
    fun getAllAttendees(): Flow<List<Attendee>>

    @Query("SELECT * FROM attendees WHERE connectionStatus = 'connected'")
    fun getConnectedAttendees(): Flow<List<Attendee>>

    @Query("UPDATE attendees SET connectionStatus = :status WHERE id = :id")
    suspend fun updateConnectionStatus(id: String, status: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendees(attendees: List<Attendee>)

    @Query("DELETE FROM attendees")
    suspend fun clearAttendees()
}

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY timestamp DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement)

    @Query("DELETE FROM announcements")
    suspend fun clearAnnouncements()
}

@Database(
    entities = [
        UserProfile::class,
        EventSession::class,
        Speaker::class,
        Sponsor::class,
        Attendee::class,
        Announcement::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun eventSessionDao(): EventSessionDao
    abstract fun speakerDao(): SpeakerDao
    abstract fun sponsorDao(): SponsorDao
    abstract fun attendeeDao(): AttendeeDao
    abstract fun announcementDao(): AnnouncementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dasa_connect_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
