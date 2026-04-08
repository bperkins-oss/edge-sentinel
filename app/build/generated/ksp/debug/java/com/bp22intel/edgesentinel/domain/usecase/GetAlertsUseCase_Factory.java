package com.bp22intel.edgesentinel.domain.usecase;

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
public final class GetAlertsUseCase_Factory implements Factory<GetAlertsUseCase> {
  private final Provider<AlertRepository> alertRepositoryProvider;

  public GetAlertsUseCase_Factory(Provider<AlertRepository> alertRepositoryProvider) {
    this.alertRepositoryProvider = alertRepositoryProvider;
  }

  @Override
  public GetAlertsUseCase get() {
    return newInstance(alertRepositoryProvider.get());
  }

  public static GetAlertsUseCase_Factory create(Provider<AlertRepository> alertRepositoryProvider) {
    return new GetAlertsUseCase_Factory(alertRepositoryProvider);
  }

  public static GetAlertsUseCase newInstance(AlertRepository alertRepository) {
    return new GetAlertsUseCase(alertRepository);
  }
}
