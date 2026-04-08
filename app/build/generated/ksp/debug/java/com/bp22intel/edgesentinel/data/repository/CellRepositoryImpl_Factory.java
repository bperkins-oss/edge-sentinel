package com.bp22intel.edgesentinel.data.repository;

import com.bp22intel.edgesentinel.data.local.dao.CellDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class CellRepositoryImpl_Factory implements Factory<CellRepositoryImpl> {
  private final Provider<CellDao> cellDaoProvider;

  public CellRepositoryImpl_Factory(Provider<CellDao> cellDaoProvider) {
    this.cellDaoProvider = cellDaoProvider;
  }

  @Override
  public CellRepositoryImpl get() {
    return newInstance(cellDaoProvider.get());
  }

  public static CellRepositoryImpl_Factory create(Provider<CellDao> cellDaoProvider) {
    return new CellRepositoryImpl_Factory(cellDaoProvider);
  }

  public static CellRepositoryImpl newInstance(CellDao cellDao) {
    return new CellRepositoryImpl(cellDao);
  }
}
