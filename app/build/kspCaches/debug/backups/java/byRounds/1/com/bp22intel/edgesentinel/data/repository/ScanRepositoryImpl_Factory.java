package com.bp22intel.edgesentinel.data.repository;

import com.bp22intel.edgesentinel.data.local.dao.ScanDao;
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
public final class ScanRepositoryImpl_Factory implements Factory<ScanRepositoryImpl> {
  private final Provider<ScanDao> scanDaoProvider;

  public ScanRepositoryImpl_Factory(Provider<ScanDao> scanDaoProvider) {
    this.scanDaoProvider = scanDaoProvider;
  }

  @Override
  public ScanRepositoryImpl get() {
    return newInstance(scanDaoProvider.get());
  }

  public static ScanRepositoryImpl_Factory create(Provider<ScanDao> scanDaoProvider) {
    return new ScanRepositoryImpl_Factory(scanDaoProvider);
  }

  public static ScanRepositoryImpl newInstance(ScanDao scanDao) {
    return new ScanRepositoryImpl(scanDao);
  }
}
