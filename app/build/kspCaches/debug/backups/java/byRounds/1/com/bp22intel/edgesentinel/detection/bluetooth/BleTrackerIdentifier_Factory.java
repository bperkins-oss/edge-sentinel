package com.bp22intel.edgesentinel.detection.bluetooth;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class BleTrackerIdentifier_Factory implements Factory<BleTrackerIdentifier> {
  @Override
  public BleTrackerIdentifier get() {
    return newInstance();
  }

  public static BleTrackerIdentifier_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BleTrackerIdentifier newInstance() {
    return new BleTrackerIdentifier();
  }

  private static final class InstanceHolder {
    private static final BleTrackerIdentifier_Factory INSTANCE = new BleTrackerIdentifier_Factory();
  }
}
