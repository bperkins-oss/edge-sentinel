package com.bp22intel.edgesentinel.data.sensor;

import android.content.Context;
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
public final class NrMonitor_Factory implements Factory<NrMonitor> {
  private final Provider<Context> contextProvider;

  public NrMonitor_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public NrMonitor get() {
    return newInstance(contextProvider.get());
  }

  public static NrMonitor_Factory create(Provider<Context> contextProvider) {
    return new NrMonitor_Factory(contextProvider);
  }

  public static NrMonitor newInstance(Context context) {
    return new NrMonitor(context);
  }
}
