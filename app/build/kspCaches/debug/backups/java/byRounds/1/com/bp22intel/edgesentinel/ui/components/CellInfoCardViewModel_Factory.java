package com.bp22intel.edgesentinel.ui.components;

import com.bp22intel.edgesentinel.detection.tower.TowerVerifier;
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
public final class CellInfoCardViewModel_Factory implements Factory<CellInfoCardViewModel> {
  private final Provider<TowerVerifier> towerVerifierProvider;

  public CellInfoCardViewModel_Factory(Provider<TowerVerifier> towerVerifierProvider) {
    this.towerVerifierProvider = towerVerifierProvider;
  }

  @Override
  public CellInfoCardViewModel get() {
    return newInstance(towerVerifierProvider.get());
  }

  public static CellInfoCardViewModel_Factory create(
      Provider<TowerVerifier> towerVerifierProvider) {
    return new CellInfoCardViewModel_Factory(towerVerifierProvider);
  }

  public static CellInfoCardViewModel newInstance(TowerVerifier towerVerifier) {
    return new CellInfoCardViewModel(towerVerifier);
  }
}
