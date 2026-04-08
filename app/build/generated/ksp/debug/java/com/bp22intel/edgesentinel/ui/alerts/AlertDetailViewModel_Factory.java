package com.bp22intel.edgesentinel.ui.alerts;

import androidx.lifecycle.SavedStateHandle;
import com.bp22intel.edgesentinel.analysis.ThreatAnalyst;
import com.bp22intel.edgesentinel.data.local.dao.AlertFeedbackDao;
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

  private final Provider<AlertFeedbackDao> feedbackDaoProvider;

  public AlertDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<ThreatAnalyst> threatAnalystProvider,
      Provider<AlertFeedbackDao> feedbackDaoProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.alertRepositoryProvider = alertRepositoryProvider;
    this.threatAnalystProvider = threatAnalystProvider;
    this.feedbackDaoProvider = feedbackDaoProvider;
  }

  @Override
  public AlertDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), alertRepositoryProvider.get(), threatAnalystProvider.get(), feedbackDaoProvider.get());
  }

  public static AlertDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<ThreatAnalyst> threatAnalystProvider,
      Provider<AlertFeedbackDao> feedbackDaoProvider) {
    return new AlertDetailViewModel_Factory(savedStateHandleProvider, alertRepositoryProvider, threatAnalystProvider, feedbackDaoProvider);
  }

  public static AlertDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      AlertRepository alertRepository, ThreatAnalyst threatAnalyst, AlertFeedbackDao feedbackDao) {
    return new AlertDetailViewModel(savedStateHandle, alertRepository, threatAnalyst, feedbackDao);
  }
}
