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
public final class SensorFusionEngine_Factory implements Factory<SensorFusionEngine> {
  private final Provider<ThreatNarrator> threatNarratorProvider;

  public SensorFusionEngine_Factory(Provider<ThreatNarrator> threatNarratorProvider) {
    this.threatNarratorProvider = threatNarratorProvider;
  }

  @Override
  public SensorFusionEngine get() {
    return newInstance(threatNarratorProvider.get());
  }

  public static SensorFusionEngine_Factory create(Provider<ThreatNarrator> threatNarratorProvider) {
    return new SensorFusionEngine_Factory(threatNarratorProvider);
  }

  public static SensorFusionEngine newInstance(ThreatNarrator threatNarrator) {
    return new SensorFusionEngine(threatNarrator);
  }
}
