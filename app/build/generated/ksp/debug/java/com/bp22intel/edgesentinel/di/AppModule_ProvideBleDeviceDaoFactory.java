package com.bp22intel.edgesentinel.di;

import com.bp22intel.edgesentinel.data.local.EdgeSentinelDatabase;
import com.bp22intel.edgesentinel.data.local.dao.BleDeviceDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideBleDeviceDaoFactory implements Factory<BleDeviceDao> {
  private final Provider<EdgeSentinelDatabase> dbProvider;

  public AppModule_ProvideBleDeviceDaoFactory(Provider<EdgeSentinelDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BleDeviceDao get() {
    return provideBleDeviceDao(dbProvider.get());
  }

  public static AppModule_ProvideBleDeviceDaoFactory create(
      Provider<EdgeSentinelDatabase> dbProvider) {
    return new AppModule_ProvideBleDeviceDaoFactory(dbProvider);
  }

  public static BleDeviceDao provideBleDeviceDao(EdgeSentinelDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBleDeviceDao(db));
  }
}
