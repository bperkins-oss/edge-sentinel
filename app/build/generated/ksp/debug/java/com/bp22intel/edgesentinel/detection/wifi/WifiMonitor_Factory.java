package com.bp22intel.edgesentinel.detection.wifi;

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
public final class WifiMonitor_Factory implements Factory<WifiMonitor> {
  private final Provider<Context> contextProvider;

  public WifiMonitor_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public WifiMonitor get() {
    return newInstance(contextProvider.get());
  }

  public static WifiMonitor_Factory create(Provider<Context> contextProvider) {
    return new WifiMonitor_Factory(contextProvider);
  }

  public static WifiMonitor newInstance(Context context) {
    return new WifiMonitor(context);
  }
}
