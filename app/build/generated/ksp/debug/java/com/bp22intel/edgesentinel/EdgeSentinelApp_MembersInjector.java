package com.bp22intel.edgesentinel;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class EdgeSentinelApp_MembersInjector implements MembersInjector<EdgeSentinelApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public EdgeSentinelApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<EdgeSentinelApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new EdgeSentinelApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(EdgeSentinelApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.EdgeSentinelApp.workerFactory")
  public static void injectWorkerFactory(EdgeSentinelApp instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
