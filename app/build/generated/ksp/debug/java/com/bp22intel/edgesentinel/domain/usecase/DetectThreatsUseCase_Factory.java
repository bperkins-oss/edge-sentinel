package com.bp22intel.edgesentinel.domain.usecase;

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
public final class DetectThreatsUseCase_Factory implements Factory<DetectThreatsUseCase> {
  private final Provider<CellRepository> cellRepositoryProvider;

  private final Provider<AlertRepository> alertRepositoryProvider;

  private final Provider<ScanRepository> scanRepositoryProvider;

  public DetectThreatsUseCase_Factory(Provider<CellRepository> cellRepositoryProvider,
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider) {
    this.cellRepositoryProvider = cellRepositoryProvider;
    this.alertRepositoryProvider = alertRepositoryProvider;
    this.scanRepositoryProvider = scanRepositoryProvider;
  }

  @Override
  public DetectThreatsUseCase get() {
    return newInstance(cellRepositoryProvider.get(), alertRepositoryProvider.get(), scanRepositoryProvider.get());
  }

  public static DetectThreatsUseCase_Factory create(Provider<CellRepository> cellRepositoryProvider,
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider) {
    return new DetectThreatsUseCase_Factory(cellRepositoryProvider, alertRepositoryProvider, scanRepositoryProvider);
  }

  public static DetectThreatsUseCase newInstance(CellRepository cellRepository,
      AlertRepository alertRepository, ScanRepository scanRepository) {
    return new DetectThreatsUseCase(cellRepository, alertRepository, scanRepository);
  }
}
