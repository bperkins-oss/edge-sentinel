package com.bp22intel.edgesentinel.detection.wifi;

import com.bp22intel.edgesentinel.data.local.dao.TrustedNetworkDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class WifiThreatDetector_Factory implements Factory<WifiThreatDetector> {
  private final Provider<TrustedNetworkDao> trustedNetworkDaoProvider;

  public WifiThreatDetector_Factory(Provider<TrustedNetworkDao> trustedNetworkDaoProvider) {
    this.trustedNetworkDaoProvider = trustedNetworkDaoProvider;
  }

  @Override
  public WifiThreatDetector get() {
    return newInstance(trustedNetworkDaoProvider.get());
  }

  public static WifiThreatDetector_Factory create(
      Provider<TrustedNetworkDao> trustedNetworkDaoProvider) {
    return new WifiThreatDetector_Factory(trustedNetworkDaoProvider);
  }

  public static WifiThreatDetector newInstance(TrustedNetworkDao trustedNetworkDao) {
    return new WifiThreatDetector(trustedNetworkDao);
  }
}
