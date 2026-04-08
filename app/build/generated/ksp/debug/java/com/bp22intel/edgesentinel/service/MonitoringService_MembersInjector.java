package com.bp22intel.edgesentinel.service;

import com.bp22intel.edgesentinel.data.local.dao.TrustedNetworkDao;
import com.bp22intel.edgesentinel.data.sensor.CellInfoCollector;
import com.bp22intel.edgesentinel.data.sensor.TelephonyMonitor;
import com.bp22intel.edgesentinel.detection.bluetooth.BleAlertManager;
import com.bp22intel.edgesentinel.detection.bluetooth.BleTrackingDetector;
import com.bp22intel.edgesentinel.detection.engine.ThreatDetectionEngine;
import com.bp22intel.edgesentinel.detection.network.CaptivePortalDetector;
import com.bp22intel.edgesentinel.detection.network.DnsIntegrityChecker;
import com.bp22intel.edgesentinel.detection.network.TlsIntegrityChecker;
import com.bp22intel.edgesentinel.detection.network.VpnMonitor;
import com.bp22intel.edgesentinel.detection.wifi.WifiMonitor;
import com.bp22intel.edgesentinel.detection.wifi.WifiThreatDetector;
import com.bp22intel.edgesentinel.domain.repository.AlertRepository;
import com.bp22intel.edgesentinel.domain.repository.CellRepository;
import com.bp22intel.edgesentinel.domain.repository.ScanRepository;
import com.bp22intel.edgesentinel.fusion.SensorFusionEngine;
import com.bp22intel.edgesentinel.sensor.MotionDetector;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MonitoringService_MembersInjector implements MembersInjector<MonitoringService> {
  private final Provider<CellInfoCollector> cellInfoCollectorProvider;

  private final Provider<TelephonyMonitor> telephonyMonitorProvider;

  private final Provider<ThreatDetectionEngine> threatDetectionEngineProvider;

  private final Provider<CellRepository> cellRepositoryProvider;

  private final Provider<AlertRepository> alertRepositoryProvider;

  private final Provider<ScanRepository> scanRepositoryProvider;

  private final Provider<SensorFusionEngine> sensorFusionEngineProvider;

  private final Provider<MotionDetector> motionDetectorProvider;

  private final Provider<WifiMonitor> wifiMonitorProvider;

  private final Provider<WifiThreatDetector> wifiThreatDetectorProvider;

  private final Provider<TrustedNetworkDao> trustedNetworkDaoProvider;

  private final Provider<BleTrackingDetector> bleTrackingDetectorProvider;

  private final Provider<BleAlertManager> bleAlertManagerProvider;

  private final Provider<DnsIntegrityChecker> dnsIntegrityCheckerProvider;

  private final Provider<TlsIntegrityChecker> tlsIntegrityCheckerProvider;

  private final Provider<CaptivePortalDetector> captivePortalDetectorProvider;

  private final Provider<VpnMonitor> vpnMonitorProvider;

  public MonitoringService_MembersInjector(Provider<CellInfoCollector> cellInfoCollectorProvider,
      Provider<TelephonyMonitor> telephonyMonitorProvider,
      Provider<ThreatDetectionEngine> threatDetectionEngineProvider,
      Provider<CellRepository> cellRepositoryProvider,
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider,
      Provider<SensorFusionEngine> sensorFusionEngineProvider,
      Provider<MotionDetector> motionDetectorProvider, Provider<WifiMonitor> wifiMonitorProvider,
      Provider<WifiThreatDetector> wifiThreatDetectorProvider,
      Provider<TrustedNetworkDao> trustedNetworkDaoProvider,
      Provider<BleTrackingDetector> bleTrackingDetectorProvider,
      Provider<BleAlertManager> bleAlertManagerProvider,
      Provider<DnsIntegrityChecker> dnsIntegrityCheckerProvider,
      Provider<TlsIntegrityChecker> tlsIntegrityCheckerProvider,
      Provider<CaptivePortalDetector> captivePortalDetectorProvider,
      Provider<VpnMonitor> vpnMonitorProvider) {
    this.cellInfoCollectorProvider = cellInfoCollectorProvider;
    this.telephonyMonitorProvider = telephonyMonitorProvider;
    this.threatDetectionEngineProvider = threatDetectionEngineProvider;
    this.cellRepositoryProvider = cellRepositoryProvider;
    this.alertRepositoryProvider = alertRepositoryProvider;
    this.scanRepositoryProvider = scanRepositoryProvider;
    this.sensorFusionEngineProvider = sensorFusionEngineProvider;
    this.motionDetectorProvider = motionDetectorProvider;
    this.wifiMonitorProvider = wifiMonitorProvider;
    this.wifiThreatDetectorProvider = wifiThreatDetectorProvider;
    this.trustedNetworkDaoProvider = trustedNetworkDaoProvider;
    this.bleTrackingDetectorProvider = bleTrackingDetectorProvider;
    this.bleAlertManagerProvider = bleAlertManagerProvider;
    this.dnsIntegrityCheckerProvider = dnsIntegrityCheckerProvider;
    this.tlsIntegrityCheckerProvider = tlsIntegrityCheckerProvider;
    this.captivePortalDetectorProvider = captivePortalDetectorProvider;
    this.vpnMonitorProvider = vpnMonitorProvider;
  }

  public static MembersInjector<MonitoringService> create(
      Provider<CellInfoCollector> cellInfoCollectorProvider,
      Provider<TelephonyMonitor> telephonyMonitorProvider,
      Provider<ThreatDetectionEngine> threatDetectionEngineProvider,
      Provider<CellRepository> cellRepositoryProvider,
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider,
      Provider<SensorFusionEngine> sensorFusionEngineProvider,
      Provider<MotionDetector> motionDetectorProvider, Provider<WifiMonitor> wifiMonitorProvider,
      Provider<WifiThreatDetector> wifiThreatDetectorProvider,
      Provider<TrustedNetworkDao> trustedNetworkDaoProvider,
      Provider<BleTrackingDetector> bleTrackingDetectorProvider,
      Provider<BleAlertManager> bleAlertManagerProvider,
      Provider<DnsIntegrityChecker> dnsIntegrityCheckerProvider,
      Provider<TlsIntegrityChecker> tlsIntegrityCheckerProvider,
      Provider<CaptivePortalDetector> captivePortalDetectorProvider,
      Provider<VpnMonitor> vpnMonitorProvider) {
    return new MonitoringService_MembersInjector(cellInfoCollectorProvider, telephonyMonitorProvider, threatDetectionEngineProvider, cellRepositoryProvider, alertRepositoryProvider, scanRepositoryProvider, sensorFusionEngineProvider, motionDetectorProvider, wifiMonitorProvider, wifiThreatDetectorProvider, trustedNetworkDaoProvider, bleTrackingDetectorProvider, bleAlertManagerProvider, dnsIntegrityCheckerProvider, tlsIntegrityCheckerProvider, captivePortalDetectorProvider, vpnMonitorProvider);
  }

  @Override
  public void injectMembers(MonitoringService instance) {
    injectCellInfoCollector(instance, cellInfoCollectorProvider.get());
    injectTelephonyMonitor(instance, telephonyMonitorProvider.get());
    injectThreatDetectionEngine(instance, threatDetectionEngineProvider.get());
    injectCellRepository(instance, cellRepositoryProvider.get());
    injectAlertRepository(instance, alertRepositoryProvider.get());
    injectScanRepository(instance, scanRepositoryProvider.get());
    injectSensorFusionEngine(instance, sensorFusionEngineProvider.get());
    injectMotionDetector(instance, motionDetectorProvider.get());
    injectWifiMonitor(instance, wifiMonitorProvider.get());
    injectWifiThreatDetector(instance, wifiThreatDetectorProvider.get());
    injectTrustedNetworkDao(instance, trustedNetworkDaoProvider.get());
    injectBleTrackingDetector(instance, bleTrackingDetectorProvider.get());
    injectBleAlertManager(instance, bleAlertManagerProvider.get());
    injectDnsIntegrityChecker(instance, dnsIntegrityCheckerProvider.get());
    injectTlsIntegrityChecker(instance, tlsIntegrityCheckerProvider.get());
    injectCaptivePortalDetector(instance, captivePortalDetectorProvider.get());
    injectVpnMonitor(instance, vpnMonitorProvider.get());
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.cellInfoCollector")
  public static void injectCellInfoCollector(MonitoringService instance,
      CellInfoCollector cellInfoCollector) {
    instance.cellInfoCollector = cellInfoCollector;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.telephonyMonitor")
  public static void injectTelephonyMonitor(MonitoringService instance,
      TelephonyMonitor telephonyMonitor) {
    instance.telephonyMonitor = telephonyMonitor;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.threatDetectionEngine")
  public static void injectThreatDetectionEngine(MonitoringService instance,
      ThreatDetectionEngine threatDetectionEngine) {
    instance.threatDetectionEngine = threatDetectionEngine;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.cellRepository")
  public static void injectCellRepository(MonitoringService instance,
      CellRepository cellRepository) {
    instance.cellRepository = cellRepository;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.alertRepository")
  public static void injectAlertRepository(MonitoringService instance,
      AlertRepository alertRepository) {
    instance.alertRepository = alertRepository;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.scanRepository")
  public static void injectScanRepository(MonitoringService instance,
      ScanRepository scanRepository) {
    instance.scanRepository = scanRepository;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.sensorFusionEngine")
  public static void injectSensorFusionEngine(MonitoringService instance,
      SensorFusionEngine sensorFusionEngine) {
    instance.sensorFusionEngine = sensorFusionEngine;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.motionDetector")
  public static void injectMotionDetector(MonitoringService instance,
      MotionDetector motionDetector) {
    instance.motionDetector = motionDetector;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.wifiMonitor")
  public static void injectWifiMonitor(MonitoringService instance, WifiMonitor wifiMonitor) {
    instance.wifiMonitor = wifiMonitor;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.wifiThreatDetector")
  public static void injectWifiThreatDetector(MonitoringService instance,
      WifiThreatDetector wifiThreatDetector) {
    instance.wifiThreatDetector = wifiThreatDetector;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.trustedNetworkDao")
  public static void injectTrustedNetworkDao(MonitoringService instance,
      TrustedNetworkDao trustedNetworkDao) {
    instance.trustedNetworkDao = trustedNetworkDao;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.bleTrackingDetector")
  public static void injectBleTrackingDetector(MonitoringService instance,
      BleTrackingDetector bleTrackingDetector) {
    instance.bleTrackingDetector = bleTrackingDetector;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.bleAlertManager")
  public static void injectBleAlertManager(MonitoringService instance,
      BleAlertManager bleAlertManager) {
    instance.bleAlertManager = bleAlertManager;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.dnsIntegrityChecker")
  public static void injectDnsIntegrityChecker(MonitoringService instance,
      DnsIntegrityChecker dnsIntegrityChecker) {
    instance.dnsIntegrityChecker = dnsIntegrityChecker;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.tlsIntegrityChecker")
  public static void injectTlsIntegrityChecker(MonitoringService instance,
      TlsIntegrityChecker tlsIntegrityChecker) {
    instance.tlsIntegrityChecker = tlsIntegrityChecker;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.captivePortalDetector")
  public static void injectCaptivePortalDetector(MonitoringService instance,
      CaptivePortalDetector captivePortalDetector) {
    instance.captivePortalDetector = captivePortalDetector;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.service.MonitoringService.vpnMonitor")
  public static void injectVpnMonitor(MonitoringService instance, VpnMonitor vpnMonitor) {
    instance.vpnMonitor = vpnMonitor;
  }
}
