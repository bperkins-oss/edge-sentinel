package com.bp22intel.edgesentinel.analysis;

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
public final class ThreatAnalyst_Factory implements Factory<ThreatAnalyst> {
  private final Provider<FalsePositiveFilter> falsePositiveFilterProvider;

  public ThreatAnalyst_Factory(Provider<FalsePositiveFilter> falsePositiveFilterProvider) {
    this.falsePositiveFilterProvider = falsePositiveFilterProvider;
  }

  @Override
  public ThreatAnalyst get() {
    return newInstance(falsePositiveFilterProvider.get());
  }

  public static ThreatAnalyst_Factory create(
      Provider<FalsePositiveFilter> falsePositiveFilterProvider) {
    return new ThreatAnalyst_Factory(falsePositiveFilterProvider);
  }

  public static ThreatAnalyst newInstance(FalsePositiveFilter falsePositiveFilter) {
    return new ThreatAnalyst(falsePositiveFilter);
  }
}
