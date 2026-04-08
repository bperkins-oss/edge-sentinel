package com.bp22intel.edgesentinel.detection.geo;

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
public final class ThreatGeolocation_Factory implements Factory<ThreatGeolocation> {
  @Override
  public ThreatGeolocation get() {
    return newInstance();
  }

  public static ThreatGeolocation_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ThreatGeolocation newInstance() {
    return new ThreatGeolocation();
  }

  private static final class InstanceHolder {
    private static final ThreatGeolocation_Factory INSTANCE = new ThreatGeolocation_Factory();
  }
}
