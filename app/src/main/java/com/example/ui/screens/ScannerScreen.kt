package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.model.ScanResult
import com.example.repository.ScanRepository
import com.example.viewmodel.ScannerViewModel

@Composable
fun ScannerScreen(
    repository: ScanRepository,
    onScanComplete: (ScanResult) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScannerViewModel? = null,
    onNavigateBack: () -> Unit = {}
) {
    ScannerDashboardShell(
        repository = repository,
        onScanComplete = onScanComplete,
        modifier = modifier,
        viewModel = viewModel,
        onNavigateBack = onNavigateBack
    )
}
