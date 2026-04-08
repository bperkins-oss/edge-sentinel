package com.bp22intel.edgesentinel.ui.dashboard;

import com.bp22intel.edgesentinel.data.sensor.CellInfoCollector;
import com.bp22intel.edgesentinel.detection.engine.DemoDataGenerator;
import com.bp22intel.edgesentinel.domain.repository.AlertRepository;
import com.bp22intel.edgesentinel.domain.repository.CellRepository;
import com.bp22intel.edgesentinel.domain.repository.ScanRepository;
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

  public DashboardViewModel_Factory(Provider<AlertRepository> alertRepositoryProvider,
      Provider<CellRepository> cellRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider,
      Provider<DemoDataGenerator> demoDataGeneratorProvider,
      Provider<CellInfoCollector> cellInfoCollectorProvider) {
    this.alertRepositoryProvider = alertRepositoryProvider;
    this.cellRepositoryProvider = cellRepositoryProvider;
    this.scanRepositoryProvider = scanRepositoryProvider;
    this.demoDataGeneratorProvider = demoDataGeneratorProvider;
    this.cellInfoCollectorProvider = cellInfoCollectorProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(alertRepositoryProvider.get(), cellRepositoryProvider.get(), scanRepositoryProvider.get(), demoDataGeneratorProvider.get(), cellInfoCollectorProvider.get());
  }

  public static DashboardViewModel_Factory create(Provider<AlertRepository> alertRepositoryProvider,
      Provider<CellRepository> cellRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider,
      Provider<DemoDataGenerator> demoDataGeneratorProvider,
      Provider<CellInfoCollector> cellInfoCollectorProvider) {
    return new DashboardViewModel_Factory(alertRepositoryProvider, cellRepositoryProvider, scanRepositoryProvider, demoDataGeneratorProvider, cellInfoCollectorProvider);
  }

  public static DashboardViewModel newInstance(AlertRepository alertRepository,
      CellRepository cellRepository, ScanRepository scanRepository,
      DemoDataGenerator demoDataGenerator, CellInfoCollector cellInfoCollector) {
    return new DashboardViewModel(alertRepository, cellRepository, scanRepository, demoDataGenerator, cellInfoCollector);
  }
}
