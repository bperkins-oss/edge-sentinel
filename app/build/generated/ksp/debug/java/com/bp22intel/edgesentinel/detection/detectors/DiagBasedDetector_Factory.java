package com.bp22intel.edgesentinel.detection.detectors;

import com.bp22intel.edgesentinel.diag.DiagBridge;
import com.bp22intel.edgesentinel.diag.DiagMessageParser;
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
public final class DiagBasedDetector_Factory implements Factory<DiagBasedDetector> {
  private final Provider<DiagBridge> diagBridgeProvider;

  private final Provider<DiagMessageParser> parserProvider;

  public DiagBasedDetector_Factory(Provider<DiagBridge> diagBridgeProvider,
      Provider<DiagMessageParser> parserProvider) {
    this.diagBridgeProvider = diagBridgeProvider;
    this.parserProvider = parserProvider;
  }

  @Override
  public DiagBasedDetector get() {
    return newInstance(diagBridgeProvider.get(), parserProvider.get());
  }

  public static DiagBasedDetector_Factory create(Provider<DiagBridge> diagBridgeProvider,
      Provider<DiagMessageParser> parserProvider) {
    return new DiagBasedDetector_Factory(diagBridgeProvider, parserProvider);
  }

  public static DiagBasedDetector newInstance(DiagBridge diagBridge, DiagMessageParser parser) {
    return new DiagBasedDetector(diagBridge, parser);
  }
}
