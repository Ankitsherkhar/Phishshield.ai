package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.RiskLevel
import com.example.model.ScanResult
import com.example.model.ScanType
import com.example.model.UserProfile
import com.example.repository.ScanRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Filter criteria for threat risk severity in audit history.
 */
enum class RiskFilterCriteria(val displayName: String) {
    ALL("All Risks"),
    HIGH_RISK_ONLY("High Risk Only"),
    MEDIUM_RISK("Medium Risk"),
    LOW_RISK("Low Risk")
}

/**
 * Sorting criteria for audit history log entries.
 */
enum class HistorySortOption(val displayName: String) {
    DATE_NEWEST("Date: Newest"),
    DATE_OLDEST("Date: Oldest"),
    SCORE_HIGHEST("Threat: Highest"),
    SCORE_LOWEST("Threat: Lowest")
}

/**
 * ViewModel managing the state of threat scanning operations, including
 * input validation, loading phases, error feedback, history caching, and
 * persistent storage of [ScanResult] items in local Room SQLite database.
 */
class ScannerViewModel(
    application: Application,
    val repository: ScanRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        repository = ScanRepository.getInstance(application)
    )

    constructor(repository: ScanRepository = ScanRepository.createInMemory()) : this(
        application = Application(),
        repository = repository
    )

    private val _scanUiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val scanUiState: StateFlow<ScanUiState> = _scanUiState.asStateFlow()

    private val _currentResult = MutableStateFlow<ScanResult?>(null)
    val currentResult: StateFlow<ScanResult?> = _currentResult.asStateFlow()

    val scanHistory: StateFlow<List<ScanResult>> = repository.scanHistory

    // Filter & Sorting states
    private val _riskFilter = MutableStateFlow(RiskFilterCriteria.ALL)
    val riskFilter: StateFlow<RiskFilterCriteria> = _riskFilter.asStateFlow()

    private val _typeFilter = MutableStateFlow<ScanType?>(null)
    val typeFilter: StateFlow<ScanType?> = _typeFilter.asStateFlow()

    private val _sortOption = MutableStateFlow(HistorySortOption.DATE_NEWEST)
    val sortOption: StateFlow<HistorySortOption> = _sortOption.asStateFlow()

    /**
     * Reactively emits filtered and sorted scan history based on selected risk, type, and sort order.
     */
    val filteredScanHistory: StateFlow<List<ScanResult>> = combine(
        scanHistory,
        _riskFilter,
        _typeFilter,
        _sortOption
    ) { history, risk, type, sort ->
        filterAndSort(history, risk, type, sort)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    /**
     * Initiates a threat analysis scan for the given content payload.
     * Validates input, emits [ScanUiState.Loading] with telemetry phases,
     * stores the result upon success in Room database, and emits [ScanUiState.Error] if validation fails.
     */
    fun initiateScan(
        type: ScanType,
        content: String,
        subject: String = "",
        isPreset: Boolean = false,
        onSuccess: ((ScanResult) -> Unit)? = null
    ) {
        val trimmed = content.trim()
        if (trimmed.isBlank()) {
            _scanUiState.value = ScanUiState.Error("Please enter or paste ${type.displayName.lowercase()} content to analyze.")
            return
        }

        viewModelScope.launch {
            _scanUiState.value = ScanUiState.Loading("Tokenizing lexical patterns and n-grams...")
            delay(280)
            _scanUiState.value = ScanUiState.Loading("Calculating social-engineering urgency coefficients...")
            delay(280)
            _scanUiState.value = ScanUiState.Loading("Cross-referencing domain entropy & threat signatures...")
            delay(280)

            try {
                val result = repository.scan(type, trimmed, subject, isPreset)
                _currentResult.value = result
                _scanUiState.value = ScanUiState.Success(result)
                onSuccess?.invoke(result)
            } catch (e: Exception) {
                _scanUiState.value = ScanUiState.Error(
                    e.message ?: "An unexpected error occurred during threat analysis."
                )
            }
        }
    }

    /**
     * Executes immediate threat analysis, updates state and Room database, and invokes callback.
     */
    fun performScan(
        type: ScanType,
        content: String,
        subject: String = "",
        isPreset: Boolean = false,
        onComplete: (ScanResult) -> Unit
    ) {
        val trimmed = content.trim()
        if (trimmed.isBlank()) {
            _scanUiState.value = ScanUiState.Error("Please enter or paste ${type.displayName.lowercase()} content to analyze.")
            return
        }
        val result = repository.scan(type, trimmed, subject, isPreset)
        _currentResult.value = result
        _scanUiState.value = ScanUiState.Success(result)
        onComplete(result)
    }

    /**
     * Resets the scan UI state back to [ScanUiState.Idle].
     */
    fun resetScanState() {
        _scanUiState.value = ScanUiState.Idle
    }

    /**
     * Clears error state if present.
     */
    fun clearError() {
        if (_scanUiState.value is ScanUiState.Error) {
            _scanUiState.value = ScanUiState.Idle
        }
    }

    /**
     * Retrieves a scan report by its unique ID.
     */
    fun getScanById(id: String): ScanResult? {
        return repository.getScanById(id) ?: _currentResult.value
    }

    /**
     * Deletes a scan record from Room repository history.
     */
    fun deleteScan(id: String) {
        repository.deleteScan(id)
        if (_currentResult.value?.id == id) {
            _currentResult.value = null
        }
    }

    /**
     * Clears all scan history from the local Room database.
     */
    fun clearHistory() {
        repository.clearHistory()
        _currentResult.value = null
    }

    /**
     * Updates the threat risk filter criteria (e.g. HIGH_RISK_ONLY).
     */
    fun setRiskFilter(criteria: RiskFilterCriteria) {
        _riskFilter.value = criteria
    }

    /**
     * Updates the communication channel type filter (e.g. Email, SMS, URL).
     */
    fun setTypeFilter(type: ScanType?) {
        _typeFilter.value = type
    }

    /**
     * Sets the sort ordering for audit logs.
     */
    fun setSortOption(sort: HistorySortOption) {
        _sortOption.value = sort
    }

    /**
     * Toggles date sorting between newest first and oldest first.
     */
    fun toggleDateSort() {
        _sortOption.value = if (_sortOption.value == HistorySortOption.DATE_NEWEST) {
            HistorySortOption.DATE_OLDEST
        } else {
            HistorySortOption.DATE_NEWEST
        }
    }

    /**
     * Resets all filters and restores default newest-first ordering.
     */
    fun resetFilters() {
        _riskFilter.value = RiskFilterCriteria.ALL
        _typeFilter.value = null
        _sortOption.value = HistorySortOption.DATE_NEWEST
    }

    /**
     * Updates the active user security profile.
     */
    fun updateProfile(newProfile: UserProfile) {
        _userProfile.value = newProfile
    }

    companion object {
        /**
         * Pure function to filter and sort security scan audit entries.
         */
        fun filterAndSort(
            history: List<ScanResult>,
            riskFilter: RiskFilterCriteria = RiskFilterCriteria.ALL,
            typeFilter: ScanType? = null,
            sortOption: HistorySortOption = HistorySortOption.DATE_NEWEST
        ): List<ScanResult> {
            var items = history

            // 1. Threat severity filtering
            items = when (riskFilter) {
                RiskFilterCriteria.ALL -> items
                RiskFilterCriteria.HIGH_RISK_ONLY -> items.filter {
                    it.riskLevel == RiskLevel.HIGH || it.riskLevel == RiskLevel.VERY_HIGH || it.score >= 60
                }
                RiskFilterCriteria.MEDIUM_RISK -> items.filter {
                    it.riskLevel == RiskLevel.MEDIUM
                }
                RiskFilterCriteria.LOW_RISK -> items.filter {
                    it.riskLevel == RiskLevel.LOW
                }
            }

            // 2. Scan channel type filtering
            if (typeFilter != null) {
                items = items.filter { it.type == typeFilter }
            }

            // 3. Sorting
            items = when (sortOption) {
                HistorySortOption.DATE_NEWEST -> items.sortedByDescending { it.timestamp }
                HistorySortOption.DATE_OLDEST -> items.sortedBy { it.timestamp }
                HistorySortOption.SCORE_HIGHEST -> items.sortedByDescending { it.score }
                HistorySortOption.SCORE_LOWEST -> items.sortedBy { it.score }
            }

            return items
        }
    }
}
