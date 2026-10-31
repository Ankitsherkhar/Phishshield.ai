package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.PhishDetectorEngine
import com.example.model.RiskLevel
import com.example.model.ScanType
import com.example.ui.navigation.NavDestination
import com.example.viewmodel.ScannerViewModel
import com.example.viewmodel.ScanUiState
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PhishShield AI", appName)
  }

  @Test
  fun `navigation destinations routes are properly defined`() {
    assertEquals("home", NavDestination.Home.route)
    assertEquals("scanner", NavDestination.Scanner.route)
    assertEquals("dashboard", NavDestination.Dashboard.route)
    assertEquals("result/123", NavDestination.Result.createRoute("123"))
    assertEquals(5, NavDestination.bottomNavItems.size)
  }

  @Test
  fun `scanner viewmodel initializes with demo history and scans properly`() {
    val viewModel = ScannerViewModel()
    assertTrue(viewModel.scanHistory.value.isNotEmpty())

    var scannedResultId: String? = null
    viewModel.performScan(
      type = ScanType.URL,
      content = "http://paypa1-security-verification.xyz/login"
    ) { result ->
      scannedResultId = result.id
    }

    assertNotNull(scannedResultId)
    val fetched = viewModel.getScanById(scannedResultId!!)
    assertNotNull(fetched)
    assertEquals(RiskLevel.VERY_HIGH, fetched?.riskLevel)
  }

  @Test
  fun `phishing engine flags malicious urgency email`() {
    val result = PhishDetectorEngine.analyze(
      type = ScanType.EMAIL,
      subject = "URGENT: Verify PayPal Password Within 24 Hours",
      content = "Your account is suspended. Confirm identity and password immediately at http://paypa1-secure.xyz/login"
    )
    assertTrue("Score should be elevated for phishing email", result.score > 60)
    assertTrue("Should be high or very high risk", result.riskLevel == RiskLevel.HIGH || result.riskLevel == RiskLevel.VERY_HIGH)
    assertTrue("Indicators should not be empty", result.indicators.isNotEmpty())
  }

  @Test
  fun `phishing engine evaluates clean benign message as low risk`() {
    val result = PhishDetectorEngine.analyze(
      type = ScanType.SMS,
      content = "Hey Alex! Meeting for lunch at 12:30 at the campus cafeteria."
    )
    assertEquals(RiskLevel.LOW, result.riskLevel)
    assertTrue(result.score < 25)
  }

  @Test
  fun `phishing engine detects homoglyph and suspicious TLD`() {
    val result = PhishDetectorEngine.analyze(
      type = ScanType.URL,
      content = "http://paypa1-security-verification.xyz/login"
    )
    assertTrue("Score should be high for homoglyph URL", result.score >= 60)
    assertEquals(true, result.urlAnalysis?.suspiciousTld)
    assertEquals(true, result.urlAnalysis?.hasTyposquatting)
  }

  @Test
  fun `scan result data class provides structured report fields and threat flags`() {
    val report = com.example.model.ScanResult(
      id = "test-scan-001",
      type = ScanType.EMAIL,
      title = "PayPal Security Lure",
      snippet = "Your account is suspended...",
      rawContent = "Full email body",
      score = 92,
      riskLevel = RiskLevel.VERY_HIGH,
      timestamp = System.currentTimeMillis(),
      formattedDate = "Oct 31, 2026 · 10:45",
      summary = "High-confidence spear-phishing attack detected.",
      indicators = listOf(
        com.example.model.ThreatIndicator(
          title = "High Psychological Urgency",
          description = "Urgency pressure tactics",
          severity = RiskLevel.HIGH,
          category = "Linguistic Pattern"
        )
      ),
      aiExplanation = "The email exhibits credential harvesting intent.",
      recommendedActions = listOf("Do not click links"),
      isDemo = false
    )

    assertEquals("test-scan-001", report.id)
    assertEquals(92, report.score)
    assertEquals(RiskLevel.VERY_HIGH, report.riskLevel)
    assertTrue(report.isCriticalThreat)
    assertEquals(false, report.isBenign)
    assertEquals(1, report.indicators.size)
    assertEquals("The email exhibits credential harvesting intent.", report.aiExplanation)
  }

  @Test
  fun `scanner viewmodel manages idle, loading, error, and storage states`() {
    val viewModel = ScannerViewModel()
    assertEquals(ScanUiState.Idle, viewModel.scanUiState.value)

    // Validation failure emits Error state
    viewModel.initiateScan(ScanType.EMAIL, "   ")
    assertTrue(viewModel.scanUiState.value is ScanUiState.Error)
    val errorState = viewModel.scanUiState.value as ScanUiState.Error
    assertTrue(errorState.message.contains("content"))

    // Reset state restores Idle
    viewModel.resetScanState()
    assertEquals(ScanUiState.Idle, viewModel.scanUiState.value)

    // Valid scan performs analysis and stores result
    viewModel.performScan(
      type = ScanType.SMS,
      content = "USPS alert: Parcel delivery failed, claim at http://usps-parcel.top/update"
    ) { result ->
      assertNotNull(result)
      assertEquals(result.id, viewModel.currentResult.value?.id)
    }
  }

  @Test
  fun `room database stores and retrieves scan results accurately`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = com.example.data.local.AppDatabase.createInMemoryDatabase(context)
    val dao = db.scanResultDao()

    assertEquals(0, dao.getCount())

    val testScan = com.example.engine.PhishDetectorEngine.analyze(
      type = ScanType.EMAIL,
      subject = "Urgent Bank Notice",
      content = "Your bank account has been suspended. Log in at http://fakebank-verify.xyz/auth"
    )

    dao.insertScan(com.example.data.local.ScanResultEntity.fromDomainModel(testScan))
    assertEquals(1, dao.getCount())

    val loadedEntity = dao.getScanById(testScan.id)
    assertNotNull(loadedEntity)
    val restoredDomain = loadedEntity!!.toDomainModel()

    assertEquals(testScan.id, restoredDomain.id)
    assertEquals(testScan.type, restoredDomain.type)
    assertEquals(testScan.score, restoredDomain.score)
    assertEquals(testScan.riskLevel, restoredDomain.riskLevel)
    assertEquals(testScan.indicators.size, restoredDomain.indicators.size)
    assertEquals(testScan.aiExplanation, restoredDomain.aiExplanation)

    // Test deletion
    dao.deleteScanById(testScan.id)
    assertEquals(0, dao.getCount())

    db.close()
  }

  @Test
  fun `room database seeds demo scans and supports repository flow`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = com.example.data.local.AppDatabase.createInMemoryDatabase(context)
    val dao = db.scanResultDao()

    com.example.data.local.AppDatabase.seedInitialDemoData(dao)
    val initialCount = dao.getCount()
    assertTrue("Initial seed should insert demo scans", initialCount >= 5)

    // Insert new scan
    val newScan = com.example.engine.PhishDetectorEngine.analyze(
      type = ScanType.URL,
      content = "http://suspicious-test-link.xyz/login"
    )
    dao.insertScan(com.example.data.local.ScanResultEntity.fromDomainModel(newScan))
    assertEquals(initialCount + 1, dao.getCount())

    val loaded = dao.getScanById(newScan.id)
    assertNotNull(loaded)
    assertEquals(newScan.id, loaded?.id)

    // Clear history
    dao.clearAll()
    assertEquals(0, dao.getCount())

    db.close()
  }
}

