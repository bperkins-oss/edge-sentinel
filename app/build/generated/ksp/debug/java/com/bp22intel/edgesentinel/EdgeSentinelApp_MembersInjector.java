package com.bp22intel.edgesentinel;

import androidx.hilt.work.HiltWorkerFactory;
import com.bp22intel.edgesentinel.detection.tower.TowerDatabaseManager;
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

  private final Provider<TowerDatabaseManager> towerDatabaseManagerProvider;

  public EdgeSentinelApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<TowerDatabaseManager> towerDatabaseManagerProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
    this.towerDatabaseManagerProvider = towerDatabaseManagerProvider;
  }

  public static MembersInjector<EdgeSentinelApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<TowerDatabaseManager> towerDatabaseManagerProvider) {
    return new EdgeSentinelApp_MembersInjector(workerFactoryProvider, towerDatabaseManagerProvider);
  }

  @Override
  public void injectMembers(EdgeSentinelApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
    injectTowerDatabaseManager(instance, towerDatabaseManagerProvider.get());
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.EdgeSentinelApp.workerFactory")
  public static void injectWorkerFactory(EdgeSentinelApp instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }

  @InjectedFieldSignature("com.bp22intel.edgesentinel.EdgeSentinelApp.towerDatabaseManager")
  public static void injectTowerDatabaseManager(EdgeSentinelApp instance,
      TowerDatabaseManager towerDatabaseManager) {
    instance.towerDatabaseManager = towerDatabaseManager;
  }
}
