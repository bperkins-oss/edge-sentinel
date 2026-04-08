package com.bp22intel.edgesentinel.ui.map;

import android.content.Context;
import com.bp22intel.edgesentinel.detection.geo.ThreatGeolocation;
import com.bp22intel.edgesentinel.domain.repository.AlertRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ThreatMapViewModel_Factory implements Factory<ThreatMapViewModel> {
  private final Provider<AlertRepository> alertRepositoryProvider;

  private final Provider<ThreatGeolocation> threatGeolocationProvider;

  private final Provider<Context> contextProvider;

  public ThreatMapViewModel_Factory(Provider<AlertRepository> alertRepositoryProvider,
      Provider<ThreatGeolocation> threatGeolocationProvider, Provider<Context> contextProvider) {
    this.alertRepositoryProvider = alertRepositoryProvider;
    this.threatGeolocationProvider = threatGeolocationProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public ThreatMapViewModel get() {
    return newInstance(alertRepositoryProvider.get(), threatGeolocationProvider.get(), contextProvider.get());
  }

  public static ThreatMapViewModel_Factory create(Provider<AlertRepository> alertRepositoryProvider,
      Provider<ThreatGeolocation> threatGeolocationProvider, Provider<Context> contextProvider) {
    return new ThreatMapViewModel_Factory(alertRepositoryProvider, threatGeolocationProvider, contextProvider);
  }

  public static ThreatMapViewModel newInstance(AlertRepository alertRepository,
      ThreatGeolocation threatGeolocation, Context context) {
    return new ThreatMapViewModel(alertRepository, threatGeolocation, context);
  }
}
