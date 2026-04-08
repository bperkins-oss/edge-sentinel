package com.bp22intel.edgesentinel.ui.alerts;

import androidx.lifecycle.SavedStateHandle;
import com.bp22intel.edgesentinel.analysis.ThreatAnalyst;
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
public final class AlertDetailViewModel_Factory implements Factory<AlertDetailViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<AlertRepository> alertRepositoryProvider;

  private final Provider<ThreatAnalyst> threatAnalystProvider;

  public AlertDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<ThreatAnalyst> threatAnalystProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.alertRepositoryProvider = alertRepositoryProvider;
    this.threatAnalystProvider = threatAnalystProvider;
  }

  @Override
  public AlertDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), alertRepositoryProvider.get(), threatAnalystProvider.get());
  }

  public static AlertDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<ThreatAnalyst> threatAnalystProvider) {
    return new AlertDetailViewModel_Factory(savedStateHandleProvider, alertRepositoryProvider, threatAnalystProvider);
  }

  public static AlertDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      AlertRepository alertRepository, ThreatAnalyst threatAnalyst) {
    return new AlertDetailViewModel(savedStateHandle, alertRepository, threatAnalyst);
  }
}
