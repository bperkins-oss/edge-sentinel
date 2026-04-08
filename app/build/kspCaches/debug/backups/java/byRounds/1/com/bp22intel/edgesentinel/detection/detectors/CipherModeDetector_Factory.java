package com.bp22intel.edgesentinel.detection.detectors;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
  private final Provider<DiagBasedDetector> diagBasedDetectorProvider;

  public CipherModeDetector_Factory(Provider<DiagBasedDetector> diagBasedDetectorProvider) {
    this.diagBasedDetectorProvider = diagBasedDetectorProvider;
  }

  @Override
  public CipherModeDetector get() {
    return newInstance(diagBasedDetectorProvider.get());
  }

  public static CipherModeDetector_Factory create(
      Provider<DiagBasedDetector> diagBasedDetectorProvider) {
    return new CipherModeDetector_Factory(diagBasedDetectorProvider);
  }

  public static CipherModeDetector newInstance(DiagBasedDetector diagBasedDetector) {
    return new CipherModeDetector(diagBasedDetector);
  }
}
