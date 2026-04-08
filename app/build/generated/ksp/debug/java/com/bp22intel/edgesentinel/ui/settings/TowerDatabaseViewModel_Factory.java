package com.bp22intel.edgesentinel.ui.settings;

import android.content.Context;
import com.bp22intel.edgesentinel.detection.tower.TowerDatabaseManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class TowerDatabaseViewModel_Factory implements Factory<TowerDatabaseViewModel> {
  private final Provider<TowerDatabaseManager> towerDatabaseManagerProvider;

  private final Provider<Context> contextProvider;

  public TowerDatabaseViewModel_Factory(Provider<TowerDatabaseManager> towerDatabaseManagerProvider,
      Provider<Context> contextProvider) {
    this.towerDatabaseManagerProvider = towerDatabaseManagerProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public TowerDatabaseViewModel get() {
    return newInstance(towerDatabaseManagerProvider.get(), contextProvider.get());
  }

  public static TowerDatabaseViewModel_Factory create(
      Provider<TowerDatabaseManager> towerDatabaseManagerProvider,
      Provider<Context> contextProvider) {
    return new TowerDatabaseViewModel_Factory(towerDatabaseManagerProvider, contextProvider);
  }

  public static TowerDatabaseViewModel newInstance(TowerDatabaseManager towerDatabaseManager,
      Context context) {
    return new TowerDatabaseViewModel(towerDatabaseManager, context);
  }
}
