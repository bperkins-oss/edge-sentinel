package com.bp22intel.edgesentinel.detection.network;

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
public final class DnsIntegrityChecker_Factory implements Factory<DnsIntegrityChecker> {
  @Override
  public DnsIntegrityChecker get() {
    return newInstance();
  }

  public static DnsIntegrityChecker_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DnsIntegrityChecker newInstance() {
    return new DnsIntegrityChecker();
  }

  private static final class InstanceHolder {
    private static final DnsIntegrityChecker_Factory INSTANCE = new DnsIntegrityChecker_Factory();
  }
}
