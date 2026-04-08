package com.bp22intel.edgesentinel.di;

import com.bp22intel.edgesentinel.data.local.EdgeSentinelDatabase;
import com.bp22intel.edgesentinel.data.local.dao.TrustedNetworkDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideTrustedNetworkDaoFactory implements Factory<TrustedNetworkDao> {
  private final Provider<EdgeSentinelDatabase> dbProvider;

  public AppModule_ProvideTrustedNetworkDaoFactory(Provider<EdgeSentinelDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public TrustedNetworkDao get() {
    return provideTrustedNetworkDao(dbProvider.get());
  }

  public static AppModule_ProvideTrustedNetworkDaoFactory create(
      Provider<EdgeSentinelDatabase> dbProvider) {
    return new AppModule_ProvideTrustedNetworkDaoFactory(dbProvider);
  }

  public static TrustedNetworkDao provideTrustedNetworkDao(EdgeSentinelDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTrustedNetworkDao(db));
  }
}
