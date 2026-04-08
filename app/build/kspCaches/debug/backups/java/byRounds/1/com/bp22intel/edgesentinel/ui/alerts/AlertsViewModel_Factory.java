package com.bp22intel.edgesentinel.ui.alerts;

import com.bp22intel.edgesentinel.domain.repository.AlertRepository;
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

  public AlertsViewModel_Factory(Provider<AlertRepository> alertRepositoryProvider) {
    this.alertRepositoryProvider = alertRepositoryProvider;
  }

  @Override
  public AlertsViewModel get() {
    return newInstance(alertRepositoryProvider.get());
  }

  public static AlertsViewModel_Factory create(Provider<AlertRepository> alertRepositoryProvider) {
    return new AlertsViewModel_Factory(alertRepositoryProvider);
  }

  public static AlertsViewModel newInstance(AlertRepository alertRepository) {
    return new AlertsViewModel(alertRepository);
  }
}
