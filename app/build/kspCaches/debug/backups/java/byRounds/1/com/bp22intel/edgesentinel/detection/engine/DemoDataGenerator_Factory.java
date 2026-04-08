package com.bp22intel.edgesentinel.detection.engine;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class DemoDataGenerator_Factory implements Factory<DemoDataGenerator> {
  @Override
  public DemoDataGenerator get() {
    return newInstance();
  }

  public static DemoDataGenerator_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DemoDataGenerator newInstance() {
    return new DemoDataGenerator();
  }

  private static final class InstanceHolder {
    private static final DemoDataGenerator_Factory INSTANCE = new DemoDataGenerator_Factory();
  }
}
