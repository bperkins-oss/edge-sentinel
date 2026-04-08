package com.bp22intel.edgesentinel.ui.alerts;

import androidx.lifecycle.SavedStateHandle;
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
  private final Provider<AlertRepository> alertRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public AlertDetailViewModel_Factory(Provider<AlertRepository> alertRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.alertRepositoryProvider = alertRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public AlertDetailViewModel get() {
    return newInstance(alertRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static AlertDetailViewModel_Factory create(
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new AlertDetailViewModel_Factory(alertRepositoryProvider, savedStateHandleProvider);
  }

  public static AlertDetailViewModel newInstance(AlertRepository alertRepository,
      SavedStateHandle savedStateHandle) {
    return new AlertDetailViewModel(alertRepository, savedStateHandle);
  }
}
