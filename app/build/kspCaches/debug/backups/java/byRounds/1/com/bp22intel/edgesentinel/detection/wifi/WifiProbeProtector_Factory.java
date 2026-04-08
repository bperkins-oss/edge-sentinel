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
public final class WifiProbeProtector_Factory implements Factory<WifiProbeProtector> {
  private final Provider<Context> contextProvider;

  public WifiProbeProtector_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public WifiProbeProtector get() {
    return newInstance(contextProvider.get());
  }

  public static WifiProbeProtector_Factory create(Provider<Context> contextProvider) {
    return new WifiProbeProtector_Factory(contextProvider);
  }

  public static WifiProbeProtector newInstance(Context context) {
    return new WifiProbeProtector(context);
  }
}
