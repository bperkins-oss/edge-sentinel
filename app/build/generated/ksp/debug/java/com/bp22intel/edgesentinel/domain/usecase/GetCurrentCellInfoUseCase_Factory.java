package com.bp22intel.edgesentinel.domain.usecase;

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
public final class GetCurrentCellInfoUseCase_Factory implements Factory<GetCurrentCellInfoUseCase> {
  private final Provider<CellRepository> cellRepositoryProvider;

  public GetCurrentCellInfoUseCase_Factory(Provider<CellRepository> cellRepositoryProvider) {
    this.cellRepositoryProvider = cellRepositoryProvider;
  }

  @Override
  public GetCurrentCellInfoUseCase get() {
    return newInstance(cellRepositoryProvider.get());
  }

  public static GetCurrentCellInfoUseCase_Factory create(
      Provider<CellRepository> cellRepositoryProvider) {
    return new GetCurrentCellInfoUseCase_Factory(cellRepositoryProvider);
  }

  public static GetCurrentCellInfoUseCase newInstance(CellRepository cellRepository) {
    return new GetCurrentCellInfoUseCase(cellRepository);
  }
}
