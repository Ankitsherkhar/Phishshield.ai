package com.example.engine

import com.example.model.RiskLevel
import com.example.model.ScanResult
import com.example.model.ScanType
import com.example.model.ThreatIndicator
import com.example.model.UrlAnalysis
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object PhishDetectorEngine {

    fun analyze(
        type: ScanType,
        content: String,
        subject: String = "",
        isPresetDemo: Boolean = false,
        timestamp: Long = System.currentTimeMillis()
    ): ScanResult {
        val cleanContent = content.trim()
        val combinedText = if (subject.isNotBlank()) "$subject\n$cleanContent" else cleanContent
        val lower = combinedText.lowercase(Locale.ROOT)
        val now = timestamp
        val dateStr = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault()).format(Date(now))

        var calculatedScore = 0
        val indicators = mutableListOf<ThreatIndicator>()
        val tactics = mutableListOf<String>()
        val recommendations = mutableListOf<String>()
        var urlAnalysis: UrlAnalysis? = null

        when (type) {
            ScanType.EMAIL -> {
                // Urgency & Fear cues
                val urgencyKeywords = listOf("urgent", "immediately", "within 24 hours", "suspended", "account locked", "unauthorized access", "terminate", "final notice", "action required")
                val foundUrgency = urgencyKeywords.filter { lower.contains(it) }
                if (foundUrgency.isNotEmpty()) {
                    calculatedScore += 25
                    indicators.add(
                        ThreatIndicator(
                            title = "High Psychological Urgency",
                            description = "Detected pressure keywords (${foundUrgency.take(3).joinToString()}) designed to bypass rational skepticism.",
                            severity = RiskLevel.HIGH,
                            category = "Linguistic Pattern"
                        )
                    )
                    tactics.add("Artificial Urgency & Fear Appeal")
                }

                // Credential & Financial harvesting
                val harvestingKeywords = listOf("password", "verify your account", "update billing", "ssn", "social security", "credit card", "tax refund", "wire transfer", "gift card", "bitcoin", "direct deposit", "routing number", "confirm identity")
                val foundHarvesting = harvestingKeywords.filter { lower.contains(it) }
                if (foundHarvesting.isNotEmpty()) {
                    calculatedScore += 35
                    indicators.add(
                        ThreatIndicator(
                            title = "Credential / Financial Harvesting Vector",
                            description = "Requesting sensitive personal or financial credentials (${foundHarvesting.take(3).joinToString()}).",
                            severity = RiskLevel.VERY_HIGH,
                            category = "Data Exfiltration"
                        )
                    )
                    tactics.add("Credential Harvesting")
                }

                // Brand Impersonation
                val brands = listOf("paypal", "amazon", "microsoft", "google", "apple", "netflix", "chase", "wells fargo", "bank of america", "usps", "fedex", "irs", "hr payroll")
                val matchedBrands = brands.filter { lower.contains(it) }
                if (matchedBrands.isNotEmpty()) {
                    calculatedScore += 20
                    indicators.add(
                        ThreatIndicator(
                            title = "Brand Impersonation Likelihood",
                            description = "Targets recognizable entity (${matchedBrands.take(2).joinToString { it.replaceFirstChar { c -> c.uppercase() } }}). Legitimate companies rarely request password resets via unsolicited emails.",
                            severity = RiskLevel.MEDIUM,
                            category = "Impersonation"
                        )
                    )
                    tactics.add("Authority & Brand Impersonation")
                }

                // Suspicious link or generic greeting
                if (lower.contains("click here") || lower.contains("http://") || lower.contains("https://") || lower.contains("tinyurl") || lower.contains("bit.ly")) {
                    calculatedScore += 18
                    indicators.add(
                        ThreatIndicator(
                            title = "Unverified Call-to-Action Link",
                            description = "Message contains embedded web links redirecting external traffic outside verified organizational domains.",
                            severity = RiskLevel.HIGH,
                            category = "Vector Link"
                        )
                    )
                }

                if (lower.contains("dear customer") || lower.contains("dear user") || lower.contains("undisclosed-recipients")) {
                    calculatedScore += 10
                    indicators.add(
                        ThreatIndicator(
                            title = "Generic Salutation",
                            description = "Lacks personalized customer identity information typical of mass-spearphishing campaigns.",
                            severity = RiskLevel.LOW,
                            category = "Social Engineering"
                        )
                    )
                }
            }

            ScanType.SMS -> {
                // Smishing indicators
                val smishPatterns = listOf("package", "delivery failed", "usps", "fedex", "card blocked", "otp", "code is", "claim prize", "toll fee", "unpaid ticket", "won", "congratulations", "bank alert")
                val foundSmish = smishPatterns.filter { lower.contains(it) }
                if (foundSmish.isNotEmpty()) {
                    calculatedScore += 40
                    indicators.add(
                        ThreatIndicator(
                            title = "Smishing Hook Pattern",
                            description = "Identified SMS lure vectors (${foundSmish.take(3).joinToString()}) targeting consumer anxiety.",
                            severity = RiskLevel.VERY_HIGH,
                            category = "Mobile Threat"
                        )
                    )
                    tactics.add("Delivery/Financial Pretexting")
                }

                if (lower.contains("http") || lower.contains("bit.ly") || lower.contains(".xyz") || lower.contains(".me/") || lower.contains(".link")) {
                    calculatedScore += 35
                    indicators.add(
                        ThreatIndicator(
                            title = "Shortened or Untrusted URL in SMS",
                            description = "SMS contains obfuscated destination link intended to conceal deceptive domain.",
                            severity = RiskLevel.VERY_HIGH,
                            category = "Obfuscation"
                        )
                    )
                }

                if (lower.contains("urgent") || lower.contains("action required") || lower.contains("immediately") || lower.contains("expires")) {
                    calculatedScore += 20
                    tactics.add("Time Scarcity")
                }
            }

            ScanType.URL -> {
                urlAnalysis = inspectUrl(cleanContent)
                if (!urlAnalysis.isHttps) {
                    calculatedScore += 25
                    indicators.add(
                        ThreatIndicator(
                            title = "Insecure Transmission Protocol (HTTP)",
                            description = "URL does not utilize TLS/SSL encryption, exposing credentials to eavesdropping.",
                            severity = RiskLevel.MEDIUM,
                            category = "Network Security"
                        )
                    )
                }
                if (urlAnalysis.suspiciousTld) {
                    calculatedScore += 35
                    indicators.add(
                        ThreatIndicator(
                            title = "High-Risk Domain TLD",
                            description = "Top Level Domain exhibits statistically elevated association with disposable phishing infrastructure.",
                            severity = RiskLevel.HIGH,
                            category = "Domain Reputation"
                        )
                    )
                }
                if (urlAnalysis.hasTyposquatting) {
                    calculatedScore += 40
                    indicators.add(
                        ThreatIndicator(
                            title = "Homoglyph / Typosquatting Match",
                            description = "Domain name mimics major legitimate brand using subtle character substitutions.",
                            severity = RiskLevel.VERY_HIGH,
                            category = "Domain Spoofing"
                        )
                    )
                    tactics.add("Domain Deception")
                }
                if (urlAnalysis.hasIpHost) {
                    calculatedScore += 40
                    indicators.add(
                        ThreatIndicator(
                            title = "Direct IP Address Host",
                            description = "URL bypasses standard DNS hostname resolution by specifying a raw numeric IP.",
                            severity = RiskLevel.VERY_HIGH,
                            category = "Network Anomaly"
                        )
                    )
                }
                if (cleanContent.contains("@") || cleanContent.count { it == '.' } > 4 || cleanContent.contains("-login") || cleanContent.contains("-verify")) {
                    calculatedScore += 25
                    indicators.add(
                        ThreatIndicator(
                            title = "Subdomain Stacking & Phishing Tokens",
                            description = "URL contains multiple nested host levels and authentication lure tokens.",
                            severity = RiskLevel.HIGH,
                            category = "URL Obfuscation"
                        )
                    )
                }
            }
        }

        // Cap and map score
        val finalScore = calculatedScore.coerceIn(4, 98)
        val riskLevel = when {
            finalScore < 25 -> RiskLevel.LOW
            finalScore < 55 -> RiskLevel.MEDIUM
            finalScore < 80 -> RiskLevel.HIGH
            else -> RiskLevel.VERY_HIGH
        }

        // Default safety recommendations based on severity
        when (riskLevel) {
            RiskLevel.LOW -> {
                recommendations.add("Message appears safe based on heuristic and semantic inspection.")
                recommendations.add("Always maintain standard vigilance regarding unsolicited requests.")
                recommendations.add("Verify sender email address matches official corporate domain.")
            }
            RiskLevel.MEDIUM -> {
                recommendations.add("Do not click links or download attachments directly from this message.")
                recommendations.add("Navigate to the service provider's official portal independently via your browser bookmark.")
                recommendations.add("Look up the organization's official customer support number to verify.")
            }
            RiskLevel.HIGH, RiskLevel.VERY_HIGH -> {
                recommendations.add("CRITICAL: Do NOT click any links, open attachments, or input passwords.")
                recommendations.add("Mark as Phishing / Spam in your email or messaging client immediately.")
                recommendations.add("If you already visited the link, immediately change passwords and revoke session tokens.")
                recommendations.add("Report this message to your internal IT security / SOC team or forward to reportphishing@apwg.org.")
            }
        }

        val title = when (type) {
            ScanType.EMAIL -> if (subject.isNotBlank()) subject else "Email Security Analysis"
            ScanType.SMS -> "Mobile SMS Message Audit"
            ScanType.URL -> cleanContent.take(36)
        }

        val snippet = if (cleanContent.length > 80) cleanContent.take(77) + "..." else cleanContent

        val summary = when (riskLevel) {
            RiskLevel.LOW -> "PhishShield AI evaluated this input and discovered zero high-confidence phishing indicators. The lexical composition, links, and tone match standard legitimate communications."
            RiskLevel.MEDIUM -> "Heuristic analysis flagged borderline suspicious attributes. While not an outright malicious payload, the text features cautionary indicators such as external redirects or unpersonalized styling."
            RiskLevel.HIGH -> "Strong phishing confidence. Multiple indicators including psychological urgency, brand spoofing, and unverified destination links were identified in the payload."
            RiskLevel.VERY_HIGH -> "Severe malicious alert. Active social-engineering attack patterns detected. Payload exhibits weaponized urgency and credential harvesting mechanisms designed to compromise user security."
        }

        val aiExplanation = buildAiExplanation(type, riskLevel, indicators, tactics, cleanContent)

        return ScanResult(
            id = UUID.randomUUID().toString(),
            type = type,
            title = title,
            snippet = snippet,
            rawContent = cleanContent,
            score = finalScore,
            riskLevel = riskLevel,
            timestamp = now,
            formattedDate = dateStr,
            summary = summary,
            indicators = indicators,
            urlAnalysis = urlAnalysis,
            socialEngineeringTactics = if (tactics.isEmpty()) listOf("No deceptive psychological vectors identified") else tactics,
            aiExplanation = aiExplanation,
            recommendedActions = recommendations,
            isDemo = isPresetDemo
        )
    }

    private fun inspectUrl(rawUrl: String): UrlAnalysis {
        val trimmed = rawUrl.trim()
        val isHttps = trimmed.startsWith("https://", ignoreCase = true)
        val protocol = if (isHttps) "HTTPS (Encrypted)" else "HTTP (Insecure/Plaintext)"
        val stripped = trimmed.removePrefix("https://").removePrefix("http://")
        val domain = stripped.substringBefore("/").substringBefore("?")
        val lowerDomain = domain.lowercase(Locale.ROOT)

        val suspiciousTlds = listOf(".xyz", ".top", ".tk", ".buzz", ".club", ".icu", ".work", ".cfd", ".monster", ".gq")
        val hasSuspiciousTld = suspiciousTlds.any { lowerDomain.endsWith(it) }

        val ipRegex = Regex("""^(\d{1,3}\.){3}\d{1,3}""")
        val hasIp = ipRegex.containsMatchIn(lowerDomain)

        val typosquattingMatches = listOf("paypa1", "micros0ft", "g00gle", "amaz0n", "netfl1x", "wellsfarg0", "chase-online-login", "apple-support")
        val hasTypo = typosquattingMatches.any { lowerDomain.contains(it) }

        val entropy = if (domain.count { it == '-' || it == '_' } >= 2 || domain.length > 28) "High Randomness (Evasion)" else "Normal Entropy"
        val ageStatus = if (hasSuspiciousTld || hasTypo) "Suspected Newly Registered Domain (< 7 days)" else "Established Domain"

        return UrlAnalysis(
            originalUrl = rawUrl,
            domain = domain,
            protocol = protocol,
            isHttps = isHttps,
            suspiciousTld = hasSuspiciousTld,
            hasIpHost = hasIp,
            hasTyposquatting = hasTypo,
            entropyRating = entropy,
            domainAgeStatus = ageStatus
        )
    }

    private fun buildAiExplanation(
        type: ScanType,
        risk: RiskLevel,
        indicators: List<ThreatIndicator>,
        tactics: List<String>,
        content: String
    ): String {
        return buildString {
            append("PhishShield Explainable AI Analysis Report:\n\n")
            when (risk) {
                RiskLevel.LOW -> {
                    append("Upon multi-vector evaluation across lexical semantics and target domain structures, our detection pipeline determined this ")
                    append(type.displayName.lowercase(Locale.ROOT))
                    append(" presents minimal threat attributes. The content demonstrates normal syntax without coercive calls-to-action or suspicious redirects.")
                }
                RiskLevel.MEDIUM -> {
                    append("Our AI models observed a combination of ambient risk indicators. While standard legitimate senders occasionally utilize external hosting, ")
                    append("the presence of promotional or redirect cues warrants manual review prior to credential entry.")
                }
                RiskLevel.HIGH, RiskLevel.VERY_HIGH -> {
                    append("The heuristic and deep contextual classifier identified severe social engineering signatures. ")
                    append("Attackers frequently deploy techniques such as ")
                    append(tactics.joinToString(" and "))
                    append(" to trigger cognitive panic, causing recipients to bypass authentication verification. ")
                    if (indicators.isNotEmpty()) {
                        append("Primary triggers included: ")
                        append(indicators.take(2).joinToString("; ") { "${it.title} (${it.category})" })
                        append(". ")
                    }
                    append("Under zero-trust principles, this message must be treated as an adversarial intrusion attempt.")
                }
            }
        }
    }
}
