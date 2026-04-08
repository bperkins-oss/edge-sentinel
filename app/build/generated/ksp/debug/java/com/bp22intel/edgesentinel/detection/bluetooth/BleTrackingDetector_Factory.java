package com.bp22intel.edgesentinel.detection.bluetooth;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class BleTrackingDetector_Factory implements Factory<BleTrackingDetector> {
  private final Provider<Context> contextProvider;

  private final Provider<BleDeviceTracker> deviceTrackerProvider;

  private final Provider<BleAlertManager> alertManagerProvider;

  private final Provider<BleTrackerIdentifier> trackerIdentifierProvider;

  public BleTrackingDetector_Factory(Provider<Context> contextProvider,
      Provider<BleDeviceTracker> deviceTrackerProvider,
      Provider<BleAlertManager> alertManagerProvider,
      Provider<BleTrackerIdentifier> trackerIdentifierProvider) {
    this.contextProvider = contextProvider;
    this.deviceTrackerProvider = deviceTrackerProvider;
    this.alertManagerProvider = alertManagerProvider;
    this.trackerIdentifierProvider = trackerIdentifierProvider;
  }

  @Override
  public BleTrackingDetector get() {
    return newInstance(contextProvider.get(), deviceTrackerProvider.get(), alertManagerProvider.get(), trackerIdentifierProvider.get());
  }

  public static BleTrackingDetector_Factory create(Provider<Context> contextProvider,
      Provider<BleDeviceTracker> deviceTrackerProvider,
      Provider<BleAlertManager> alertManagerProvider,
      Provider<BleTrackerIdentifier> trackerIdentifierProvider) {
    return new BleTrackingDetector_Factory(contextProvider, deviceTrackerProvider, alertManagerProvider, trackerIdentifierProvider);
  }

  public static BleTrackingDetector newInstance(Context context, BleDeviceTracker deviceTracker,
      BleAlertManager alertManager, BleTrackerIdentifier trackerIdentifier) {
    return new BleTrackingDetector(context, deviceTracker, alertManager, trackerIdentifier);
  }
}
