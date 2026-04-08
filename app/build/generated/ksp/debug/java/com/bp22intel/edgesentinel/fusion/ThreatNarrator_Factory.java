package com.bp22intel.edgesentinel.fusion;

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
public final class ThreatNarrator_Factory implements Factory<ThreatNarrator> {
  @Override
  public ThreatNarrator get() {
    return newInstance();
  }

  public static ThreatNarrator_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ThreatNarrator newInstance() {
    return new ThreatNarrator();
  }

  private static final class InstanceHolder {
    private static final ThreatNarrator_Factory INSTANCE = new ThreatNarrator_Factory();
  }
}
