package com.bp22intel.edgesentinel.diag;

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
public final class DiagBridge_Factory implements Factory<DiagBridge> {
  private final Provider<RootChecker> rootCheckerProvider;

  public DiagBridge_Factory(Provider<RootChecker> rootCheckerProvider) {
    this.rootCheckerProvider = rootCheckerProvider;
  }

  @Override
  public DiagBridge get() {
    return newInstance(rootCheckerProvider.get());
  }

  public static DiagBridge_Factory create(Provider<RootChecker> rootCheckerProvider) {
    return new DiagBridge_Factory(rootCheckerProvider);
  }

  public static DiagBridge newInstance(RootChecker rootChecker) {
    return new DiagBridge(rootChecker);
  }
}
