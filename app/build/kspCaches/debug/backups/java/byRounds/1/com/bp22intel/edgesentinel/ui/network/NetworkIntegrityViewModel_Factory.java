package com.bp22intel.edgesentinel.ui.network;

import com.bp22intel.edgesentinel.detection.network.CaptivePortalDetector;
import com.bp22intel.edgesentinel.detection.network.DnsIntegrityChecker;
import com.bp22intel.edgesentinel.detection.network.TlsIntegrityChecker;
import com.bp22intel.edgesentinel.detection.network.VpnMonitor;
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
public final class NetworkIntegrityViewModel_Factory implements Factory<NetworkIntegrityViewModel> {
  private final Provider<VpnMonitor> vpnMonitorProvider;

  private final Provider<DnsIntegrityChecker> dnsCheckerProvider;

  private final Provider<TlsIntegrityChecker> tlsCheckerProvider;

  private final Provider<CaptivePortalDetector> captivePortalDetectorProvider;

  public NetworkIntegrityViewModel_Factory(Provider<VpnMonitor> vpnMonitorProvider,
      Provider<DnsIntegrityChecker> dnsCheckerProvider,
      Provider<TlsIntegrityChecker> tlsCheckerProvider,
      Provider<CaptivePortalDetector> captivePortalDetectorProvider) {
    this.vpnMonitorProvider = vpnMonitorProvider;
    this.dnsCheckerProvider = dnsCheckerProvider;
    this.tlsCheckerProvider = tlsCheckerProvider;
    this.captivePortalDetectorProvider = captivePortalDetectorProvider;
  }

  @Override
  public NetworkIntegrityViewModel get() {
    return newInstance(vpnMonitorProvider.get(), dnsCheckerProvider.get(), tlsCheckerProvider.get(), captivePortalDetectorProvider.get());
  }

  public static NetworkIntegrityViewModel_Factory create(Provider<VpnMonitor> vpnMonitorProvider,
      Provider<DnsIntegrityChecker> dnsCheckerProvider,
      Provider<TlsIntegrityChecker> tlsCheckerProvider,
      Provider<CaptivePortalDetector> captivePortalDetectorProvider) {
    return new NetworkIntegrityViewModel_Factory(vpnMonitorProvider, dnsCheckerProvider, tlsCheckerProvider, captivePortalDetectorProvider);
  }

  public static NetworkIntegrityViewModel newInstance(VpnMonitor vpnMonitor,
      DnsIntegrityChecker dnsChecker, TlsIntegrityChecker tlsChecker,
      CaptivePortalDetector captivePortalDetector) {
    return new NetworkIntegrityViewModel(vpnMonitor, dnsChecker, tlsChecker, captivePortalDetector);
  }
}
