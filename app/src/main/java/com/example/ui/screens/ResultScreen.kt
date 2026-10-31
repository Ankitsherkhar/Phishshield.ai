package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RiskLevel
import com.example.model.ScanResult
import com.example.model.UrlAnalysis
import com.example.ui.components.CircularRiskGauge
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberOutlinedButton
import com.example.ui.components.RiskBadge
import com.example.ui.components.ScanTypeBadge
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberNavyDarker
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberNavySurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonSky
import com.example.ui.theme.RiskCriticalRed
import com.example.ui.theme.RiskLowGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ResultScreen(
    result: ScanResult,
    onScanAgain: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberNavyDarker)
            .testTag("scan_result_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Nav / Back header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .background(CyberNavySurface, CircleShape)
                            .testTag("result_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SECURITY AUDIT REPORT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = result.formattedDate,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                ScanTypeBadge(type = result.type)
            }
        }

        // Demo vs Real Tag
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (result.isDemo) CyberNavySurfaceVariant else Color(0xFF0C2442),
                        RoundedCornerShape(8.dp)
                    )
                    .border(1.dp, if (result.isDemo) CyberCardBorder else ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (result.isDemo) "SAMPLE DEMO EVALUATION" else "ACTIVE HEURISTIC SESSION RESULT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (result.isDemo) TextMuted else ElectricCyan,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "ID: ${result.id.take(8)}",
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Primary Score Gauge Card
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = result.riskLevel.color.copy(alpha = 0.5f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Threat Risk Assessment",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    CircularRiskGauge(
                        score = result.score,
                        riskLevel = result.riskLevel
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = result.riskLevel.description,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }

        // Detection Summary Card
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Detection Summary",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.summary,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                if (result.snippet.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberNavySurfaceVariant)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "\"${result.snippet}\"",
                            fontSize = 12.sp,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Suspicious Indicators Detected
        if (result.indicators.isNotEmpty()) {
            item {
                Column {
                    Text(
                        text = "SUSPICIOUS INDICATORS DETECTED (${result.indicators.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(result.indicators) { indicator ->
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = indicator.severity.color.copy(alpha = 0.35f),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(indicator.severity.color.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (indicator.severity == RiskLevel.VERY_HIGH) Icons.Default.Dangerous else Icons.Default.Warning,
                                contentDescription = null,
                                tint = indicator.severity.color,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = indicator.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Box(
                                    modifier = Modifier
                                        .background(CyberNavySurfaceVariant, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = indicator.category,
                                        fontSize = 9.sp,
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = indicator.description,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // URL / Domain Technical Analysis (if applicable)
        if (result.urlAnalysis != null) {
            item {
                UrlAnalysisCard(analysis = result.urlAnalysis)
            }
        }

        // Social Engineering Cues
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = NeonPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Social Engineering Vectors",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    result.socialEngineeringTactics.forEach { tactic ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(NeonPurple, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = tactic,
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Explainable AI Report
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ElectricCyan.copy(alpha = 0.4f),
                backgroundColor = Color(0xFF0B1736)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Explainable AI Reasoning (XAI)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = result.aiExplanation,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 19.sp
                )
            }
        }

        // Recommended Safety Actions
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = RiskLowGreen.copy(alpha = 0.35f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = RiskLowGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recommended Safety Countermeasures",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    result.recommendedActions.forEach { action ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = RiskLowGreen,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = action,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }

        // Bottom Action Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CyberButton(
                    text = "Scan Another Item",
                    onClick = onScanAgain,
                    leadingIcon = Icons.Default.Replay,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "scan_again_button"
                )

                CyberOutlinedButton(
                    text = "Copy Audit Summary",
                    onClick = {
                        val textToCopy = "PhishShield AI Report\nScore: ${result.score}/100 (${result.riskLevel.displayName})\nType: ${result.type.displayName}\nSummary: ${result.summary}"
                        clipboardManager.setText(AnnotatedString(textToCopy))
                        Toast.makeText(context, "Report copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    leadingIcon = Icons.Default.ContentCopy,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "copy_report_button"
                )
            }
        }
    }
}

@Composable
private fun UrlAnalysisCard(analysis: UrlAnalysis) {
    CyberCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = NeonSky.copy(alpha = 0.4f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Dns,
                contentDescription = null,
                tint = NeonSky,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "URL & DNS Infrastructure Telemetry",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        UrlRow(label = "Target Domain", value = analysis.domain)
        UrlRow(label = "Protocol", value = analysis.protocol)
        UrlRow(
            label = "High-Risk TLD (.xyz, .top, etc.)",
            value = if (analysis.suspiciousTld) "DETECTED (Flagged)" else "Normal TLD",
            highlight = analysis.suspiciousTld
        )
        UrlRow(
            label = "Typosquatting / Brand Mimicry",
            value = if (analysis.hasTyposquatting) "SUSPECTED HOMOGLYPH" else "Clean",
            highlight = analysis.hasTyposquatting
        )
        UrlRow(
            label = "Direct IP Address Host",
            value = if (analysis.hasIpHost) "RAW IP DETECTED" else "Standard DNS Host",
            highlight = analysis.hasIpHost
        )
        UrlRow(label = "Entropy Rating", value = analysis.entropyRating)
        UrlRow(label = "Domain Age Telemetry", value = analysis.domainAgeStatus)
    }
}

@Composable
private fun UrlRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlight) RiskCriticalRed else TextPrimary,
            fontFamily = FontFamily.Monospace
        )
    }
    Divider(color = CyberCardBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
}
