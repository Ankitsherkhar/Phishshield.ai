package com.example.model

/**
 * Defines the comprehensive structure of a PhishShield AI security analysis report.
 *
 * @property id Unique identifier for the audit report.
 * @property type The target communication channel scanned (Email, SMS, or URL).
 * @property title Concise heading or subject line representing the scanned item.
 * @property snippet Shortened text excerpt for previews in history logs.
 * @property rawContent Full unedited text or URL payload submitted for analysis.
 * @property score Normalized threat risk score from 0 (completely safe) to 100 (critical attack).
 * @property riskLevel Categorized severity level (LOW, MEDIUM, HIGH, VERY_HIGH).
 * @property timestamp Unix epoch timestamp in milliseconds when the analysis occurred.
 * @property formattedDate Pre-formatted human-readable timestamp (e.g. "Oct 31, 2026 · 10:45").
 * @property summary Executive summary of the threat verdict.
 * @property indicators List of specific suspicious threat indicators identified by heuristic & NLP models.
 * @property urlAnalysis Technical domain, protocol, homoglyph, and entropy telemetry (null if not a URL vector).
 * @property socialEngineeringTactics Specific psychological manipulation patterns (e.g. Urgency, Authority Impersonation).
 * @property aiExplanation Plain-English Explainable AI (XAI) rationale describing WHY the content was flagged.
 * @property recommendedActions Concrete, prioritized security countermeasures for user defense.
 * @property isDemo Flag explicitly differentiating demonstration/preset reports from active user scans.
 */
data class ScanResult(
    val id: String,
    val type: ScanType,
    val title: String,
    val snippet: String,
    val rawContent: String,
    val score: Int,
    val riskLevel: RiskLevel,
    val timestamp: Long,
    val formattedDate: String,
    val summary: String,
    val indicators: List<ThreatIndicator> = emptyList(),
    val urlAnalysis: UrlAnalysis? = null,
    val socialEngineeringTactics: List<String> = emptyList(),
    val aiExplanation: String,
    val recommendedActions: List<String> = emptyList(),
    val isDemo: Boolean = false
) {
    val isCriticalThreat: Boolean
        get() = riskLevel == RiskLevel.VERY_HIGH || score >= 80

    val isBenign: Boolean
        get() = riskLevel == RiskLevel.LOW && score < 25
}
