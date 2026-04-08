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
public final class FakeBtsDetector_Factory implements Factory<FakeBtsDetector> {
  @Override
  public FakeBtsDetector get() {
    return newInstance();
  }

  public static FakeBtsDetector_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FakeBtsDetector newInstance() {
    return new FakeBtsDetector();
  }

  private static final class InstanceHolder {
    private static final FakeBtsDetector_Factory INSTANCE = new FakeBtsDetector_Factory();
  }
}
