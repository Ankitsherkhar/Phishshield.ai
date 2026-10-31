package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.RiskLevel
import com.example.model.ScanResult
import com.example.model.ScanType
import com.example.model.ThreatIndicator
import com.example.model.UrlAnalysis
import org.json.JSONArray
import org.json.JSONObject

/**
 * Room Entity representing a persistent security threat analysis scan result.
 */
@Entity(tableName = "scan_history")
data class ScanResultEntity(
    @PrimaryKey
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
    val indicatorsJson: String,
    val urlAnalysisJson: String?,
    val socialEngineeringTacticsJson: String,
    val aiExplanation: String,
    val recommendedActionsJson: String,
    val isDemo: Boolean
) {

    /**
     * Maps database entity to rich UI domain model [ScanResult].
     */
    fun toDomainModel(): ScanResult {
        return ScanResult(
            id = id,
            type = type,
            title = title,
            snippet = snippet,
            rawContent = rawContent,
            score = score,
            riskLevel = riskLevel,
            timestamp = timestamp,
            formattedDate = formattedDate,
            summary = summary,
            indicators = parseIndicators(indicatorsJson),
            urlAnalysis = parseUrlAnalysis(urlAnalysisJson),
            socialEngineeringTactics = parseStringList(socialEngineeringTacticsJson),
            aiExplanation = aiExplanation,
            recommendedActions = parseStringList(recommendedActionsJson),
            isDemo = isDemo
        )
    }

    companion object {
        fun fromDomainModel(domain: ScanResult): ScanResultEntity {
            return ScanResultEntity(
                id = domain.id,
                type = domain.type,
                title = domain.title,
                snippet = domain.snippet,
                rawContent = domain.rawContent,
                score = domain.score,
                riskLevel = domain.riskLevel,
                timestamp = domain.timestamp,
                formattedDate = domain.formattedDate,
                summary = domain.summary,
                indicatorsJson = serializeIndicators(domain.indicators),
                urlAnalysisJson = serializeUrlAnalysis(domain.urlAnalysis),
                socialEngineeringTacticsJson = serializeStringList(domain.socialEngineeringTactics),
                aiExplanation = domain.aiExplanation,
                recommendedActionsJson = serializeStringList(domain.recommendedActions),
                isDemo = domain.isDemo
            )
        }

        private fun serializeStringList(list: List<String>): String {
            val array = JSONArray()
            list.forEach { array.put(it) }
            return array.toString()
        }

        private fun parseStringList(json: String?): List<String> {
            if (json.isNullOrBlank()) return emptyList()
            return try {
                val array = JSONArray(json)
                val list = ArrayList<String>(array.length())
                for (i in 0 until array.length()) {
                    list.add(array.getString(i))
                }
                list
            } catch (_: Exception) {
                emptyList()
            }
        }

        private fun serializeIndicators(indicators: List<ThreatIndicator>): String {
            val array = JSONArray()
            indicators.forEach { ind ->
                val obj = JSONObject()
                obj.put("title", ind.title)
                obj.put("description", ind.description)
                obj.put("severity", ind.severity.name)
                obj.put("category", ind.category)
                array.put(obj)
            }
            return array.toString()
        }

        private fun parseIndicators(json: String?): List<ThreatIndicator> {
            if (json.isNullOrBlank()) return emptyList()
            return try {
                val array = JSONArray(json)
                val list = ArrayList<ThreatIndicator>(array.length())
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val severityStr = obj.optString("severity", RiskLevel.MEDIUM.name)
                    val severity = try {
                        RiskLevel.valueOf(severityStr)
                    } catch (_: Exception) {
                        RiskLevel.MEDIUM
                    }
                    list.add(
                        ThreatIndicator(
                            title = obj.optString("title", ""),
                            description = obj.optString("description", ""),
                            severity = severity,
                            category = obj.optString("category", "General")
                        )
                    )
                }
                list
            } catch (_: Exception) {
                emptyList()
            }
        }

        private fun serializeUrlAnalysis(analysis: UrlAnalysis?): String? {
            if (analysis == null) return null
            val obj = JSONObject()
            obj.put("originalUrl", analysis.originalUrl)
            obj.put("domain", analysis.domain)
            obj.put("protocol", analysis.protocol)
            obj.put("isHttps", analysis.isHttps)
            obj.put("suspiciousTld", analysis.suspiciousTld)
            obj.put("hasIpHost", analysis.hasIpHost)
            obj.put("hasTyposquatting", analysis.hasTyposquatting)
            obj.put("entropyRating", analysis.entropyRating)
            obj.put("domainAgeStatus", analysis.domainAgeStatus)
            return obj.toString()
        }

        private fun parseUrlAnalysis(json: String?): UrlAnalysis? {
            if (json.isNullOrBlank()) return null
            return try {
                val obj = JSONObject(json)
                UrlAnalysis(
                    originalUrl = obj.optString("originalUrl", ""),
                    domain = obj.optString("domain", ""),
                    protocol = obj.optString("protocol", "http"),
                    isHttps = obj.optBoolean("isHttps", false),
                    suspiciousTld = obj.optBoolean("suspiciousTld", false),
                    hasIpHost = obj.optBoolean("hasIpHost", false),
                    hasTyposquatting = obj.optBoolean("hasTyposquatting", false),
                    entropyRating = obj.optString("entropyRating", "Low"),
                    domainAgeStatus = obj.optString("domainAgeStatus", "Unknown")
                )
            } catch (_: Exception) {
                null
            }
        }
    }
}

fun ScanResult.toEntity(): ScanResultEntity = ScanResultEntity.fromDomainModel(this)
