package com.example.viewmodel

import com.example.model.ScanResult

/**
 * Represents the distinct UI states for threat scanning operations.
 */
sealed class ScanUiState {
    /**
     * Default idle state: scanner is awaiting user input.
     */
    object Idle : ScanUiState()

    /**
     * Active scanning state with live telemetry progress message.
     * @property phase Description of the current analysis phase.
     */
    data class Loading(
        val phase: String = "Initializing neural threat analyzer..."
    ) : ScanUiState()

    /**
     * Successful scan state storing the computed [ScanResult].
     * @property result The complete threat analysis report.
     */
    data class Success(
        val result: ScanResult
    ) : ScanUiState()

    /**
     * Error state encountered during validation or scanning.
     * @property message User-facing error explanation.
     */
    data class Error(
        val message: String
    ) : ScanUiState()
}
