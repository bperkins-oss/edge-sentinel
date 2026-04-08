package com.bp22intel.edgesentinel.detection.engine;

import com.bp22intel.edgesentinel.detection.detectors.ThreatDetector;
import com.bp22intel.edgesentinel.detection.scoring.ThreatScorer;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.util.Set;
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
public final class ThreatDetectionEngine_Factory implements Factory<ThreatDetectionEngine> {
  private final Provider<Set<ThreatDetector>> detectorsProvider;

  private final Provider<ThreatScorer> scorerProvider;

  public ThreatDetectionEngine_Factory(Provider<Set<ThreatDetector>> detectorsProvider,
      Provider<ThreatScorer> scorerProvider) {
    this.detectorsProvider = detectorsProvider;
    this.scorerProvider = scorerProvider;
  }

  @Override
  public ThreatDetectionEngine get() {
    return newInstance(detectorsProvider.get(), scorerProvider.get());
  }

  public static ThreatDetectionEngine_Factory create(
      Provider<Set<ThreatDetector>> detectorsProvider, Provider<ThreatScorer> scorerProvider) {
    return new ThreatDetectionEngine_Factory(detectorsProvider, scorerProvider);
  }

  public static ThreatDetectionEngine newInstance(Set<ThreatDetector> detectors,
      ThreatScorer scorer) {
    return new ThreatDetectionEngine(detectors, scorer);
  }
}
