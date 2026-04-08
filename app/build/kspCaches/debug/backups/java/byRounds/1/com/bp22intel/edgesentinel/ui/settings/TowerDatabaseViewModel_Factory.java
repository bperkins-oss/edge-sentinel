package com.bp22intel.edgesentinel.ui.settings;

import com.bp22intel.edgesentinel.detection.tower.TowerDatabaseManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class TowerDatabaseViewModel_Factory implements Factory<TowerDatabaseViewModel> {
  private final Provider<TowerDatabaseManager> towerDatabaseManagerProvider;

  public TowerDatabaseViewModel_Factory(
      Provider<TowerDatabaseManager> towerDatabaseManagerProvider) {
    this.towerDatabaseManagerProvider = towerDatabaseManagerProvider;
  }

  @Override
  public TowerDatabaseViewModel get() {
    return newInstance(towerDatabaseManagerProvider.get());
  }

  public static TowerDatabaseViewModel_Factory create(
      Provider<TowerDatabaseManager> towerDatabaseManagerProvider) {
    return new TowerDatabaseViewModel_Factory(towerDatabaseManagerProvider);
  }

  public static TowerDatabaseViewModel newInstance(TowerDatabaseManager towerDatabaseManager) {
    return new TowerDatabaseViewModel(towerDatabaseManager);
  }
}
