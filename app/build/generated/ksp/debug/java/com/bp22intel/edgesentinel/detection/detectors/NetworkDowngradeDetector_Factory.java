package com.bp22intel.edgesentinel.detection.detectors;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class NetworkDowngradeDetector_Factory implements Factory<NetworkDowngradeDetector> {
  @Override
  public NetworkDowngradeDetector get() {
    return newInstance();
  }

  public static NetworkDowngradeDetector_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static NetworkDowngradeDetector newInstance() {
    return new NetworkDowngradeDetector();
  }

  private static final class InstanceHolder {
    private static final NetworkDowngradeDetector_Factory INSTANCE = new NetworkDowngradeDetector_Factory();
  }
}
