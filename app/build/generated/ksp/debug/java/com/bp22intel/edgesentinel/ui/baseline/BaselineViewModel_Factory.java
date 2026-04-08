package com.bp22intel.edgesentinel.ui.baseline;

import com.bp22intel.edgesentinel.baseline.BaselineManager;
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
public final class BaselineViewModel_Factory implements Factory<BaselineViewModel> {
  private final Provider<BaselineManager> baselineManagerProvider;

  public BaselineViewModel_Factory(Provider<BaselineManager> baselineManagerProvider) {
    this.baselineManagerProvider = baselineManagerProvider;
  }

  @Override
  public BaselineViewModel get() {
    return newInstance(baselineManagerProvider.get());
  }

  public static BaselineViewModel_Factory create(
      Provider<BaselineManager> baselineManagerProvider) {
    return new BaselineViewModel_Factory(baselineManagerProvider);
  }

  public static BaselineViewModel newInstance(BaselineManager baselineManager) {
    return new BaselineViewModel(baselineManager);
  }
}
