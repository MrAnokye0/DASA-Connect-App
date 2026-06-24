package com.example.data.api

import com.example.data.model.Announcement
import com.example.data.model.EventSession
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.*

@JsonClass(generateAdapter = true)
data class ProfileRequest(
    val email: String,
    val name: String,
    val company: String,
    val title: String,
    val role: String,
    @Json(name = "qrCodePayload") val qrCodePayload: String,
    val bio: String,
    val networkingPrefs: String,
    val phone: String,
    val linkedin: String,
    val twitter: String,
    val github: String,
    val cardTheme: String
)

@JsonClass(generateAdapter = true)
data class ProfileResponse(
    val success: Boolean,
    val data: ProfileData? = null,
    val message: String? = null
)

@JsonClass(generateAdapter = true)
data class ProfileData(
    val email: String,
    val name: String,
    val company: String?,
    val title: String?,
    val role: String?,
    @Json(name = "qr_code_payload") val qrCodePayload: String?,
    val bio: String?,
    @Json(name = "networking_prefs") val networkingPrefs: String?,
    val phone: String?,
    val linkedin: String?,
    val twitter: String?,
    val github: String?,
    @Json(name = "card_theme") val cardTheme: String?
)

@JsonClass(generateAdapter = true)
data class BookmarkRequest(
    val email: String,
    val bookmarked: Boolean
)

@JsonClass(generateAdapter = true)
data class FeedbackRequest(
    val email: String?,
    val rating: Float,
    val comment: String?
)

@JsonClass(generateAdapter = true)
data class GeneralResponse(
    val success: Boolean,
    val message: String? = null
)

@JsonClass(generateAdapter = true)
data class AnnouncementsResponse(
    val success: Boolean,
    val data: List<AnnouncementData>? = null
)

@JsonClass(generateAdapter = true)
data class AnnouncementData(
    val id: Int,
    val title: String,
    val content: String,
    val category: String,
    @Json(name = "created_at") val createdAt: String?
)

interface BackendApi {

    @GET("api/profile/{email}")
    suspend fun getProfile(@Path("email") email: String): ProfileResponse

    @POST("api/profile")
    suspend fun saveProfile(@Body request: ProfileRequest): GeneralResponse

    @POST("api/sessions/{id}/bookmark")
    suspend fun toggleBookmark(
        @Path("id") sessionId: String,
        @Body request: BookmarkRequest
    ): GeneralResponse

    @POST("api/sessions/{id}/feedback")
    suspend fun submitFeedback(
        @Path("id") sessionId: String,
        @Body request: FeedbackRequest
    ): GeneralResponse

    @GET("api/announcements")
    suspend fun getAnnouncements(): AnnouncementsResponse
}
