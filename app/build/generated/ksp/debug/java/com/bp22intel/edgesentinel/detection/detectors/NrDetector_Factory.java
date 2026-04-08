package com.bp22intel.edgesentinel.detection.detectors;

import com.bp22intel.edgesentinel.data.sensor.NrMonitor;
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
public final class NrDetector_Factory implements Factory<NrDetector> {
  private final Provider<NrMonitor> nrMonitorProvider;

  public NrDetector_Factory(Provider<NrMonitor> nrMonitorProvider) {
    this.nrMonitorProvider = nrMonitorProvider;
  }

  @Override
  public NrDetector get() {
    return newInstance(nrMonitorProvider.get());
  }

  public static NrDetector_Factory create(Provider<NrMonitor> nrMonitorProvider) {
    return new NrDetector_Factory(nrMonitorProvider);
  }

  public static NrDetector newInstance(NrMonitor nrMonitor) {
    return new NrDetector(nrMonitor);
  }
}
