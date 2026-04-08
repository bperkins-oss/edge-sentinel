package com.bp22intel.edgesentinel.di;

import com.bp22intel.edgesentinel.data.local.EdgeSentinelDatabase;
import com.bp22intel.edgesentinel.data.local.dao.ScanDao;
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
public final class AppModule_ProvideScanDaoFactory implements Factory<ScanDao> {
  private final Provider<EdgeSentinelDatabase> dbProvider;

  public AppModule_ProvideScanDaoFactory(Provider<EdgeSentinelDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ScanDao get() {
    return provideScanDao(dbProvider.get());
  }

  public static AppModule_ProvideScanDaoFactory create(Provider<EdgeSentinelDatabase> dbProvider) {
    return new AppModule_ProvideScanDaoFactory(dbProvider);
  }

  public static ScanDao provideScanDao(EdgeSentinelDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideScanDao(db));
  }
}
