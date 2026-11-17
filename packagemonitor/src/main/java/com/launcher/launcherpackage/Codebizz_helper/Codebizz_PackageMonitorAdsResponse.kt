package com.launcher.launcherpackage.Codebizz_helper

import androidx.annotation.Keep

@Keep
data class Codebizz_PackageMonitorAdsResponse(
    val PackageMonitor_Native: String = "",
    val PackageMonitor_Banner_Inline: String = "",
    val PackageMonitor_Rewarded_Video: String = "",
    val PackageMonitor_Native_All_Click2Change: Boolean = false,
    val PackageMonitor_Native_All_Fail_Array: List<Codebizz_PackageMonitor_WebAdItem> = emptyList(),
)


