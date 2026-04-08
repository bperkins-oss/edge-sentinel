package com.bp22intel.edgesentinel.di;

import com.bp22intel.edgesentinel.data.local.EdgeSentinelDatabase;
import com.bp22intel.edgesentinel.data.local.dao.BaselineDao;
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
public final class AppModule_ProvideBaselineDaoFactory implements Factory<BaselineDao> {
  private final Provider<EdgeSentinelDatabase> dbProvider;

  public AppModule_ProvideBaselineDaoFactory(Provider<EdgeSentinelDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BaselineDao get() {
    return provideBaselineDao(dbProvider.get());
  }

  public static AppModule_ProvideBaselineDaoFactory create(
      Provider<EdgeSentinelDatabase> dbProvider) {
    return new AppModule_ProvideBaselineDaoFactory(dbProvider);
  }

  public static BaselineDao provideBaselineDao(EdgeSentinelDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBaselineDao(db));
  }
}
