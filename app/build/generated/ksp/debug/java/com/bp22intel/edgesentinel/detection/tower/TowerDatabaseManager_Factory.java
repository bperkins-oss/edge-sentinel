package com.bp22intel.edgesentinel.detection.tower;

import android.content.Context;
import com.bp22intel.edgesentinel.data.local.dao.KnownTowerDao;
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
public final class TowerDatabaseManager_Factory implements Factory<TowerDatabaseManager> {
  private final Provider<Context> contextProvider;

  private final Provider<KnownTowerDao> knownTowerDaoProvider;

  public TowerDatabaseManager_Factory(Provider<Context> contextProvider,
      Provider<KnownTowerDao> knownTowerDaoProvider) {
    this.contextProvider = contextProvider;
    this.knownTowerDaoProvider = knownTowerDaoProvider;
  }

  @Override
  public TowerDatabaseManager get() {
    return newInstance(contextProvider.get(), knownTowerDaoProvider.get());
  }

  public static TowerDatabaseManager_Factory create(Provider<Context> contextProvider,
      Provider<KnownTowerDao> knownTowerDaoProvider) {
    return new TowerDatabaseManager_Factory(contextProvider, knownTowerDaoProvider);
  }

  public static TowerDatabaseManager newInstance(Context context, KnownTowerDao knownTowerDao) {
    return new TowerDatabaseManager(context, knownTowerDao);
  }
}
