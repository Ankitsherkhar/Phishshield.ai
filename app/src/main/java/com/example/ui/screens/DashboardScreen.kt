package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RiskLevel
import com.example.model.ScanResult
import com.example.model.ScanType
import com.example.repository.ScanRepository
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.RiskBadge
import com.example.ui.components.ScanTypeBadge
import com.example.ui.components.StatCard
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberNavyDarker
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberNavySurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonSky
import com.example.ui.theme.RiskCriticalRed
import com.example.ui.theme.RiskLowGreen
import com.example.ui.theme.RiskMediumAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    repository: ScanRepository,
    onViewReport: (ScanResult) -> Unit,
    onNavigateToScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scanHistory by repository.scanHistory.collectAsState()
    var selectedFilter by remember { mutableStateOf<ScanType?>(null) } // null = All

    val filteredList = if (selectedFilter == null) {
        scanHistory
    } else {
        scanHistory.filter { it.type == selectedFilter }
    }

    // Metrics calculations
    val totalScans = scanHistory.size
    val safeScans = scanHistory.count { it.riskLevel == RiskLevel.LOW }
    val suspiciousScans = scanHistory.count { it.riskLevel == RiskLevel.HIGH || it.riskLevel == RiskLevel.VERY_HIGH }
    val avgScore = if (totalScans > 0) scanHistory.map { it.score }.average().toInt() else 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberNavyDarker)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SECURITY AUDIT LOGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .background(CyberNavySurfaceVariant, RoundedCornerShape(6.dp))
                            .border(1.dp, CyberCardBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "DEMO & LOCAL DATA",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Threat Intelligence Console",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )

                Text(
                    text = "Real-time audit log of inspected emails, mobile messages, and network URLs.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // Metrics 2x2 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Total Scans",
                        value = "$totalScans",
                        subtitle = "Inspected payloads",
                        icon = Icons.Default.Assessment,
                        iconColor = ElectricCyan,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Safe Items",
                        value = "$safeScans",
                        subtitle = "${if (totalScans > 0) (safeScans * 100 / totalScans) else 0}% clean rate",
                        icon = Icons.Default.CheckCircle,
                        iconColor = RiskLowGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Threats Neutralized",
                        value = "$suspiciousScans",
                        subtitle = "High/Critical alerts",
                        icon = Icons.Default.Warning,
                        iconColor = RiskCriticalRed,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Avg Threat Index",
                        value = "$avgScore/100",
                        subtitle = "Aggregated severity",
                        icon = Icons.Default.Security,
                        iconColor = if (avgScore > 50) RiskMediumAmber else RiskLowGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Filter Pills
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FILTER AUDIT LOGS BY TYPE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )

                    if (scanHistory.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { repository.clearHistory() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("dashboard_clear_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear all scans",
                                tint = RiskCriticalRed.copy(alpha = 0.85f),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Clear All",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RiskCriticalRed.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("All (${scanHistory.size})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricCyan.copy(alpha = 0.2f),
                            selectedLabelColor = ElectricCyan,
                            containerColor = CyberNavySurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (selectedFilter == null) ElectricCyan else CyberCardBorder,
                            enabled = true,
                            selected = selectedFilter == null
                        ),
                        modifier = Modifier.testTag("filter_all")
                    )

                    FilterChip(
                        selected = selectedFilter == ScanType.EMAIL,
                        onClick = { selectedFilter = ScanType.EMAIL },
                        label = { Text("Email", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonSky.copy(alpha = 0.2f),
                            selectedLabelColor = NeonSky,
                            containerColor = CyberNavySurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (selectedFilter == ScanType.EMAIL) NeonSky else CyberCardBorder,
                            enabled = true,
                            selected = selectedFilter == ScanType.EMAIL
                        ),
                        modifier = Modifier.testTag("filter_email")
                    )

                    FilterChip(
                        selected = selectedFilter == ScanType.SMS,
                        onClick = { selectedFilter = ScanType.SMS },
                        label = { Text("SMS", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonPurple.copy(alpha = 0.2f),
                            selectedLabelColor = NeonPurple,
                            containerColor = CyberNavySurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (selectedFilter == ScanType.SMS) NeonPurple else CyberCardBorder,
                            enabled = true,
                            selected = selectedFilter == ScanType.SMS
                        ),
                        modifier = Modifier.testTag("filter_sms")
                    )

                    FilterChip(
                        selected = selectedFilter == ScanType.URL,
                        onClick = { selectedFilter = ScanType.URL },
                        label = { Text("URL", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricCyan.copy(alpha = 0.2f),
                            selectedLabelColor = ElectricCyan,
                            containerColor = CyberNavySurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (selectedFilter == ScanType.URL) ElectricCyan else CyberCardBorder,
                            enabled = true,
                            selected = selectedFilter == ScanType.URL
                        ),
                        modifier = Modifier.testTag("filter_url")
                    )
                }
            }
        }

        // List of Scans
        if (filteredList.isEmpty()) {
            item {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Scans Found",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "No audit records match the current filter. Try scanning an email, SMS message, or URL.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        CyberButton(
                            text = "Launch Scanner",
                            onClick = onNavigateToScan,
                            modifier = Modifier.fillMaxWidth(0.7f),
                            testTag = "dashboard_launch_scanner_button"
                        )
                    }
                }
            }
        } else {
            items(filteredList) { item ->
                ScanHistoryCard(
                    item = item,
                    onViewReport = { onViewReport(item) },
                    onDelete = { repository.deleteScan(item.id) }
                )
            }
        }
    }
}

@Composable
private fun ScanHistoryCard(
    item: ScanResult,
    onViewReport: () -> Unit,
    onDelete: () -> Unit
) {
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewReport() }
            .testTag("history_item_${item.id.take(6)}"),
        borderColor = item.riskLevel.color.copy(alpha = 0.35f),
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ScanTypeBadge(type = item.type)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.formattedDate,
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace
                )
            }

            RiskBadge(riskLevel = item.riskLevel, score = item.score)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.snippet,
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 16.sp,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onViewReport() }
                    .padding(vertical = 4.dp, horizontal = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "View",
                    tint = ElectricCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "View Security Report",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete scan",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
