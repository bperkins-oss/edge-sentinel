package com.bp22intel.edgesentinel.detection.engine;

import com.bp22intel.edgesentinel.baseline.BaselineManager;
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

  private final Provider<BaselineManager> baselineManagerProvider;

  public ThreatDetectionEngine_Factory(Provider<Set<ThreatDetector>> detectorsProvider,
      Provider<ThreatScorer> scorerProvider, Provider<BaselineManager> baselineManagerProvider) {
    this.detectorsProvider = detectorsProvider;
    this.scorerProvider = scorerProvider;
    this.baselineManagerProvider = baselineManagerProvider;
  }

  @Override
  public ThreatDetectionEngine get() {
    return newInstance(detectorsProvider.get(), scorerProvider.get(), baselineManagerProvider.get());
  }

  public static ThreatDetectionEngine_Factory create(
      Provider<Set<ThreatDetector>> detectorsProvider, Provider<ThreatScorer> scorerProvider,
      Provider<BaselineManager> baselineManagerProvider) {
    return new ThreatDetectionEngine_Factory(detectorsProvider, scorerProvider, baselineManagerProvider);
  }

  public static ThreatDetectionEngine newInstance(Set<ThreatDetector> detectors,
      ThreatScorer scorer, BaselineManager baselineManager) {
    return new ThreatDetectionEngine(detectors, scorer, baselineManager);
  }
}
