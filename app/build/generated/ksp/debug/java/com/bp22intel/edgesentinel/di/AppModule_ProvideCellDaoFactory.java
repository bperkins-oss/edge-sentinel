package com.bp22intel.edgesentinel.di;

import com.bp22intel.edgesentinel.data.local.EdgeSentinelDatabase;
import com.bp22intel.edgesentinel.data.local.dao.CellDao;
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
public final class AppModule_ProvideCellDaoFactory implements Factory<CellDao> {
  private final Provider<EdgeSentinelDatabase> dbProvider;

  public AppModule_ProvideCellDaoFactory(Provider<EdgeSentinelDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public CellDao get() {
    return provideCellDao(dbProvider.get());
  }

  public static AppModule_ProvideCellDaoFactory create(Provider<EdgeSentinelDatabase> dbProvider) {
    return new AppModule_ProvideCellDaoFactory(dbProvider);
  }

  public static CellDao provideCellDao(EdgeSentinelDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideCellDao(db));
  }
}
