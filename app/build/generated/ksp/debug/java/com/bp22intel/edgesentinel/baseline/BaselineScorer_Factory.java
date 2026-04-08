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
public final class BaselineScorer_Factory implements Factory<BaselineScorer> {
  @Override
  public BaselineScorer get() {
    return newInstance();
  }

  public static BaselineScorer_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BaselineScorer newInstance() {
    return new BaselineScorer();
  }

  private static final class InstanceHolder {
    private static final BaselineScorer_Factory INSTANCE = new BaselineScorer_Factory();
  }
}
