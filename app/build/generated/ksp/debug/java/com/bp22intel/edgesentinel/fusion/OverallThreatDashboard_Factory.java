package com.bp22intel.edgesentinel.fusion;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class OverallThreatDashboard_Factory implements Factory<OverallThreatDashboard> {
  private final Provider<SensorFusionEngine> fusionEngineProvider;

  private final Provider<ThreatNarrator> threatNarratorProvider;

  public OverallThreatDashboard_Factory(Provider<SensorFusionEngine> fusionEngineProvider,
      Provider<ThreatNarrator> threatNarratorProvider) {
    this.fusionEngineProvider = fusionEngineProvider;
    this.threatNarratorProvider = threatNarratorProvider;
  }

  @Override
  public OverallThreatDashboard get() {
    return newInstance(fusionEngineProvider.get(), threatNarratorProvider.get());
  }

  public static OverallThreatDashboard_Factory create(
      Provider<SensorFusionEngine> fusionEngineProvider,
      Provider<ThreatNarrator> threatNarratorProvider) {
    return new OverallThreatDashboard_Factory(fusionEngineProvider, threatNarratorProvider);
  }

  public static OverallThreatDashboard newInstance(SensorFusionEngine fusionEngine,
      ThreatNarrator threatNarrator) {
    return new OverallThreatDashboard(fusionEngine, threatNarrator);
  }
}
