package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.viewmodel.ScannerViewModel
import com.example.viewmodel.ScanUiState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScanResult
import com.example.model.ScanType
import com.example.repository.ScanRepository
import com.example.repository.ScannerPreset
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberOutlinedButton
import com.example.ui.components.CyberRadarPulseAnimation
import com.example.ui.components.WarningBanner
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ScannerModule(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val scanType: ScanType,
    val testTag: String
) {
    EMAIL(
        title = "Email Scanner",
        subtitle = "Linguistic urgency, spoofed headers & payroll lures",
        icon = Icons.Default.Email,
        scanType = ScanType.EMAIL,
        testTag = "scanner_shell_tab_email"
    ),
    SMS(
        title = "SMS Scanner",
        subtitle = "Smishing traps, fake parcel alerts & OTP banking scams",
        icon = Icons.Default.Message,
        scanType = ScanType.SMS,
        testTag = "scanner_shell_tab_sms"
    ),
    URL(
        title = "URL Scanner",
        subtitle = "Homoglyphs, typosquatting & malicious TLD inspection",
        icon = Icons.Default.Language,
        scanType = ScanType.URL,
        testTag = "scanner_shell_tab_url"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerDashboardShell(
    repository: ScanRepository,
    onScanComplete: (ScanResult) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScannerViewModel? = null,
    onNavigateBack: () -> Unit = {}
) {
    var activeModule by remember { mutableStateOf(ScannerModule.EMAIL) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    // Input States
    var emailSubject by remember { mutableStateOf("") }
    var emailBody by remember { mutableStateOf("") }
    var smsContent by remember { mutableStateOf("") }
    var urlContent by remember { mutableStateOf("") }

    // Validation & State tracking
    var validationError by remember { mutableStateOf<String?>(null) }
    var localIsAnalyzing by remember { mutableStateOf(false) }
    var localScanPhaseText by remember { mutableStateOf("Initializing neural analyzer...") }

    val scanUiState = viewModel?.scanUiState?.collectAsState()?.value ?: ScanUiState.Idle
    val isAnalyzing = if (viewModel != null) scanUiState is ScanUiState.Loading else localIsAnalyzing
    val scanPhaseText = if (viewModel != null) {
        (scanUiState as? ScanUiState.Loading)?.phase ?: "Analyzing payload..."
    } else localScanPhaseText
    val effectiveError = validationError ?: (scanUiState as? ScanUiState.Error)?.message

    fun resetActiveInputs() {
        when (activeModule) {
            ScannerModule.EMAIL -> {
                emailSubject = ""
                emailBody = ""
            }
            ScannerModule.SMS -> smsContent = ""
            ScannerModule.URL -> urlContent = ""
        }
        validationError = null
        viewModel?.resetScanState()
    }

    fun executeAnalysis() {
        val (content, subject, scanType) = when (activeModule) {
            ScannerModule.EMAIL -> Triple(emailBody, emailSubject, ScanType.EMAIL)
            ScannerModule.SMS -> Triple(smsContent, "", ScanType.SMS)
            ScannerModule.URL -> Triple(urlContent, "", ScanType.URL)
        }

        if (viewModel != null) {
            viewModel.initiateScan(
                type = scanType,
                content = content,
                subject = subject,
                isPreset = false,
                onSuccess = onScanComplete
            )
        } else {
            if (content.isBlank()) {
                validationError = "Please input or paste suspicious ${activeModule.scanType.displayName.lowercase()} content."
                return
            }
            validationError = null
            localIsAnalyzing = true

            coroutineScope.launch {
                localScanPhaseText = "Tokenizing lexical patterns and n-grams..."
                delay(280)
                localScanPhaseText = "Calculating social-engineering urgency coefficients..."
                delay(280)
                localScanPhaseText = "Cross-referencing domain entropy & threat signatures..."
                delay(280)
                val result = repository.scan(scanType, content, subject, isPreset = false)
                localIsAnalyzing = false
                onScanComplete(result)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CyberNavySurface,
                drawerContentColor = TextPrimary,
                modifier = Modifier
                    .width(310.dp)
                    .fillMaxHeight()
                    .border(1.dp, CyberCardBorder)
                    .testTag("scanner_navigation_drawer")
            ) {
                // Drawer Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberNavySurfaceVariant)
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(ElectricCyan.copy(alpha = 0.15f), CircleShape)
                                    .border(1.dp, ElectricCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "PhishShield Engine",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Threat Engine v2.4-Neural",
                                    fontSize = 11.sp,
                                    color = ElectricCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "SELECT SCANNER MODULE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )

                // Drawer Navigation Items for Email, SMS, and URL
                ScannerModule.values().forEach { module ->
                    val isSelected = activeModule == module
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = module.icon,
                                contentDescription = module.title,
                                tint = if (isSelected) ElectricCyan else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Column {
                                Text(
                                    text = module.title,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) ElectricCyan else TextPrimary
                                )
                                Text(
                                    text = module.subtitle,
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    lineHeight = 13.sp
                                )
                            }
                        },
                        selected = isSelected,
                        onClick = {
                            activeModule = module
                            validationError = null
                            coroutineScope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = ElectricCyan.copy(alpha = 0.12f),
                            unselectedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .testTag("drawer_item_${module.name.lowercase()}")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = CyberCardBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(6.dp))

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Return to App Overview",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigateBack()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                        .testTag("drawer_item_return_overview")
                )

                Spacer(modifier = Modifier.weight(1f))
                Divider(color = CyberCardBorder, thickness = 0.5.dp)

                // Drawer Telemetry Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(CyberNavySurfaceVariant, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Engine Status", fontSize = 11.sp, color = TextSecondary)
                            Text(text = "100% ONLINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RiskLowGreen, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Zero-link sandbox active. Inspect payloads with complete isolation.",
                            fontSize = 10.sp,
                            color = TextMuted,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    ) {
        // Dedicated Scanner Shell Scaffold
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .testTag("scanner_dashboard_shell"),
            containerColor = CyberNavyDarker,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Security Command Center",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(ElectricCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = activeModule.scanType.badgeText,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricCyan,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Text(
                                text = activeModule.subtitle,
                                fontSize = 11.sp,
                                color = TextMuted,
                                maxLines = 1
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { coroutineScope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("scanner_drawer_toggle_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Scanner Drawer",
                                tint = ElectricCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { resetActiveInputs() },
                            modifier = Modifier.testTag("scanner_shell_reset_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Form",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(
                            onClick = { onNavigateBack() },
                            modifier = Modifier.testTag("scanner_shell_exit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Return to Overview",
                                tint = ElectricCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = CyberNavySurface,
                        titleContentColor = TextPrimary
                    )
                )
            },
            bottomBar = {
                // Dedicated Scanner Bottom Navigation Bar to switch between Email, SMS, and URL
                NavigationBar(
                    containerColor = CyberNavySurface,
                    contentColor = ElectricCyan,
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .border(1.dp, CyberCardBorder.copy(alpha = 0.5f))
                        .testTag("scanner_bottom_navigation_bar")
                ) {
                    ScannerModule.values().forEach { module ->
                        val isSelected = activeModule == module
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                activeModule = module
                                validationError = null
                            },
                            icon = {
                                Icon(
                                    imageVector = module.icon,
                                    contentDescription = module.title,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = when (module) {
                                        ScannerModule.EMAIL -> "Email"
                                        ScannerModule.SMS -> "SMS / Msg"
                                        ScannerModule.URL -> "Web URL"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CyberNavyDarker,
                                selectedTextColor = ElectricCyan,
                                indicatorColor = ElectricCyan,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag(module.testTag)
                        )
                    }
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(CyberNavyDarker)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Warning Banner
                item {
                    WarningBanner()
                }

                // Active Scanner Input Area
                item {
                    AnimatedContent(
                        targetState = activeModule,
                        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(180)) },
                        label = "ScannerFormTransition"
                    ) { targetModule ->
                        CyberCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = ElectricCyan.copy(alpha = 0.45f)
                        ) {
                            when (targetModule) {
                                ScannerModule.EMAIL -> {
                                    EmailInputContent(
                                        subject = emailSubject,
                                        onSubjectChange = { emailSubject = it },
                                        body = emailBody,
                                        onBodyChange = { emailBody = it },
                                        onPaste = {
                                            clipboardManager.getText()?.text?.let { emailBody = it }
                                        }
                                    )
                                }
                                ScannerModule.SMS -> {
                                    SmsInputContent(
                                        content = smsContent,
                                        onContentChange = { smsContent = it },
                                        onPaste = {
                                            clipboardManager.getText()?.text?.let { smsContent = it }
                                        }
                                    )
                                }
                                ScannerModule.URL -> {
                                    UrlInputContent(
                                        url = urlContent,
                                        onUrlChange = { urlContent = it },
                                        onPaste = {
                                            clipboardManager.getText()?.text?.let { urlContent = it }
                                        }
                                    )
                                }
                            }

                            if (effectiveError != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "⚠ $effectiveError",
                                    color = RiskCriticalRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CyberOutlinedButton(
                                    text = "Clear",
                                    onClick = { resetActiveInputs() },
                                    leadingIcon = Icons.Default.Clear,
                                    modifier = Modifier.weight(0.35f),
                                    testTag = "shell_clear_button"
                                )

                                val buttonLabel = when (targetModule) {
                                    ScannerModule.EMAIL -> "Analyze Email"
                                    ScannerModule.SMS -> "Analyze Message"
                                    ScannerModule.URL -> "Analyze URL"
                                }

                                CyberButton(
                                    text = buttonLabel,
                                    onClick = { executeAnalysis() },
                                    enabled = !isAnalyzing,
                                    leadingIcon = Icons.Default.Security,
                                    modifier = Modifier.weight(0.65f),
                                    testTag = "shell_analyze_button"
                                )
                            }
                        }
                    }
                }

                // Lottie-inspired AI Radar Sweep & Pulse Animation HUD
                if (isAnalyzing) {
                    item {
                        CyberRadarPulseAnimation(
                            phaseText = scanPhaseText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }

                // Curated Presets for Current Module
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${activeModule.title.uppercase()} PRESETS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "1-Tap Verification",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val presets = when (activeModule) {
                            ScannerModule.EMAIL -> ScanRepository.EMAIL_PRESETS
                            ScannerModule.SMS -> ScanRepository.SMS_PRESETS
                            ScannerModule.URL -> ScanRepository.URL_PRESETS
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            presets.forEach { preset ->
                                ShellPresetCard(
                                    preset = preset,
                                    onClick = {
                                        when (activeModule) {
                                            ScannerModule.EMAIL -> {
                                                emailSubject = preset.subject
                                                emailBody = preset.content
                                            }
                                            ScannerModule.SMS -> smsContent = preset.content
                                            ScannerModule.URL -> urlContent = preset.content
                                        }
                                        validationError = null
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmailInputContent(
    subject: String,
    onSubjectChange: (String) -> Unit,
    body: String,
    onBodyChange: (String) -> Unit,
    onPaste: () -> Unit
) {
    Column {
        Text(
            text = "Email Subject Line (Optional)",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = subject,
            onValueChange = onSubjectChange,
            placeholder = { Text("e.g. URGENT: Confirm identity or account suspended", color = TextMuted, fontSize = 13.sp) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("shell_email_subject_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = CyberCardBorder,
                focusedContainerColor = CyberNavySurfaceVariant,
                unfocusedContainerColor = CyberNavySurfaceVariant,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Email Payload & Headers *",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            Text(
                text = "Paste from clipboard",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricCyan,
                modifier = Modifier
                    .clickable { onPaste() }
                    .padding(4.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = body,
            onValueChange = onBodyChange,
            placeholder = {
                Text(
                    "Paste email body text, suspicious hyperlinks, or raw RFC headers...",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            },
            minLines = 6,
            maxLines = 10,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("shell_email_body_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = CyberCardBorder,
                focusedContainerColor = CyberNavySurfaceVariant,
                unfocusedContainerColor = CyberNavySurfaceVariant,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(10.dp)
        )
    }
}

@Composable
private fun SmsInputContent(
    content: String,
    onContentChange: (String) -> Unit,
    onPaste: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SMS or WhatsApp Message *",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            Text(
                text = "Paste from clipboard",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricCyan,
                modifier = Modifier
                    .clickable { onPaste() }
                    .padding(4.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            placeholder = {
                Text(
                    "Paste suspicious text message, fake delivery lure, or bank OTP text...",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            },
            minLines = 5,
            maxLines = 8,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("shell_sms_content_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = CyberCardBorder,
                focusedContainerColor = CyberNavySurfaceVariant,
                unfocusedContainerColor = CyberNavySurfaceVariant,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(10.dp)
        )
    }
}

@Composable
private fun UrlInputContent(
    url: String,
    onUrlChange: (String) -> Unit,
    onPaste: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Suspicious Destination URL *",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            Text(
                text = "Paste from clipboard",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricCyan,
                modifier = Modifier
                    .clickable { onPaste() }
                    .padding(4.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            placeholder = { Text("http://paypa1-security-verification.xyz/auth", color = TextMuted, fontSize = 13.sp) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("shell_url_content_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = CyberCardBorder,
                focusedContainerColor = CyberNavySurfaceVariant,
                unfocusedContainerColor = CyberNavySurfaceVariant,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(10.dp)
        )
    }
}

@Composable
private fun ShellPresetCard(
    preset: ScannerPreset,
    onClick: () -> Unit
) {
    val tagColor = when (preset.expectedRisk) {
        "Critical" -> RiskCriticalRed
        "High" -> Color(0xFFF97316)
        else -> RiskLowGreen
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberNavySurface)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = preset.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = if (preset.subject.isNotBlank()) preset.subject else preset.content.take(50) + "...",
                    fontSize = 11.sp,
                    color = TextMuted,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(tagColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                    .border(0.5.dp, tagColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text = preset.expectedRisk,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = tagColor
                )
            }
        }
    }
}
