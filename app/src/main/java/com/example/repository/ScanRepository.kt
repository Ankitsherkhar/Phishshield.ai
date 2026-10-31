package com.example.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.ScanResultDao
import com.example.data.local.toEntity
import com.example.engine.PhishDetectorEngine
import com.example.model.ScanResult
import com.example.model.ScanType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ScannerPreset(
    val title: String,
    val type: ScanType,
    val subject: String = "",
    val content: String,
    val expectedRisk: String
)

/**
 * Repository mediating between the local Room SQLite database and UI ViewModels.
 */
class ScanRepository(
    private val dao: ScanResultDao? = null,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    private val _fallbackHistory = MutableStateFlow<List<ScanResult>>(emptyList())

    val scanHistory: StateFlow<List<ScanResult>> = if (dao != null) {
        dao.getAllScans()
            .map { entities -> entities.map { it.toDomainModel() } }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )
    } else {
        _fallbackHistory.asStateFlow()
    }

    init {
        if (dao != null) {
            coroutineScope.launch {
                if (dao.getCount() == 0) {
                    AppDatabase.seedInitialDemoData(dao)
                }
            }
        } else {
            _fallbackHistory.value = AppDatabase.generateInitialDemoScans()
        }
    }

    fun scan(
        type: ScanType,
        content: String,
        subject: String = "",
        isPreset: Boolean = false
    ): ScanResult {
        val result = PhishDetectorEngine.analyze(type, content, subject, isPreset)
        if (dao != null) {
            coroutineScope.launch {
                dao.insertScan(result.toEntity())
            }
        } else {
            _fallbackHistory.value = listOf(result) + _fallbackHistory.value
        }
        return result
    }

    fun getScanById(id: String): ScanResult? {
        return scanHistory.value.firstOrNull { it.id == id }
    }

    fun deleteScan(id: String): kotlinx.coroutines.Job {
        return if (dao != null) {
            coroutineScope.launch {
                dao.deleteScanById(id)
            }
        } else {
            _fallbackHistory.value = _fallbackHistory.value.filterNot { it.id == id }
            kotlinx.coroutines.CompletableDeferred<Unit>().apply { complete(Unit) }
        }
    }

    fun clearHistory(): kotlinx.coroutines.Job {
        return if (dao != null) {
            coroutineScope.launch {
                dao.clearAll()
            }
        } else {
            _fallbackHistory.value = emptyList()
            kotlinx.coroutines.CompletableDeferred<Unit>().apply { complete(Unit) }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ScanRepository? = null

        fun getInstance(context: Context): ScanRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getDatabase(context.applicationContext)
                val repo = ScanRepository(dao = db.scanResultDao())
                INSTANCE = repo
                repo
            }
        }

        fun createInMemory(): ScanRepository {
            return ScanRepository(dao = null)
        }

        fun getInitialDemoScans(): List<ScanResult> {
            return AppDatabase.generateInitialDemoScans()
        }

        val EMAIL_PRESETS = listOf(
            ScannerPreset(
                title = "PayPal Suspension Lure",
                type = ScanType.EMAIL,
                subject = "URGENT: Your Account Has Been Locked!",
                content = "Dear Valued Customer,\n\nWe have detected unusual activity on your account. To protect your funds, your access has been suspended immediately.\n\nPlease verify your password and billing details within 24 hours at http://paypa1-secure-restore.xyz/login or your account will be permanently deactivated.\n\nThank you,\nPayPal Security Team",
                expectedRisk = "Critical"
            ),
            ScannerPreset(
                title = "HR Payroll Direct Deposit Trap",
                type = ScanType.EMAIL,
                subject = "Action Required: Update Direct Deposit Information",
                content = "Attention Employee,\n\nOur corporate payroll department has updated its automated clearing system. All staff must re-confirm direct deposit bank account numbers and Social Security numbers by 5:00 PM today via the external link below: http://portal-payroll-hr.top/verify\n\nFailure to update will delay this month's salary disbursement.",
                expectedRisk = "High"
            ),
            ScannerPreset(
                title = "Legitimate Project Brief",
                type = ScanType.EMAIL,
                subject = "PhishShield AI Final Presentation Deck",
                content = "Good morning Professor Sharma,\n\nAttached is our final project documentation for the PhishShield AI cyber detection platform. All team members have contributed to the testing phases. We look forward to presenting next Monday.\n\nWarm regards,\nAlex Hunter",
                expectedRisk = "Safe"
            )
        )

        val SMS_PRESETS = listOf(
            ScannerPreset(
                title = "USPS Failed Package Scam",
                type = ScanType.SMS,
                content = "[USPS MSG]: Your package 9400-1123-US cannot be delivered due to an incorrect postal address. Please resolve details and pay the $1.20 redelivery fee within 12 hours: http://usps-redelivery-portal.top/update",
                expectedRisk = "Critical"
            ),
            ScannerPreset(
                title = "Bank Fraud Alert Phishing",
                type = ScanType.SMS,
                content = "CHASE ALERT: A wire transfer of $1,250.00 to CryptoExchange LLC is pending. If you did not authorize this transaction, visit https://chase-fraud-prevention.icu/cancel immediately to secure your funds.",
                expectedRisk = "Critical"
            ),
            ScannerPreset(
                title = "Friendly Personal Message",
                type = ScanType.SMS,
                content = "Hey! Don't forget we have our AI lab demo at 3 PM today in Block 4. I already saved seats for the team.",
                expectedRisk = "Safe"
            )
        )

        val URL_PRESETS = listOf(
            ScannerPreset(
                title = "Deceptive PayPal Typosquat",
                type = ScanType.URL,
                content = "http://paypa1-account-verification-portal.xyz/secure/login",
                expectedRisk = "Critical"
            ),
            ScannerPreset(
                title = "Direct IP Malicious Portal",
                type = ScanType.URL,
                content = "http://185.220.101.5/microsoft365-login/auth.php",
                expectedRisk = "Critical"
            ),
            ScannerPreset(
                title = "Legitimate Cybersecurity Portal",
                type = ScanType.URL,
                content = "https://cve.mitre.org/cve/data_feeds.html",
                expectedRisk = "Safe"
            )
        )
    }
}
