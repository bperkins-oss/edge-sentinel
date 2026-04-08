package com.bp22intel.edgesentinel.ui.alerts;

import com.bp22intel.edgesentinel.domain.repository.AlertRepository;
import com.bp22intel.edgesentinel.export.AlertExporter;
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
public final class AlertsViewModel_Factory implements Factory<AlertsViewModel> {
  private final Provider<AlertRepository> alertRepositoryProvider;

  private final Provider<AlertExporter> alertExporterProvider;

  public AlertsViewModel_Factory(Provider<AlertRepository> alertRepositoryProvider,
      Provider<AlertExporter> alertExporterProvider) {
    this.alertRepositoryProvider = alertRepositoryProvider;
    this.alertExporterProvider = alertExporterProvider;
  }

  @Override
  public AlertsViewModel get() {
    return newInstance(alertRepositoryProvider.get(), alertExporterProvider.get());
  }

  public static AlertsViewModel_Factory create(Provider<AlertRepository> alertRepositoryProvider,
      Provider<AlertExporter> alertExporterProvider) {
    return new AlertsViewModel_Factory(alertRepositoryProvider, alertExporterProvider);
  }

  public static AlertsViewModel newInstance(AlertRepository alertRepository,
      AlertExporter alertExporter) {
    return new AlertsViewModel(alertRepository, alertExporter);
  }
}
