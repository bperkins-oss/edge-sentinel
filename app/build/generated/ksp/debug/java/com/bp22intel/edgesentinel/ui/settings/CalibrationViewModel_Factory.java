package com.bp22intel.edgesentinel.ui.settings;

import com.bp22intel.edgesentinel.calibration.CalibrationService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class CalibrationViewModel_Factory implements Factory<CalibrationViewModel> {
  private final Provider<CalibrationService> calibrationServiceProvider;

  public CalibrationViewModel_Factory(Provider<CalibrationService> calibrationServiceProvider) {
    this.calibrationServiceProvider = calibrationServiceProvider;
  }

  @Override
  public CalibrationViewModel get() {
    return newInstance(calibrationServiceProvider.get());
  }

  public static CalibrationViewModel_Factory create(
      Provider<CalibrationService> calibrationServiceProvider) {
    return new CalibrationViewModel_Factory(calibrationServiceProvider);
  }

  public static CalibrationViewModel newInstance(CalibrationService calibrationService) {
    return new CalibrationViewModel(calibrationService);
  }
}
