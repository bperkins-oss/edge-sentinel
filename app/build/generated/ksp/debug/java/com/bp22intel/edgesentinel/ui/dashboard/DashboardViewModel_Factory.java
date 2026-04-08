package com.bp22intel.edgesentinel.ui.dashboard;

import com.bp22intel.edgesentinel.analysis.ThreatAnalyst;
import com.bp22intel.edgesentinel.data.sensor.CellInfoCollector;
import com.bp22intel.edgesentinel.detection.engine.DemoDataGenerator;
import com.bp22intel.edgesentinel.domain.repository.AlertRepository;
import com.bp22intel.edgesentinel.domain.repository.CellRepository;
import com.bp22intel.edgesentinel.domain.repository.ScanRepository;
import com.bp22intel.edgesentinel.fusion.OverallThreatDashboard;
import com.bp22intel.edgesentinel.fusion.SensorFusionEngine;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<AlertRepository> alertRepositoryProvider;

  private final Provider<CellRepository> cellRepositoryProvider;

  private final Provider<ScanRepository> scanRepositoryProvider;

  private final Provider<DemoDataGenerator> demoDataGeneratorProvider;

  private final Provider<CellInfoCollector> cellInfoCollectorProvider;

  private final Provider<SensorFusionEngine> sensorFusionEngineProvider;

  private final Provider<OverallThreatDashboard> overallThreatDashboardProvider;

  private final Provider<ThreatAnalyst> threatAnalystProvider;

  public DashboardViewModel_Factory(Provider<AlertRepository> alertRepositoryProvider,
      Provider<CellRepository> cellRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider,
      Provider<DemoDataGenerator> demoDataGeneratorProvider,
      Provider<CellInfoCollector> cellInfoCollectorProvider,
      Provider<SensorFusionEngine> sensorFusionEngineProvider,
      Provider<OverallThreatDashboard> overallThreatDashboardProvider,
      Provider<ThreatAnalyst> threatAnalystProvider) {
    this.alertRepositoryProvider = alertRepositoryProvider;
    this.cellRepositoryProvider = cellRepositoryProvider;
    this.scanRepositoryProvider = scanRepositoryProvider;
    this.demoDataGeneratorProvider = demoDataGeneratorProvider;
    this.cellInfoCollectorProvider = cellInfoCollectorProvider;
    this.sensorFusionEngineProvider = sensorFusionEngineProvider;
    this.overallThreatDashboardProvider = overallThreatDashboardProvider;
    this.threatAnalystProvider = threatAnalystProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(alertRepositoryProvider.get(), cellRepositoryProvider.get(), scanRepositoryProvider.get(), demoDataGeneratorProvider.get(), cellInfoCollectorProvider.get(), sensorFusionEngineProvider.get(), overallThreatDashboardProvider.get(), threatAnalystProvider.get());
  }

  public static DashboardViewModel_Factory create(Provider<AlertRepository> alertRepositoryProvider,
      Provider<CellRepository> cellRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider,
      Provider<DemoDataGenerator> demoDataGeneratorProvider,
      Provider<CellInfoCollector> cellInfoCollectorProvider,
      Provider<SensorFusionEngine> sensorFusionEngineProvider,
      Provider<OverallThreatDashboard> overallThreatDashboardProvider,
      Provider<ThreatAnalyst> threatAnalystProvider) {
    return new DashboardViewModel_Factory(alertRepositoryProvider, cellRepositoryProvider, scanRepositoryProvider, demoDataGeneratorProvider, cellInfoCollectorProvider, sensorFusionEngineProvider, overallThreatDashboardProvider, threatAnalystProvider);
  }

  public static DashboardViewModel newInstance(AlertRepository alertRepository,
      CellRepository cellRepository, ScanRepository scanRepository,
      DemoDataGenerator demoDataGenerator, CellInfoCollector cellInfoCollector,
      SensorFusionEngine sensorFusionEngine, OverallThreatDashboard overallThreatDashboard,
      ThreatAnalyst threatAnalyst) {
    return new DashboardViewModel(alertRepository, cellRepository, scanRepository, demoDataGenerator, cellInfoCollector, sensorFusionEngine, overallThreatDashboard, threatAnalyst);
  }
}
