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
public final class CaptivePortalDetector_Factory implements Factory<CaptivePortalDetector> {
  @Override
  public CaptivePortalDetector get() {
    return newInstance();
  }

  public static CaptivePortalDetector_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CaptivePortalDetector newInstance() {
    return new CaptivePortalDetector();
  }

  private static final class InstanceHolder {
    private static final CaptivePortalDetector_Factory INSTANCE = new CaptivePortalDetector_Factory();
  }
}
