package com.bp22intel.edgesentinel.baseline;

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
public final class BaselineLearner_Factory implements Factory<BaselineLearner> {
  @Override
  public BaselineLearner get() {
    return newInstance();
  }

  public static BaselineLearner_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BaselineLearner newInstance() {
    return new BaselineLearner();
  }

  private static final class InstanceHolder {
    private static final BaselineLearner_Factory INSTANCE = new BaselineLearner_Factory();
  }
}
