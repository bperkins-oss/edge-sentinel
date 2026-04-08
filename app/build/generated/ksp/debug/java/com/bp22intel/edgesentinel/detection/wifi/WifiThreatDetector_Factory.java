package com.bp22intel.edgesentinel.detection.wifi;

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
public final class WifiThreatDetector_Factory implements Factory<WifiThreatDetector> {
  @Override
  public WifiThreatDetector get() {
    return newInstance();
  }

  public static WifiThreatDetector_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static WifiThreatDetector newInstance() {
    return new WifiThreatDetector();
  }

  private static final class InstanceHolder {
    private static final WifiThreatDetector_Factory INSTANCE = new WifiThreatDetector_Factory();
  }
}
