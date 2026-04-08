package com.bp22intel.edgesentinel.detection.bluetooth;

import com.bp22intel.edgesentinel.data.local.dao.BleDeviceDao;
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
public final class BleDeviceTracker_Factory implements Factory<BleDeviceTracker> {
  private final Provider<BleDeviceDao> bleDeviceDaoProvider;

  private final Provider<BleTrackerIdentifier> trackerIdentifierProvider;

  public BleDeviceTracker_Factory(Provider<BleDeviceDao> bleDeviceDaoProvider,
      Provider<BleTrackerIdentifier> trackerIdentifierProvider) {
    this.bleDeviceDaoProvider = bleDeviceDaoProvider;
    this.trackerIdentifierProvider = trackerIdentifierProvider;
  }

  @Override
  public BleDeviceTracker get() {
    return newInstance(bleDeviceDaoProvider.get(), trackerIdentifierProvider.get());
  }

  public static BleDeviceTracker_Factory create(Provider<BleDeviceDao> bleDeviceDaoProvider,
      Provider<BleTrackerIdentifier> trackerIdentifierProvider) {
    return new BleDeviceTracker_Factory(bleDeviceDaoProvider, trackerIdentifierProvider);
  }

  public static BleDeviceTracker newInstance(BleDeviceDao bleDeviceDao,
      BleTrackerIdentifier trackerIdentifier) {
    return new BleDeviceTracker(bleDeviceDao, trackerIdentifier);
  }
}
