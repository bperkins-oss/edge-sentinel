package com.bp22intel.edgesentinel.service;

import com.bp22intel.edgesentinel.data.sensor.CellInfoCollector;
import com.bp22intel.edgesentinel.data.sensor.TelephonyMonitor;
import com.bp22intel.edgesentinel.detection.engine.ThreatDetectionEngine;
import com.bp22intel.edgesentinel.domain.repository.AlertRepository;
import com.bp22intel.edgesentinel.domain.repository.CellRepository;
import com.bp22intel.edgesentinel.domain.repository.ScanRepository;
import com.bp22intel.edgesentinel.fusion.SensorFusionEngine;
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

  public MonitoringService_MembersInjector(Provider<CellInfoCollector> cellInfoCollectorProvider,
      Provider<TelephonyMonitor> telephonyMonitorProvider,
      Provider<ThreatDetectionEngine> threatDetectionEngineProvider,
      Provider<CellRepository> cellRepositoryProvider,
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider,
      Provider<SensorFusionEngine> sensorFusionEngineProvider) {
    this.cellInfoCollectorProvider = cellInfoCollectorProvider;
    this.telephonyMonitorProvider = telephonyMonitorProvider;
    this.threatDetectionEngineProvider = threatDetectionEngineProvider;
    this.cellRepositoryProvider = cellRepositoryProvider;
    this.alertRepositoryProvider = alertRepositoryProvider;
    this.scanRepositoryProvider = scanRepositoryProvider;
    this.sensorFusionEngineProvider = sensorFusionEngineProvider;
  }

  public static MembersInjector<MonitoringService> create(
      Provider<CellInfoCollector> cellInfoCollectorProvider,
      Provider<TelephonyMonitor> telephonyMonitorProvider,
      Provider<ThreatDetectionEngine> threatDetectionEngineProvider,
      Provider<CellRepository> cellRepositoryProvider,
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider,
      Provider<SensorFusionEngine> sensorFusionEngineProvider) {
    return new MonitoringService_MembersInjector(cellInfoCollectorProvider, telephonyMonitorProvider, threatDetectionEngineProvider, cellRepositoryProvider, alertRepositoryProvider, scanRepositoryProvider, sensorFusionEngineProvider);
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
}
