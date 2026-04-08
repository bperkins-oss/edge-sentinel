package com.bp22intel.edgesentinel.di;

import com.bp22intel.edgesentinel.data.local.EdgeSentinelDatabase;
import com.bp22intel.edgesentinel.data.local.dao.KnownTowerDao;
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
public final class AppModule_ProvideKnownTowerDaoFactory implements Factory<KnownTowerDao> {
  private final Provider<EdgeSentinelDatabase> dbProvider;

  public AppModule_ProvideKnownTowerDaoFactory(Provider<EdgeSentinelDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public KnownTowerDao get() {
    return provideKnownTowerDao(dbProvider.get());
  }

  public static AppModule_ProvideKnownTowerDaoFactory create(
      Provider<EdgeSentinelDatabase> dbProvider) {
    return new AppModule_ProvideKnownTowerDaoFactory(dbProvider);
  }

  public static KnownTowerDao provideKnownTowerDao(EdgeSentinelDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideKnownTowerDao(db));
  }
}
