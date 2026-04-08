package com.bp22intel.edgesentinel.detection.scoring;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class ThreatScorer_Factory implements Factory<ThreatScorer> {
  @Override
  public ThreatScorer get() {
    return newInstance();
  }

  public static ThreatScorer_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ThreatScorer newInstance() {
    return new ThreatScorer();
  }

  private static final class InstanceHolder {
    private static final ThreatScorer_Factory INSTANCE = new ThreatScorer_Factory();
  }
}
