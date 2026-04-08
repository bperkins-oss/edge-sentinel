package com.bp22intel.edgesentinel.service;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.bp22intel.edgesentinel.data.sensor.CellInfoCollector;
import com.bp22intel.edgesentinel.detection.engine.ThreatDetectionEngine;
import com.bp22intel.edgesentinel.domain.repository.AlertRepository;
import com.bp22intel.edgesentinel.domain.repository.CellRepository;
import com.bp22intel.edgesentinel.domain.repository.ScanRepository;
import dagger.internal.DaggerGenerated;
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
public final class ScanWorker_Factory {
  private final Provider<CellInfoCollector> cellInfoCollectorProvider;

  private final Provider<ThreatDetectionEngine> threatDetectionEngineProvider;

  private final Provider<CellRepository> cellRepositoryProvider;

  private final Provider<AlertRepository> alertRepositoryProvider;

  private final Provider<ScanRepository> scanRepositoryProvider;

  public ScanWorker_Factory(Provider<CellInfoCollector> cellInfoCollectorProvider,
      Provider<ThreatDetectionEngine> threatDetectionEngineProvider,
      Provider<CellRepository> cellRepositoryProvider,
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider) {
    this.cellInfoCollectorProvider = cellInfoCollectorProvider;
    this.threatDetectionEngineProvider = threatDetectionEngineProvider;
    this.cellRepositoryProvider = cellRepositoryProvider;
    this.alertRepositoryProvider = alertRepositoryProvider;
    this.scanRepositoryProvider = scanRepositoryProvider;
  }

  public ScanWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, cellInfoCollectorProvider.get(), threatDetectionEngineProvider.get(), cellRepositoryProvider.get(), alertRepositoryProvider.get(), scanRepositoryProvider.get());
  }

  public static ScanWorker_Factory create(Provider<CellInfoCollector> cellInfoCollectorProvider,
      Provider<ThreatDetectionEngine> threatDetectionEngineProvider,
      Provider<CellRepository> cellRepositoryProvider,
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider) {
    return new ScanWorker_Factory(cellInfoCollectorProvider, threatDetectionEngineProvider, cellRepositoryProvider, alertRepositoryProvider, scanRepositoryProvider);
  }

  public static ScanWorker newInstance(Context appContext, WorkerParameters workerParams,
      CellInfoCollector cellInfoCollector, ThreatDetectionEngine threatDetectionEngine,
      CellRepository cellRepository, AlertRepository alertRepository,
      ScanRepository scanRepository) {
    return new ScanWorker(appContext, workerParams, cellInfoCollector, threatDetectionEngine, cellRepository, alertRepository, scanRepository);
  }
}
