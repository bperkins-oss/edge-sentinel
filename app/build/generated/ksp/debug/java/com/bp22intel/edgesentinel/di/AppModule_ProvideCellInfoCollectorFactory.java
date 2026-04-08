package com.bp22intel.edgesentinel.di;

import android.content.Context;
import com.bp22intel.edgesentinel.data.sensor.CellInfoCollector;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideCellInfoCollectorFactory implements Factory<CellInfoCollector> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideCellInfoCollectorFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CellInfoCollector get() {
    return provideCellInfoCollector(contextProvider.get());
  }

  public static AppModule_ProvideCellInfoCollectorFactory create(
      Provider<Context> contextProvider) {
    return new AppModule_ProvideCellInfoCollectorFactory(contextProvider);
  }

  public static CellInfoCollector provideCellInfoCollector(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideCellInfoCollector(context));
  }
}
