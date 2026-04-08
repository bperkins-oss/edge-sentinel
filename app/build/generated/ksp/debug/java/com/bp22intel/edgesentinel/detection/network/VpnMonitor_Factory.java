package com.bp22intel.edgesentinel.detection.network;

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
public final class VpnMonitor_Factory implements Factory<VpnMonitor> {
  private final Provider<Context> contextProvider;

  public VpnMonitor_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public VpnMonitor get() {
    return newInstance(contextProvider.get());
  }

  public static VpnMonitor_Factory create(Provider<Context> contextProvider) {
    return new VpnMonitor_Factory(contextProvider);
  }

  public static VpnMonitor newInstance(Context context) {
    return new VpnMonitor(context);
  }
}
