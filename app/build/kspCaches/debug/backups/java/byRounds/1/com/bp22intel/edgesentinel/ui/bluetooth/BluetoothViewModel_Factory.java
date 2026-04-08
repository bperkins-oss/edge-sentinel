package com.bp22intel.edgesentinel.ui.bluetooth;

import com.bp22intel.edgesentinel.detection.bluetooth.BleAlertManager;
import com.bp22intel.edgesentinel.detection.bluetooth.BleDeviceTracker;
import com.bp22intel.edgesentinel.detection.bluetooth.BleTrackingDetector;
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
public final class BluetoothViewModel_Factory implements Factory<BluetoothViewModel> {
  private final Provider<BleTrackingDetector> detectorProvider;

  private final Provider<BleDeviceTracker> deviceTrackerProvider;

  private final Provider<BleAlertManager> alertManagerProvider;

  public BluetoothViewModel_Factory(Provider<BleTrackingDetector> detectorProvider,
      Provider<BleDeviceTracker> deviceTrackerProvider,
      Provider<BleAlertManager> alertManagerProvider) {
    this.detectorProvider = detectorProvider;
    this.deviceTrackerProvider = deviceTrackerProvider;
    this.alertManagerProvider = alertManagerProvider;
  }

  @Override
  public BluetoothViewModel get() {
    return newInstance(detectorProvider.get(), deviceTrackerProvider.get(), alertManagerProvider.get());
  }

  public static BluetoothViewModel_Factory create(Provider<BleTrackingDetector> detectorProvider,
      Provider<BleDeviceTracker> deviceTrackerProvider,
      Provider<BleAlertManager> alertManagerProvider) {
    return new BluetoothViewModel_Factory(detectorProvider, deviceTrackerProvider, alertManagerProvider);
  }

  public static BluetoothViewModel newInstance(BleTrackingDetector detector,
      BleDeviceTracker deviceTracker, BleAlertManager alertManager) {
    return new BluetoothViewModel(detector, deviceTracker, alertManager);
  }
}
