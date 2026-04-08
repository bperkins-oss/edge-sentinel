package com.bp22intel.edgesentinel.ui.wifi;

import com.bp22intel.edgesentinel.detection.wifi.WifiEnvironmentAnalyzer;
import com.bp22intel.edgesentinel.detection.wifi.WifiMonitor;
import com.bp22intel.edgesentinel.detection.wifi.WifiProbeProtector;
import com.bp22intel.edgesentinel.detection.wifi.WifiThreatDetector;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class WifiViewModel_Factory implements Factory<WifiViewModel> {
  private final Provider<WifiMonitor> wifiMonitorProvider;

  private final Provider<WifiThreatDetector> threatDetectorProvider;

  private final Provider<WifiEnvironmentAnalyzer> environmentAnalyzerProvider;

  private final Provider<WifiProbeProtector> probeProtectorProvider;

  public WifiViewModel_Factory(Provider<WifiMonitor> wifiMonitorProvider,
      Provider<WifiThreatDetector> threatDetectorProvider,
      Provider<WifiEnvironmentAnalyzer> environmentAnalyzerProvider,
      Provider<WifiProbeProtector> probeProtectorProvider) {
    this.wifiMonitorProvider = wifiMonitorProvider;
    this.threatDetectorProvider = threatDetectorProvider;
    this.environmentAnalyzerProvider = environmentAnalyzerProvider;
    this.probeProtectorProvider = probeProtectorProvider;
  }

  @Override
  public WifiViewModel get() {
    return newInstance(wifiMonitorProvider.get(), threatDetectorProvider.get(), environmentAnalyzerProvider.get(), probeProtectorProvider.get());
  }

  public static WifiViewModel_Factory create(Provider<WifiMonitor> wifiMonitorProvider,
      Provider<WifiThreatDetector> threatDetectorProvider,
      Provider<WifiEnvironmentAnalyzer> environmentAnalyzerProvider,
      Provider<WifiProbeProtector> probeProtectorProvider) {
    return new WifiViewModel_Factory(wifiMonitorProvider, threatDetectorProvider, environmentAnalyzerProvider, probeProtectorProvider);
  }

  public static WifiViewModel newInstance(WifiMonitor wifiMonitor,
      WifiThreatDetector threatDetector, WifiEnvironmentAnalyzer environmentAnalyzer,
      WifiProbeProtector probeProtector) {
    return new WifiViewModel(wifiMonitor, threatDetector, environmentAnalyzer, probeProtector);
  }
}
