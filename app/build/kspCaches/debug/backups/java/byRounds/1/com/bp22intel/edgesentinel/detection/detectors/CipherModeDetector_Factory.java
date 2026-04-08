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
public final class CipherModeDetector_Factory implements Factory<CipherModeDetector> {
  @Override
  public CipherModeDetector get() {
    return newInstance();
  }

  public static CipherModeDetector_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CipherModeDetector newInstance() {
    return new CipherModeDetector();
  }

  private static final class InstanceHolder {
    private static final CipherModeDetector_Factory INSTANCE = new CipherModeDetector_Factory();
  }
}
