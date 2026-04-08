package com.bp22intel.edgesentinel.calibration;

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
public final class CalibrationService_Factory implements Factory<CalibrationService> {
  private final Provider<Context> contextProvider;

  public CalibrationService_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CalibrationService get() {
    return newInstance(contextProvider.get());
  }

  public static CalibrationService_Factory create(Provider<Context> contextProvider) {
    return new CalibrationService_Factory(contextProvider);
  }

  public static CalibrationService newInstance(Context context) {
    return new CalibrationService(context);
  }
}
