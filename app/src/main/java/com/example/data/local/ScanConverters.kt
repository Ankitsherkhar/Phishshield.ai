package com.example.data.local

import androidx.room.TypeConverter
import com.example.model.RiskLevel
import com.example.model.ScanType

/**
 * Room TypeConverters for enums used in security audit entities.
 */
class ScanConverters {

    @TypeConverter
    fun fromScanType(type: ScanType?): String? = type?.name

    @TypeConverter
    fun toScanType(value: String?): ScanType? = value?.let {
        try {
            ScanType.valueOf(it)
        } catch (_: Exception) {
            ScanType.EMAIL
        }
    }

    @TypeConverter
    fun fromRiskLevel(level: RiskLevel?): String? = level?.name

    @TypeConverter
    fun toRiskLevel(value: String?): RiskLevel? = value?.let {
        try {
            RiskLevel.valueOf(it)
        } catch (_: Exception) {
            RiskLevel.MEDIUM
        }
    }
}
