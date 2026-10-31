package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberCard
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberNavyDarker
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberNavySurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonSky
import com.example.ui.theme.RiskLowGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberNavyDarker)
            .testTag("about_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "PLATFORM OVERVIEW & GOVERNANCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "About PhishShield AI",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Tagline: Detect. Defend. Stay Safe.",
                    fontSize = 13.sp,
                    color = ElectricCyan,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Mission Statement
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ElectricCyan.copy(alpha = 0.4f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "The Cybersecurity Mission",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "PhishShield AI is an intelligent threat detection platform engineered to identify social engineering attacks, credential harvesting emails, deceptive SMS messages (smishing), and malicious links before they compromise user safety.\n\nBuilt as a capstone B.Tech CSE Artificial Intelligence project, it bridges machine learning heuristic models with clean, actionable threat intelligence.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        // Why Detection Matters
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Why Phishing Defense Matters",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Over 3.4 billion phishing messages are dispatched globally every day. More than 90% of all corporate data breaches begin with a spear-phishing or social engineering lure. Attackers exploit psychological triggers—artificial urgency, fear of account suspension, and brand trust—to manipulate recipients into surrendering credentials.\n\nPhishShield AI provides an impartial, automated safety net that inspects raw payloads without human cognitive biases.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        // High Level Detection Architecture
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonPurple.copy(alpha = 0.4f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = NeonPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Detection Pipeline & Mechanics",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                ArchitectureStep(
                    step = "01. Tokenization & Lexical Analysis",
                    desc = "Extracts urgency n-grams, credential solicitation verbs, and financial redirection vectors."
                )
                ArchitectureStep(
                    step = "02. URL & DNS Feature Extraction",
                    desc = "Evaluates Shannon entropy, character substitution (homoglyphs), TLD disposable risk, and naked IP host routing."
                )
                ArchitectureStep(
                    step = "03. Social Engineering Pattern Engine",
                    desc = "Flags behavioral exploitation models including Authority Impersonation, Scarcity, and Fear Induction."
                )
                ArchitectureStep(
                    step = "04. Explainable AI (XAI) Synthesis",
                    desc = "Translates raw model weights into understandable rationale with concrete security countermeasures."
                )
            }
        }

        // Privacy & Zero-Visit Commitment
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = RiskLowGreen.copy(alpha = 0.4f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = RiskLowGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Zero-Link-Visit Privacy Commitment",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Zero-Visit Architecture: PhishShield AI strictly parses URL lexical structures and reputations without loading web pages in user sandboxes, eliminating drive-by download risks.\n\n• Ephemeral Content Handling: Submitted text is analyzed in memory. Credentials and PII are never permanently cached or indexed.\n\n• Local Storage: Scan audit logs remain stored locally in private application sandbox storage.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        // Future Backend Integration Specs
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = NeonSky,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FastAPI & Supabase Integration Blueprint",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "The client application interface is designed with clean REST service endpoints prepared for connection to:",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberNavySurfaceVariant, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "POST /api/v1/scan/email -> EmailRiskResponse\nPOST /api/v1/scan/sms   -> SmsRiskResponse\nPOST /api/v1/scan/url   -> UrlRiskResponse\nPOST /api/v1/auth/token -> JWT Bearer Auth",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = ElectricCyan,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Project Credits & Contact
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Academic & Contact Information",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Project: PhishShield AI\nCourse: B.Tech Computer Science & Engineering (Artificial Intelligence)\nDomain: Applied Machine Learning in Cyber Threat Intelligence\nEmail: contact@phishshield.ai\nGitHub: github.com/phishshield-ai/core-engine",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ArchitectureStep(step: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = step,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ElectricCyan,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = desc,
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}
