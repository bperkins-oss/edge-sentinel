package com.bp22intel.edgesentinel.detection.bluetooth;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class BleAlertManager_Factory implements Factory<BleAlertManager> {
  private final Provider<BleDeviceTracker> deviceTrackerProvider;

  public BleAlertManager_Factory(Provider<BleDeviceTracker> deviceTrackerProvider) {
    this.deviceTrackerProvider = deviceTrackerProvider;
  }

  @Override
  public BleAlertManager get() {
    return newInstance(deviceTrackerProvider.get());
  }

  public static BleAlertManager_Factory create(Provider<BleDeviceTracker> deviceTrackerProvider) {
    return new BleAlertManager_Factory(deviceTrackerProvider);
  }

  public static BleAlertManager newInstance(BleDeviceTracker deviceTracker) {
    return new BleAlertManager(deviceTracker);
  }
}
