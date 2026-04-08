package com.bp22intel.edgesentinel.detection.tower;

import com.bp22intel.edgesentinel.data.local.dao.KnownTowerDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("dagger.hilt.android.scopes.ActivityRetainedScoped")
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
public final class TowerVerifier_Factory implements Factory<TowerVerifier> {
  private final Provider<KnownTowerDao> knownTowerDaoProvider;

  public TowerVerifier_Factory(Provider<KnownTowerDao> knownTowerDaoProvider) {
    this.knownTowerDaoProvider = knownTowerDaoProvider;
  }

  @Override
  public TowerVerifier get() {
    return newInstance(knownTowerDaoProvider.get());
  }

  public static TowerVerifier_Factory create(Provider<KnownTowerDao> knownTowerDaoProvider) {
    return new TowerVerifier_Factory(knownTowerDaoProvider);
  }

  public static TowerVerifier newInstance(KnownTowerDao knownTowerDao) {
    return new TowerVerifier(knownTowerDao);
  }
}
