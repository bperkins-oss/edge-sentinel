package com.bp22intel.edgesentinel.diag;

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
public final class RootChecker_Factory implements Factory<RootChecker> {
  @Override
  public RootChecker get() {
    return newInstance();
  }

  public static RootChecker_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RootChecker newInstance() {
    return new RootChecker();
  }

  private static final class InstanceHolder {
    private static final RootChecker_Factory INSTANCE = new RootChecker_Factory();
  }
}
