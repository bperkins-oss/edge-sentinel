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
public final class TrackingPatternDetector_Factory implements Factory<TrackingPatternDetector> {
  @Override
  public TrackingPatternDetector get() {
    return newInstance();
  }

  public static TrackingPatternDetector_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TrackingPatternDetector newInstance() {
    return new TrackingPatternDetector();
  }

  private static final class InstanceHolder {
    private static final TrackingPatternDetector_Factory INSTANCE = new TrackingPatternDetector_Factory();
  }
}
