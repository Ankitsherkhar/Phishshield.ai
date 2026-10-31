package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.RiskCriticalRed
import com.example.ui.theme.RiskHighOrange
import com.example.ui.theme.RiskLowGreen
import com.example.ui.theme.RiskMediumAmber

enum class ScanType(val displayName: String, val badgeText: String) {
    EMAIL("Email", "EMAIL SCAN"),
    SMS("SMS / Message", "SMS / MSG"),
    URL("Malicious URL", "URL SCAN")
}

enum class RiskLevel(
    val displayName: String,
    val color: Color,
    val description: String
) {
    LOW("Low Risk", RiskLowGreen, "Content appears benign. No critical attack patterns detected."),
    MEDIUM("Medium Risk", RiskMediumAmber, "Suspicious indicators detected. Exercise caution before proceeding."),
    HIGH("High Risk", RiskHighOrange, "Dangerous content. Likely phishing attempt or social engineering."),
    VERY_HIGH("Very High Risk", RiskCriticalRed, "Active threat detected. High probability of credential harvesting or malware lure.")
}

data class ThreatIndicator(
    val title: String,
    val description: String,
    val severity: RiskLevel,
    val category: String
)

data class UrlAnalysis(
    val originalUrl: String,
    val domain: String,
    val protocol: String,
    val isHttps: Boolean,
    val suspiciousTld: Boolean,
    val hasIpHost: Boolean,
    val hasTyposquatting: Boolean,
    val entropyRating: String,
    val domainAgeStatus: String
)

data class UserProfile(
    val name: String = "Alex Hunter",
    val email: String = "a.hunter@securitylab.edu",
    val role: String = "Security Analyst (B.Tech AI)",
    val memberSince: String = "Oct 2026",
    val isLoggedIn: Boolean = true
)
