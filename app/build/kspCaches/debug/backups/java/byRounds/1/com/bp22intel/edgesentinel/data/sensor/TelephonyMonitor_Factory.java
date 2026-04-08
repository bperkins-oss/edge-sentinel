package com.bp22intel.edgesentinel.data.sensor;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class TelephonyMonitor_Factory implements Factory<TelephonyMonitor> {
  private final Provider<Context> contextProvider;

  public TelephonyMonitor_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public TelephonyMonitor get() {
    return newInstance(contextProvider.get());
  }

  public static TelephonyMonitor_Factory create(Provider<Context> contextProvider) {
    return new TelephonyMonitor_Factory(contextProvider);
  }

  public static TelephonyMonitor newInstance(Context context) {
    return new TelephonyMonitor(context);
  }
}
