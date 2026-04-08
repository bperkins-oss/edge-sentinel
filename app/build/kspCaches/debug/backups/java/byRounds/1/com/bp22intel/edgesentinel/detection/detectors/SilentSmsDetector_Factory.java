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
public final class SilentSmsDetector_Factory implements Factory<SilentSmsDetector> {
  @Override
  public SilentSmsDetector get() {
    return newInstance();
  }

  public static SilentSmsDetector_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SilentSmsDetector newInstance() {
    return new SilentSmsDetector();
  }

  private static final class InstanceHolder {
    private static final SilentSmsDetector_Factory INSTANCE = new SilentSmsDetector_Factory();
  }
}
