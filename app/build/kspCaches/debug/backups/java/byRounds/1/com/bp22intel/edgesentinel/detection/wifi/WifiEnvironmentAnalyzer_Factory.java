package com.bp22intel.edgesentinel.detection.wifi;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class WifiEnvironmentAnalyzer_Factory implements Factory<WifiEnvironmentAnalyzer> {
  @Override
  public WifiEnvironmentAnalyzer get() {
    return newInstance();
  }

  public static WifiEnvironmentAnalyzer_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static WifiEnvironmentAnalyzer newInstance() {
    return new WifiEnvironmentAnalyzer();
  }

  private static final class InstanceHolder {
    private static final WifiEnvironmentAnalyzer_Factory INSTANCE = new WifiEnvironmentAnalyzer_Factory();
  }
}
