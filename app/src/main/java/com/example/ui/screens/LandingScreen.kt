package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberOutlinedButton
import com.example.ui.components.StatCard
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberNavyDarker
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberNavySurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonSky
import com.example.ui.theme.RiskLowGreen
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class FeatureItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val tint: Color
)

@Composable
fun LandingScreen(
    onNavigateToScanner: () -> Unit,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val features = listOf(
        FeatureItem(
            title = "AI Email Detection",
            description = "Analyzes urgency cues, spoofed executive personas, direct deposit traps, and invoice manipulations.",
            icon = Icons.Default.Email,
            tint = NeonSky
        ),
        FeatureItem(
            title = "SMS Scam Detection",
            description = "Identifies mobile smishing attacks, missed parcel alerts, bank OTP lures, and deceptive SMS shortlinks.",
            icon = Icons.Default.Message,
            tint = Color(0xFFA78BFA)
        ),
        FeatureItem(
            title = "Malicious URL Detection",
            description = "Inspects homoglyphs, typosquatting domains, newly registered TLDs, and naked IP host addresses.",
            icon = Icons.Default.Language,
            tint = ElectricCyan
        ),
        FeatureItem(
            title = "Intelligent Risk Analysis",
            description = "Provides normalized 0–100 threat scoring with clear categorization across low, medium, and critical risks.",
            icon = Icons.Default.Assessment,
            tint = Color(0xFFF59E0B)
        ),
        FeatureItem(
            title = "Explainable AI Reports",
            description = "Demystifies attacks with plain-English rationales outlining why content was flagged and how to respond.",
            icon = Icons.Default.Psychology,
            tint = NeonPurple
        ),
        FeatureItem(
            title = "Scan History & Auditing",
            description = "Maintains a structured session audit log of verified content with zero raw exposure to untrusted links.",
            icon = Icons.Default.History,
            tint = RiskLowGreen
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberNavyDarker)
            .testTag("landing_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Hero Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F1E40),
                                CyberNavyDarker
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Badge
                    Box(
                        modifier = Modifier
                            .background(ElectricCyan.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI-POWERED PHISHING DEFENSE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Stay One Step Ahead of Cyber Threats.",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "PhishShield AI inspects suspicious emails, SMS messages, and deceptive URLs using deep linguistic heuristics and machine-learning risk engines.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Hero Graphic Card
                    CyberCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentPadding = PaddingValues(0.dp),
                        borderColor = ElectricCyan.copy(alpha = 0.35f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.phish_shield_hero),
                            contentDescription = "PhishShield Security Command Center",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // CTAs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CyberButton(
                            text = "Scan Now",
                            onClick = onNavigateToScanner,
                            leadingIcon = Icons.Default.Security,
                            modifier = Modifier.weight(1f),
                            testTag = "hero_scan_now_button"
                        )
                        CyberOutlinedButton(
                            text = "Explore Features",
                            onClick = onNavigateToAbout,
                            leadingIcon = Icons.Default.Bolt,
                            modifier = Modifier.weight(1f),
                            testTag = "hero_explore_features_button"
                        )
                    }
                }
            }
        }

        // Statistics Section (clearly marked demo/illustrative)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TELEMETRY & CAPABILITIES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "• Illustrative Demo Metrics",
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Threats Analyzed",
                        value = "1.4M+",
                        subtitle = "Simulated corpus",
                        icon = Icons.Default.Security,
                        iconColor = ElectricCyan,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "AI Accuracy",
                        value = "99.2%",
                        subtitle = "Evaluation bench",
                        icon = Icons.Default.CheckCircle,
                        iconColor = RiskLowGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Inference Speed",
                        value = "< 0.8s",
                        subtitle = "Average response",
                        icon = Icons.Default.Speed,
                        iconColor = NeonSky,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Blocked Domains",
                        value = "58,400+",
                        subtitle = "Known scam seeds",
                        icon = Icons.Default.BugReport,
                        iconColor = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // How It Works Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "HOW IT WORKS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Three Simple Steps to Safety",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    StepRow(
                        number = "01",
                        title = "Paste Suspicious Content",
                        desc = "Copy any suspicious email text, WhatsApp message, SMS alert, or raw link into the scanner."
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    StepRow(
                        number = "02",
                        title = "AI Analyzes Indicators",
                        desc = "Multi-vector heuristic engines examine urgency triggers, homoglyph typos, sender anomalies, and harvesting intent."
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    StepRow(
                        number = "03",
                        title = "Receive Security Report",
                        desc = "Obtain an instant 0–100 risk score, explainable threat narrative, and actionable safety countermeasures."
                    )
                }
            }
        }

        // Core Features Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "CORE PLATFORM MODULES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Comprehensive Security Suite",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        items(features) { feature ->
            CyberCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 5.dp),
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(feature.tint.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = feature.icon,
                            contentDescription = null,
                            tint = feature.tint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = feature.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = feature.description,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Footer & Project Info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Divider(color = CyberCardBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "PhishShield AI",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan
                )
                Text(
                    text = "Detect. Defend. Stay Safe.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "B.Tech CSE Artificial Intelligence Project · Cybersecurity & Threat Detection",
                    fontSize = 11.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StepRow(number: String, title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(ElectricCyan.copy(alpha = 0.15f), CircleShape)
                .border(1.dp, ElectricCyan.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = ElectricCyan
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
