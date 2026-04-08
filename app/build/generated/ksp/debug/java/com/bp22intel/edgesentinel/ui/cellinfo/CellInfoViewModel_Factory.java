package com.bp22intel.edgesentinel.ui.cellinfo;

import com.bp22intel.edgesentinel.data.sensor.CellInfoCollector;
import com.bp22intel.edgesentinel.domain.repository.CellRepository;
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
public final class CellInfoViewModel_Factory implements Factory<CellInfoViewModel> {
  private final Provider<CellRepository> cellRepositoryProvider;

  private final Provider<CellInfoCollector> cellInfoCollectorProvider;

  public CellInfoViewModel_Factory(Provider<CellRepository> cellRepositoryProvider,
      Provider<CellInfoCollector> cellInfoCollectorProvider) {
    this.cellRepositoryProvider = cellRepositoryProvider;
    this.cellInfoCollectorProvider = cellInfoCollectorProvider;
  }

  @Override
  public CellInfoViewModel get() {
    return newInstance(cellRepositoryProvider.get(), cellInfoCollectorProvider.get());
  }

  public static CellInfoViewModel_Factory create(Provider<CellRepository> cellRepositoryProvider,
      Provider<CellInfoCollector> cellInfoCollectorProvider) {
    return new CellInfoViewModel_Factory(cellRepositoryProvider, cellInfoCollectorProvider);
  }

  public static CellInfoViewModel newInstance(CellRepository cellRepository,
      CellInfoCollector cellInfoCollector) {
    return new CellInfoViewModel(cellRepository, cellInfoCollector);
  }
}
