package com.bp22intel.edgesentinel.analysis;

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
public final class ThreatAnalyst_Factory implements Factory<ThreatAnalyst> {
  @Override
  public ThreatAnalyst get() {
    return newInstance();
  }

  public static ThreatAnalyst_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ThreatAnalyst newInstance() {
    return new ThreatAnalyst();
  }

  private static final class InstanceHolder {
    private static final ThreatAnalyst_Factory INSTANCE = new ThreatAnalyst_Factory();
  }
}
