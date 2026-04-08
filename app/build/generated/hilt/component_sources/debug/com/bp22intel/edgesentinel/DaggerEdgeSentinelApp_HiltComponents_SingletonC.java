package com.bp22intel.edgesentinel;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.bp22intel.edgesentinel.baseline.BaselineLearner;
import com.bp22intel.edgesentinel.baseline.BaselineManager;
import com.bp22intel.edgesentinel.baseline.BaselineScorer;
import com.bp22intel.edgesentinel.calibration.CalibrationService;
import com.bp22intel.edgesentinel.data.local.EdgeSentinelDatabase;
import com.bp22intel.edgesentinel.data.local.dao.AlertDao;
import com.bp22intel.edgesentinel.data.local.dao.BaselineDao;
import com.bp22intel.edgesentinel.data.local.dao.BleDeviceDao;
import com.bp22intel.edgesentinel.data.local.dao.CellDao;
import com.bp22intel.edgesentinel.data.local.dao.KnownTowerDao;
import com.bp22intel.edgesentinel.data.local.dao.ScanDao;
import com.bp22intel.edgesentinel.data.local.dao.TrustedNetworkDao;
import com.bp22intel.edgesentinel.data.repository.AlertRepositoryImpl;
import com.bp22intel.edgesentinel.data.repository.CellRepositoryImpl;
import com.bp22intel.edgesentinel.data.repository.ScanRepositoryImpl;
import com.bp22intel.edgesentinel.data.sensor.CellInfoCollector;
import com.bp22intel.edgesentinel.data.sensor.NrMonitor;
import com.bp22intel.edgesentinel.data.sensor.TelephonyMonitor;
import com.bp22intel.edgesentinel.detection.bluetooth.BleAlertManager;
import com.bp22intel.edgesentinel.detection.bluetooth.BleDeviceTracker;
import com.bp22intel.edgesentinel.detection.bluetooth.BleTrackerIdentifier;
import com.bp22intel.edgesentinel.detection.bluetooth.BleTrackingDetector;
import com.bp22intel.edgesentinel.detection.detectors.CipherModeDetector;
import com.bp22intel.edgesentinel.detection.detectors.DiagBasedDetector;
import com.bp22intel.edgesentinel.detection.detectors.FakeBtsDetector;
import com.bp22intel.edgesentinel.detection.detectors.NetworkDowngradeDetector;
import com.bp22intel.edgesentinel.detection.detectors.NrDetector;
import com.bp22intel.edgesentinel.detection.detectors.SilentSmsDetector;
import com.bp22intel.edgesentinel.detection.detectors.ThreatDetector;
import com.bp22intel.edgesentinel.detection.detectors.TrackingPatternDetector;
import com.bp22intel.edgesentinel.detection.engine.DemoDataGenerator;
import com.bp22intel.edgesentinel.detection.engine.ThreatDetectionEngine;
import com.bp22intel.edgesentinel.detection.geo.ThreatGeolocation;
import com.bp22intel.edgesentinel.detection.network.CaptivePortalDetector;
import com.bp22intel.edgesentinel.detection.network.DnsIntegrityChecker;
import com.bp22intel.edgesentinel.detection.network.TlsIntegrityChecker;
import com.bp22intel.edgesentinel.detection.network.VpnMonitor;
import com.bp22intel.edgesentinel.detection.scoring.ThreatScorer;
import com.bp22intel.edgesentinel.detection.tower.TowerDatabaseManager;
import com.bp22intel.edgesentinel.detection.tower.TowerVerifier;
import com.bp22intel.edgesentinel.detection.wifi.WifiEnvironmentAnalyzer;
import com.bp22intel.edgesentinel.detection.wifi.WifiMonitor;
import com.bp22intel.edgesentinel.detection.wifi.WifiProbeProtector;
import com.bp22intel.edgesentinel.detection.wifi.WifiThreatDetector;
import com.bp22intel.edgesentinel.di.AppModule_ProvideAlertDaoFactory;
import com.bp22intel.edgesentinel.di.AppModule_ProvideBaselineDaoFactory;
import com.bp22intel.edgesentinel.di.AppModule_ProvideBleDeviceDaoFactory;
import com.bp22intel.edgesentinel.di.AppModule_ProvideCellDaoFactory;
import com.bp22intel.edgesentinel.di.AppModule_ProvideCellInfoCollectorFactory;
import com.bp22intel.edgesentinel.di.AppModule_ProvideDataStoreFactory;
import com.bp22intel.edgesentinel.di.AppModule_ProvideDatabaseFactory;
import com.bp22intel.edgesentinel.di.AppModule_ProvideKnownTowerDaoFactory;
import com.bp22intel.edgesentinel.di.AppModule_ProvideScanDaoFactory;
import com.bp22intel.edgesentinel.di.AppModule_ProvideTrustedNetworkDaoFactory;
import com.bp22intel.edgesentinel.diag.DiagBridge;
import com.bp22intel.edgesentinel.diag.DiagMessageParser;
import com.bp22intel.edgesentinel.diag.RootChecker;
import com.bp22intel.edgesentinel.fusion.OverallThreatDashboard;
import com.bp22intel.edgesentinel.fusion.SensorFusionEngine;
import com.bp22intel.edgesentinel.fusion.ThreatNarrator;
import com.bp22intel.edgesentinel.mesh.MeshViewModel;
import com.bp22intel.edgesentinel.mesh.MeshViewModel_HiltModules;
import com.bp22intel.edgesentinel.service.MonitoringService;
import com.bp22intel.edgesentinel.service.MonitoringService_MembersInjector;
import com.bp22intel.edgesentinel.service.ScanWorker;
import com.bp22intel.edgesentinel.service.ScanWorker_AssistedFactory;
import com.bp22intel.edgesentinel.ui.alerts.AlertDetailViewModel;
import com.bp22intel.edgesentinel.ui.alerts.AlertDetailViewModel_HiltModules;
import com.bp22intel.edgesentinel.ui.alerts.AlertsViewModel;
import com.bp22intel.edgesentinel.ui.alerts.AlertsViewModel_HiltModules;
import com.bp22intel.edgesentinel.ui.baseline.BaselineViewModel;
import com.bp22intel.edgesentinel.ui.baseline.BaselineViewModel_HiltModules;
import com.bp22intel.edgesentinel.ui.bluetooth.BluetoothViewModel;
import com.bp22intel.edgesentinel.ui.bluetooth.BluetoothViewModel_HiltModules;
import com.bp22intel.edgesentinel.ui.cellinfo.CellInfoViewModel;
import com.bp22intel.edgesentinel.ui.cellinfo.CellInfoViewModel_HiltModules;
import com.bp22intel.edgesentinel.ui.components.CellInfoCardViewModel;
import com.bp22intel.edgesentinel.ui.components.CellInfoCardViewModel_HiltModules;
import com.bp22intel.edgesentinel.ui.dashboard.DashboardViewModel;
import com.bp22intel.edgesentinel.ui.dashboard.DashboardViewModel_HiltModules;
import com.bp22intel.edgesentinel.ui.map.ThreatMapViewModel;
import com.bp22intel.edgesentinel.ui.map.ThreatMapViewModel_HiltModules;
import com.bp22intel.edgesentinel.ui.network.NetworkIntegrityViewModel;
import com.bp22intel.edgesentinel.ui.network.NetworkIntegrityViewModel_HiltModules;
import com.bp22intel.edgesentinel.ui.onboarding.OnboardingViewModel;
import com.bp22intel.edgesentinel.ui.onboarding.OnboardingViewModel_HiltModules;
import com.bp22intel.edgesentinel.ui.settings.CalibrationViewModel;
import com.bp22intel.edgesentinel.ui.settings.CalibrationViewModel_HiltModules;
import com.bp22intel.edgesentinel.ui.settings.SettingsViewModel;
import com.bp22intel.edgesentinel.ui.settings.SettingsViewModel_HiltModules;
import com.bp22intel.edgesentinel.ui.settings.TowerDatabaseViewModel;
import com.bp22intel.edgesentinel.ui.settings.TowerDatabaseViewModel_HiltModules;
import com.bp22intel.edgesentinel.ui.wifi.WifiViewModel;
import com.bp22intel.edgesentinel.ui.wifi.WifiViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SetBuilder;
import dagger.internal.SingleCheck;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerEdgeSentinelApp_HiltComponents_SingletonC {
  private DaggerEdgeSentinelApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public EdgeSentinelApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements EdgeSentinelApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public EdgeSentinelApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements EdgeSentinelApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public EdgeSentinelApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements EdgeSentinelApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public EdgeSentinelApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements EdgeSentinelApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public EdgeSentinelApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements EdgeSentinelApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public EdgeSentinelApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements EdgeSentinelApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public EdgeSentinelApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements EdgeSentinelApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public EdgeSentinelApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends EdgeSentinelApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends EdgeSentinelApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends EdgeSentinelApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends EdgeSentinelApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(15).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_alerts_AlertDetailViewModel, AlertDetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_alerts_AlertsViewModel, AlertsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_baseline_BaselineViewModel, BaselineViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_bluetooth_BluetoothViewModel, BluetoothViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_settings_CalibrationViewModel, CalibrationViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_components_CellInfoCardViewModel, CellInfoCardViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_cellinfo_CellInfoViewModel, CellInfoViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_dashboard_DashboardViewModel, DashboardViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_mesh_MeshViewModel, MeshViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_network_NetworkIntegrityViewModel, NetworkIntegrityViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_onboarding_OnboardingViewModel, OnboardingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_settings_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_map_ThreatMapViewModel, ThreatMapViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_settings_TowerDatabaseViewModel, TowerDatabaseViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_wifi_WifiViewModel, WifiViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_bp22intel_edgesentinel_ui_alerts_AlertDetailViewModel = "com.bp22intel.edgesentinel.ui.alerts.AlertDetailViewModel";

      static String com_bp22intel_edgesentinel_ui_cellinfo_CellInfoViewModel = "com.bp22intel.edgesentinel.ui.cellinfo.CellInfoViewModel";

      static String com_bp22intel_edgesentinel_ui_settings_SettingsViewModel = "com.bp22intel.edgesentinel.ui.settings.SettingsViewModel";

      static String com_bp22intel_edgesentinel_ui_bluetooth_BluetoothViewModel = "com.bp22intel.edgesentinel.ui.bluetooth.BluetoothViewModel";

      static String com_bp22intel_edgesentinel_mesh_MeshViewModel = "com.bp22intel.edgesentinel.mesh.MeshViewModel";

      static String com_bp22intel_edgesentinel_ui_components_CellInfoCardViewModel = "com.bp22intel.edgesentinel.ui.components.CellInfoCardViewModel";

      static String com_bp22intel_edgesentinel_ui_settings_CalibrationViewModel = "com.bp22intel.edgesentinel.ui.settings.CalibrationViewModel";

      static String com_bp22intel_edgesentinel_ui_network_NetworkIntegrityViewModel = "com.bp22intel.edgesentinel.ui.network.NetworkIntegrityViewModel";

      static String com_bp22intel_edgesentinel_ui_onboarding_OnboardingViewModel = "com.bp22intel.edgesentinel.ui.onboarding.OnboardingViewModel";

      static String com_bp22intel_edgesentinel_ui_dashboard_DashboardViewModel = "com.bp22intel.edgesentinel.ui.dashboard.DashboardViewModel";

      static String com_bp22intel_edgesentinel_ui_alerts_AlertsViewModel = "com.bp22intel.edgesentinel.ui.alerts.AlertsViewModel";

      static String com_bp22intel_edgesentinel_ui_map_ThreatMapViewModel = "com.bp22intel.edgesentinel.ui.map.ThreatMapViewModel";

      static String com_bp22intel_edgesentinel_ui_baseline_BaselineViewModel = "com.bp22intel.edgesentinel.ui.baseline.BaselineViewModel";

      static String com_bp22intel_edgesentinel_ui_wifi_WifiViewModel = "com.bp22intel.edgesentinel.ui.wifi.WifiViewModel";

      static String com_bp22intel_edgesentinel_ui_settings_TowerDatabaseViewModel = "com.bp22intel.edgesentinel.ui.settings.TowerDatabaseViewModel";

      @KeepFieldType
      AlertDetailViewModel com_bp22intel_edgesentinel_ui_alerts_AlertDetailViewModel2;

      @KeepFieldType
      CellInfoViewModel com_bp22intel_edgesentinel_ui_cellinfo_CellInfoViewModel2;

      @KeepFieldType
      SettingsViewModel com_bp22intel_edgesentinel_ui_settings_SettingsViewModel2;

      @KeepFieldType
      BluetoothViewModel com_bp22intel_edgesentinel_ui_bluetooth_BluetoothViewModel2;

      @KeepFieldType
      MeshViewModel com_bp22intel_edgesentinel_mesh_MeshViewModel2;

      @KeepFieldType
      CellInfoCardViewModel com_bp22intel_edgesentinel_ui_components_CellInfoCardViewModel2;

      @KeepFieldType
      CalibrationViewModel com_bp22intel_edgesentinel_ui_settings_CalibrationViewModel2;

      @KeepFieldType
      NetworkIntegrityViewModel com_bp22intel_edgesentinel_ui_network_NetworkIntegrityViewModel2;

      @KeepFieldType
      OnboardingViewModel com_bp22intel_edgesentinel_ui_onboarding_OnboardingViewModel2;

      @KeepFieldType
      DashboardViewModel com_bp22intel_edgesentinel_ui_dashboard_DashboardViewModel2;

      @KeepFieldType
      AlertsViewModel com_bp22intel_edgesentinel_ui_alerts_AlertsViewModel2;

      @KeepFieldType
      ThreatMapViewModel com_bp22intel_edgesentinel_ui_map_ThreatMapViewModel2;

      @KeepFieldType
      BaselineViewModel com_bp22intel_edgesentinel_ui_baseline_BaselineViewModel2;

      @KeepFieldType
      WifiViewModel com_bp22intel_edgesentinel_ui_wifi_WifiViewModel2;

      @KeepFieldType
      TowerDatabaseViewModel com_bp22intel_edgesentinel_ui_settings_TowerDatabaseViewModel2;
    }
  }

  private static final class ViewModelCImpl extends EdgeSentinelApp_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AlertDetailViewModel> alertDetailViewModelProvider;

    private Provider<AlertsViewModel> alertsViewModelProvider;

    private Provider<BaselineViewModel> baselineViewModelProvider;

    private Provider<BluetoothViewModel> bluetoothViewModelProvider;

    private Provider<CalibrationViewModel> calibrationViewModelProvider;

    private Provider<CellInfoCardViewModel> cellInfoCardViewModelProvider;

    private Provider<CellInfoViewModel> cellInfoViewModelProvider;

    private Provider<DashboardViewModel> dashboardViewModelProvider;

    private Provider<MeshViewModel> meshViewModelProvider;

    private Provider<NetworkIntegrityViewModel> networkIntegrityViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<ThreatMapViewModel> threatMapViewModelProvider;

    private Provider<TowerDatabaseViewModel> towerDatabaseViewModelProvider;

    private Provider<WifiViewModel> wifiViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.alertDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.alertsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.baselineViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.bluetoothViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.calibrationViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.cellInfoCardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.cellInfoViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.meshViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.networkIntegrityViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 11);
      this.threatMapViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 12);
      this.towerDatabaseViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 13);
      this.wifiViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 14);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(15).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_alerts_AlertDetailViewModel, ((Provider) alertDetailViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_alerts_AlertsViewModel, ((Provider) alertsViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_baseline_BaselineViewModel, ((Provider) baselineViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_bluetooth_BluetoothViewModel, ((Provider) bluetoothViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_settings_CalibrationViewModel, ((Provider) calibrationViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_components_CellInfoCardViewModel, ((Provider) cellInfoCardViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_cellinfo_CellInfoViewModel, ((Provider) cellInfoViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_dashboard_DashboardViewModel, ((Provider) dashboardViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_mesh_MeshViewModel, ((Provider) meshViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_network_NetworkIntegrityViewModel, ((Provider) networkIntegrityViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_onboarding_OnboardingViewModel, ((Provider) onboardingViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_settings_SettingsViewModel, ((Provider) settingsViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_map_ThreatMapViewModel, ((Provider) threatMapViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_settings_TowerDatabaseViewModel, ((Provider) towerDatabaseViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_wifi_WifiViewModel, ((Provider) wifiViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_bp22intel_edgesentinel_ui_alerts_AlertsViewModel = "com.bp22intel.edgesentinel.ui.alerts.AlertsViewModel";

      static String com_bp22intel_edgesentinel_ui_components_CellInfoCardViewModel = "com.bp22intel.edgesentinel.ui.components.CellInfoCardViewModel";

      static String com_bp22intel_edgesentinel_ui_dashboard_DashboardViewModel = "com.bp22intel.edgesentinel.ui.dashboard.DashboardViewModel";

      static String com_bp22intel_edgesentinel_ui_onboarding_OnboardingViewModel = "com.bp22intel.edgesentinel.ui.onboarding.OnboardingViewModel";

      static String com_bp22intel_edgesentinel_ui_alerts_AlertDetailViewModel = "com.bp22intel.edgesentinel.ui.alerts.AlertDetailViewModel";

      static String com_bp22intel_edgesentinel_ui_settings_SettingsViewModel = "com.bp22intel.edgesentinel.ui.settings.SettingsViewModel";

      static String com_bp22intel_edgesentinel_ui_map_ThreatMapViewModel = "com.bp22intel.edgesentinel.ui.map.ThreatMapViewModel";

      static String com_bp22intel_edgesentinel_ui_settings_CalibrationViewModel = "com.bp22intel.edgesentinel.ui.settings.CalibrationViewModel";

      static String com_bp22intel_edgesentinel_ui_settings_TowerDatabaseViewModel = "com.bp22intel.edgesentinel.ui.settings.TowerDatabaseViewModel";

      static String com_bp22intel_edgesentinel_ui_wifi_WifiViewModel = "com.bp22intel.edgesentinel.ui.wifi.WifiViewModel";

      static String com_bp22intel_edgesentinel_mesh_MeshViewModel = "com.bp22intel.edgesentinel.mesh.MeshViewModel";

      static String com_bp22intel_edgesentinel_ui_cellinfo_CellInfoViewModel = "com.bp22intel.edgesentinel.ui.cellinfo.CellInfoViewModel";

      static String com_bp22intel_edgesentinel_ui_network_NetworkIntegrityViewModel = "com.bp22intel.edgesentinel.ui.network.NetworkIntegrityViewModel";

      static String com_bp22intel_edgesentinel_ui_bluetooth_BluetoothViewModel = "com.bp22intel.edgesentinel.ui.bluetooth.BluetoothViewModel";

      static String com_bp22intel_edgesentinel_ui_baseline_BaselineViewModel = "com.bp22intel.edgesentinel.ui.baseline.BaselineViewModel";

      @KeepFieldType
      AlertsViewModel com_bp22intel_edgesentinel_ui_alerts_AlertsViewModel2;

      @KeepFieldType
      CellInfoCardViewModel com_bp22intel_edgesentinel_ui_components_CellInfoCardViewModel2;

      @KeepFieldType
      DashboardViewModel com_bp22intel_edgesentinel_ui_dashboard_DashboardViewModel2;

      @KeepFieldType
      OnboardingViewModel com_bp22intel_edgesentinel_ui_onboarding_OnboardingViewModel2;

      @KeepFieldType
      AlertDetailViewModel com_bp22intel_edgesentinel_ui_alerts_AlertDetailViewModel2;

      @KeepFieldType
      SettingsViewModel com_bp22intel_edgesentinel_ui_settings_SettingsViewModel2;

      @KeepFieldType
      ThreatMapViewModel com_bp22intel_edgesentinel_ui_map_ThreatMapViewModel2;

      @KeepFieldType
      CalibrationViewModel com_bp22intel_edgesentinel_ui_settings_CalibrationViewModel2;

      @KeepFieldType
      TowerDatabaseViewModel com_bp22intel_edgesentinel_ui_settings_TowerDatabaseViewModel2;

      @KeepFieldType
      WifiViewModel com_bp22intel_edgesentinel_ui_wifi_WifiViewModel2;

      @KeepFieldType
      MeshViewModel com_bp22intel_edgesentinel_mesh_MeshViewModel2;

      @KeepFieldType
      CellInfoViewModel com_bp22intel_edgesentinel_ui_cellinfo_CellInfoViewModel2;

      @KeepFieldType
      NetworkIntegrityViewModel com_bp22intel_edgesentinel_ui_network_NetworkIntegrityViewModel2;

      @KeepFieldType
      BluetoothViewModel com_bp22intel_edgesentinel_ui_bluetooth_BluetoothViewModel2;

      @KeepFieldType
      BaselineViewModel com_bp22intel_edgesentinel_ui_baseline_BaselineViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.bp22intel.edgesentinel.ui.alerts.AlertDetailViewModel 
          return (T) new AlertDetailViewModel(singletonCImpl.alertRepositoryImplProvider.get(), viewModelCImpl.savedStateHandle);

          case 1: // com.bp22intel.edgesentinel.ui.alerts.AlertsViewModel 
          return (T) new AlertsViewModel(singletonCImpl.alertRepositoryImplProvider.get());

          case 2: // com.bp22intel.edgesentinel.ui.baseline.BaselineViewModel 
          return (T) new BaselineViewModel(singletonCImpl.baselineManagerProvider.get());

          case 3: // com.bp22intel.edgesentinel.ui.bluetooth.BluetoothViewModel 
          return (T) new BluetoothViewModel(singletonCImpl.bleTrackingDetectorProvider.get(), singletonCImpl.bleDeviceTrackerProvider.get(), singletonCImpl.bleAlertManagerProvider.get());

          case 4: // com.bp22intel.edgesentinel.ui.settings.CalibrationViewModel 
          return (T) new CalibrationViewModel(singletonCImpl.calibrationServiceProvider.get());

          case 5: // com.bp22intel.edgesentinel.ui.components.CellInfoCardViewModel 
          return (T) new CellInfoCardViewModel(activityRetainedCImpl.towerVerifierProvider.get());

          case 6: // com.bp22intel.edgesentinel.ui.cellinfo.CellInfoViewModel 
          return (T) new CellInfoViewModel(singletonCImpl.cellRepositoryImplProvider.get(), singletonCImpl.provideCellInfoCollectorProvider.get());

          case 7: // com.bp22intel.edgesentinel.ui.dashboard.DashboardViewModel 
          return (T) new DashboardViewModel(singletonCImpl.alertRepositoryImplProvider.get(), singletonCImpl.cellRepositoryImplProvider.get(), singletonCImpl.scanRepositoryImplProvider.get(), new DemoDataGenerator(), singletonCImpl.provideCellInfoCollectorProvider.get(), singletonCImpl.sensorFusionEngineProvider.get(), singletonCImpl.overallThreatDashboardProvider.get());

          case 8: // com.bp22intel.edgesentinel.mesh.MeshViewModel 
          return (T) new MeshViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 9: // com.bp22intel.edgesentinel.ui.network.NetworkIntegrityViewModel 
          return (T) new NetworkIntegrityViewModel(singletonCImpl.vpnMonitorProvider.get(), singletonCImpl.dnsIntegrityCheckerProvider.get(), singletonCImpl.tlsIntegrityCheckerProvider.get(), singletonCImpl.captivePortalDetectorProvider.get());

          case 10: // com.bp22intel.edgesentinel.ui.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(singletonCImpl.provideDataStoreProvider.get());

          case 11: // com.bp22intel.edgesentinel.ui.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.provideDataStoreProvider.get());

          case 12: // com.bp22intel.edgesentinel.ui.map.ThreatMapViewModel 
          return (T) new ThreatMapViewModel(singletonCImpl.alertRepositoryImplProvider.get(), singletonCImpl.threatGeolocationProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 13: // com.bp22intel.edgesentinel.ui.settings.TowerDatabaseViewModel 
          return (T) new TowerDatabaseViewModel(singletonCImpl.towerDatabaseManagerProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 14: // com.bp22intel.edgesentinel.ui.wifi.WifiViewModel 
          return (T) new WifiViewModel(singletonCImpl.wifiMonitorProvider.get(), singletonCImpl.wifiThreatDetectorProvider.get(), singletonCImpl.wifiEnvironmentAnalyzerProvider.get(), singletonCImpl.wifiProbeProtectorProvider.get(), singletonCImpl.provideTrustedNetworkDaoProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends EdgeSentinelApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private Provider<TowerVerifier> towerVerifierProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
      this.towerVerifierProvider = DoubleCheck.provider(new SwitchingProvider<TowerVerifier>(singletonCImpl, activityRetainedCImpl, 1));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          case 1: // com.bp22intel.edgesentinel.detection.tower.TowerVerifier 
          return (T) new TowerVerifier(singletonCImpl.knownTowerDao());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends EdgeSentinelApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    private TelephonyMonitor telephonyMonitor() {
      return new TelephonyMonitor(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));
    }

    @Override
    public void injectMonitoringService(MonitoringService monitoringService) {
      injectMonitoringService2(monitoringService);
    }

    private MonitoringService injectMonitoringService2(MonitoringService instance) {
      MonitoringService_MembersInjector.injectCellInfoCollector(instance, singletonCImpl.provideCellInfoCollectorProvider.get());
      MonitoringService_MembersInjector.injectTelephonyMonitor(instance, telephonyMonitor());
      MonitoringService_MembersInjector.injectThreatDetectionEngine(instance, singletonCImpl.threatDetectionEngineProvider.get());
      MonitoringService_MembersInjector.injectCellRepository(instance, singletonCImpl.cellRepositoryImplProvider.get());
      MonitoringService_MembersInjector.injectAlertRepository(instance, singletonCImpl.alertRepositoryImplProvider.get());
      MonitoringService_MembersInjector.injectScanRepository(instance, singletonCImpl.scanRepositoryImplProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends EdgeSentinelApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<CellInfoCollector> provideCellInfoCollectorProvider;

    private Provider<RootChecker> rootCheckerProvider;

    private Provider<DiagBridge> diagBridgeProvider;

    private Provider<DiagMessageParser> diagMessageParserProvider;

    private Provider<NrMonitor> nrMonitorProvider;

    private Provider<ThreatScorer> threatScorerProvider;

    private Provider<EdgeSentinelDatabase> provideDatabaseProvider;

    private Provider<BaselineScorer> baselineScorerProvider;

    private Provider<BaselineLearner> baselineLearnerProvider;

    private Provider<BaselineManager> baselineManagerProvider;

    private Provider<ThreatDetectionEngine> threatDetectionEngineProvider;

    private Provider<CellRepositoryImpl> cellRepositoryImplProvider;

    private Provider<AlertRepositoryImpl> alertRepositoryImplProvider;

    private Provider<ScanRepositoryImpl> scanRepositoryImplProvider;

    private Provider<ScanWorker_AssistedFactory> scanWorker_AssistedFactoryProvider;

    private Provider<BleTrackerIdentifier> bleTrackerIdentifierProvider;

    private Provider<BleDeviceTracker> bleDeviceTrackerProvider;

    private Provider<BleAlertManager> bleAlertManagerProvider;

    private Provider<BleTrackingDetector> bleTrackingDetectorProvider;

    private Provider<CalibrationService> calibrationServiceProvider;

    private Provider<ThreatNarrator> threatNarratorProvider;

    private Provider<SensorFusionEngine> sensorFusionEngineProvider;

    private Provider<OverallThreatDashboard> overallThreatDashboardProvider;

    private Provider<VpnMonitor> vpnMonitorProvider;

    private Provider<DnsIntegrityChecker> dnsIntegrityCheckerProvider;

    private Provider<TlsIntegrityChecker> tlsIntegrityCheckerProvider;

    private Provider<CaptivePortalDetector> captivePortalDetectorProvider;

    private Provider<DataStore<Preferences>> provideDataStoreProvider;

    private Provider<ThreatGeolocation> threatGeolocationProvider;

    private Provider<TowerDatabaseManager> towerDatabaseManagerProvider;

    private Provider<WifiMonitor> wifiMonitorProvider;

    private Provider<WifiThreatDetector> wifiThreatDetectorProvider;

    private Provider<WifiEnvironmentAnalyzer> wifiEnvironmentAnalyzerProvider;

    private Provider<WifiProbeProtector> wifiProbeProtectorProvider;

    private Provider<TrustedNetworkDao> provideTrustedNetworkDaoProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private DiagBasedDetector diagBasedDetector() {
      return new DiagBasedDetector(diagBridgeProvider.get(), diagMessageParserProvider.get());
    }

    private CipherModeDetector cipherModeDetector() {
      return new CipherModeDetector(diagBasedDetector());
    }

    private NrDetector nrDetector() {
      return new NrDetector(nrMonitorProvider.get());
    }

    private Set<ThreatDetector> setOfThreatDetector() {
      return SetBuilder.<ThreatDetector>newSetBuilder(6).add(new FakeBtsDetector()).add(new NetworkDowngradeDetector()).add(new SilentSmsDetector()).add(new TrackingPatternDetector()).add(cipherModeDetector()).add(nrDetector()).build();
    }

    private BaselineDao baselineDao() {
      return AppModule_ProvideBaselineDaoFactory.provideBaselineDao(provideDatabaseProvider.get());
    }

    private CellDao cellDao() {
      return AppModule_ProvideCellDaoFactory.provideCellDao(provideDatabaseProvider.get());
    }

    private AlertDao alertDao() {
      return AppModule_ProvideAlertDaoFactory.provideAlertDao(provideDatabaseProvider.get());
    }

    private ScanDao scanDao() {
      return AppModule_ProvideScanDaoFactory.provideScanDao(provideDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return Collections.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>singletonMap("com.bp22intel.edgesentinel.service.ScanWorker", ((Provider) scanWorker_AssistedFactoryProvider));
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    private BleDeviceDao bleDeviceDao() {
      return AppModule_ProvideBleDeviceDaoFactory.provideBleDeviceDao(provideDatabaseProvider.get());
    }

    private KnownTowerDao knownTowerDao() {
      return AppModule_ProvideKnownTowerDaoFactory.provideKnownTowerDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideCellInfoCollectorProvider = DoubleCheck.provider(new SwitchingProvider<CellInfoCollector>(singletonCImpl, 1));
      this.rootCheckerProvider = DoubleCheck.provider(new SwitchingProvider<RootChecker>(singletonCImpl, 4));
      this.diagBridgeProvider = DoubleCheck.provider(new SwitchingProvider<DiagBridge>(singletonCImpl, 3));
      this.diagMessageParserProvider = DoubleCheck.provider(new SwitchingProvider<DiagMessageParser>(singletonCImpl, 5));
      this.nrMonitorProvider = DoubleCheck.provider(new SwitchingProvider<NrMonitor>(singletonCImpl, 6));
      this.threatScorerProvider = DoubleCheck.provider(new SwitchingProvider<ThreatScorer>(singletonCImpl, 7));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<EdgeSentinelDatabase>(singletonCImpl, 9));
      this.baselineScorerProvider = DoubleCheck.provider(new SwitchingProvider<BaselineScorer>(singletonCImpl, 10));
      this.baselineLearnerProvider = DoubleCheck.provider(new SwitchingProvider<BaselineLearner>(singletonCImpl, 11));
      this.baselineManagerProvider = DoubleCheck.provider(new SwitchingProvider<BaselineManager>(singletonCImpl, 8));
      this.threatDetectionEngineProvider = DoubleCheck.provider(new SwitchingProvider<ThreatDetectionEngine>(singletonCImpl, 2));
      this.cellRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<CellRepositoryImpl>(singletonCImpl, 12));
      this.alertRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<AlertRepositoryImpl>(singletonCImpl, 13));
      this.scanRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<ScanRepositoryImpl>(singletonCImpl, 14));
      this.scanWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<ScanWorker_AssistedFactory>(singletonCImpl, 0));
      this.bleTrackerIdentifierProvider = DoubleCheck.provider(new SwitchingProvider<BleTrackerIdentifier>(singletonCImpl, 17));
      this.bleDeviceTrackerProvider = DoubleCheck.provider(new SwitchingProvider<BleDeviceTracker>(singletonCImpl, 16));
      this.bleAlertManagerProvider = DoubleCheck.provider(new SwitchingProvider<BleAlertManager>(singletonCImpl, 18));
      this.bleTrackingDetectorProvider = DoubleCheck.provider(new SwitchingProvider<BleTrackingDetector>(singletonCImpl, 15));
      this.calibrationServiceProvider = DoubleCheck.provider(new SwitchingProvider<CalibrationService>(singletonCImpl, 19));
      this.threatNarratorProvider = DoubleCheck.provider(new SwitchingProvider<ThreatNarrator>(singletonCImpl, 21));
      this.sensorFusionEngineProvider = DoubleCheck.provider(new SwitchingProvider<SensorFusionEngine>(singletonCImpl, 20));
      this.overallThreatDashboardProvider = DoubleCheck.provider(new SwitchingProvider<OverallThreatDashboard>(singletonCImpl, 22));
      this.vpnMonitorProvider = DoubleCheck.provider(new SwitchingProvider<VpnMonitor>(singletonCImpl, 23));
      this.dnsIntegrityCheckerProvider = DoubleCheck.provider(new SwitchingProvider<DnsIntegrityChecker>(singletonCImpl, 24));
      this.tlsIntegrityCheckerProvider = DoubleCheck.provider(new SwitchingProvider<TlsIntegrityChecker>(singletonCImpl, 25));
      this.captivePortalDetectorProvider = DoubleCheck.provider(new SwitchingProvider<CaptivePortalDetector>(singletonCImpl, 26));
      this.provideDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<DataStore<Preferences>>(singletonCImpl, 27));
      this.threatGeolocationProvider = DoubleCheck.provider(new SwitchingProvider<ThreatGeolocation>(singletonCImpl, 28));
      this.towerDatabaseManagerProvider = DoubleCheck.provider(new SwitchingProvider<TowerDatabaseManager>(singletonCImpl, 29));
      this.wifiMonitorProvider = DoubleCheck.provider(new SwitchingProvider<WifiMonitor>(singletonCImpl, 30));
      this.wifiThreatDetectorProvider = DoubleCheck.provider(new SwitchingProvider<WifiThreatDetector>(singletonCImpl, 31));
      this.wifiEnvironmentAnalyzerProvider = DoubleCheck.provider(new SwitchingProvider<WifiEnvironmentAnalyzer>(singletonCImpl, 32));
      this.wifiProbeProtectorProvider = DoubleCheck.provider(new SwitchingProvider<WifiProbeProtector>(singletonCImpl, 33));
      this.provideTrustedNetworkDaoProvider = DoubleCheck.provider(new SwitchingProvider<TrustedNetworkDao>(singletonCImpl, 34));
    }

    @Override
    public void injectEdgeSentinelApp(EdgeSentinelApp edgeSentinelApp) {
      injectEdgeSentinelApp2(edgeSentinelApp);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private EdgeSentinelApp injectEdgeSentinelApp2(EdgeSentinelApp instance) {
      EdgeSentinelApp_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.bp22intel.edgesentinel.service.ScanWorker_AssistedFactory 
          return (T) new ScanWorker_AssistedFactory() {
            @Override
            public ScanWorker create(Context appContext, WorkerParameters workerParams) {
              return new ScanWorker(appContext, workerParams, singletonCImpl.provideCellInfoCollectorProvider.get(), singletonCImpl.threatDetectionEngineProvider.get(), singletonCImpl.cellRepositoryImplProvider.get(), singletonCImpl.alertRepositoryImplProvider.get(), singletonCImpl.scanRepositoryImplProvider.get());
            }
          };

          case 1: // com.bp22intel.edgesentinel.data.sensor.CellInfoCollector 
          return (T) AppModule_ProvideCellInfoCollectorFactory.provideCellInfoCollector(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.bp22intel.edgesentinel.detection.engine.ThreatDetectionEngine 
          return (T) new ThreatDetectionEngine(singletonCImpl.setOfThreatDetector(), singletonCImpl.threatScorerProvider.get(), singletonCImpl.baselineManagerProvider.get());

          case 3: // com.bp22intel.edgesentinel.diag.DiagBridge 
          return (T) new DiagBridge(singletonCImpl.rootCheckerProvider.get());

          case 4: // com.bp22intel.edgesentinel.diag.RootChecker 
          return (T) new RootChecker();

          case 5: // com.bp22intel.edgesentinel.diag.DiagMessageParser 
          return (T) new DiagMessageParser();

          case 6: // com.bp22intel.edgesentinel.data.sensor.NrMonitor 
          return (T) new NrMonitor(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // com.bp22intel.edgesentinel.detection.scoring.ThreatScorer 
          return (T) new ThreatScorer();

          case 8: // com.bp22intel.edgesentinel.baseline.BaselineManager 
          return (T) new BaselineManager(singletonCImpl.baselineDao(), singletonCImpl.baselineScorerProvider.get(), singletonCImpl.baselineLearnerProvider.get());

          case 9: // com.bp22intel.edgesentinel.data.local.EdgeSentinelDatabase 
          return (T) AppModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // com.bp22intel.edgesentinel.baseline.BaselineScorer 
          return (T) new BaselineScorer();

          case 11: // com.bp22intel.edgesentinel.baseline.BaselineLearner 
          return (T) new BaselineLearner();

          case 12: // com.bp22intel.edgesentinel.data.repository.CellRepositoryImpl 
          return (T) new CellRepositoryImpl(singletonCImpl.cellDao());

          case 13: // com.bp22intel.edgesentinel.data.repository.AlertRepositoryImpl 
          return (T) new AlertRepositoryImpl(singletonCImpl.alertDao());

          case 14: // com.bp22intel.edgesentinel.data.repository.ScanRepositoryImpl 
          return (T) new ScanRepositoryImpl(singletonCImpl.scanDao());

          case 15: // com.bp22intel.edgesentinel.detection.bluetooth.BleTrackingDetector 
          return (T) new BleTrackingDetector(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.bleDeviceTrackerProvider.get(), singletonCImpl.bleAlertManagerProvider.get(), singletonCImpl.bleTrackerIdentifierProvider.get());

          case 16: // com.bp22intel.edgesentinel.detection.bluetooth.BleDeviceTracker 
          return (T) new BleDeviceTracker(singletonCImpl.bleDeviceDao(), singletonCImpl.bleTrackerIdentifierProvider.get());

          case 17: // com.bp22intel.edgesentinel.detection.bluetooth.BleTrackerIdentifier 
          return (T) new BleTrackerIdentifier();

          case 18: // com.bp22intel.edgesentinel.detection.bluetooth.BleAlertManager 
          return (T) new BleAlertManager(singletonCImpl.bleDeviceTrackerProvider.get());

          case 19: // com.bp22intel.edgesentinel.calibration.CalibrationService 
          return (T) new CalibrationService(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 20: // com.bp22intel.edgesentinel.fusion.SensorFusionEngine 
          return (T) new SensorFusionEngine(singletonCImpl.threatNarratorProvider.get());

          case 21: // com.bp22intel.edgesentinel.fusion.ThreatNarrator 
          return (T) new ThreatNarrator();

          case 22: // com.bp22intel.edgesentinel.fusion.OverallThreatDashboard 
          return (T) new OverallThreatDashboard(singletonCImpl.sensorFusionEngineProvider.get(), singletonCImpl.threatNarratorProvider.get());

          case 23: // com.bp22intel.edgesentinel.detection.network.VpnMonitor 
          return (T) new VpnMonitor(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 24: // com.bp22intel.edgesentinel.detection.network.DnsIntegrityChecker 
          return (T) new DnsIntegrityChecker();

          case 25: // com.bp22intel.edgesentinel.detection.network.TlsIntegrityChecker 
          return (T) new TlsIntegrityChecker(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 26: // com.bp22intel.edgesentinel.detection.network.CaptivePortalDetector 
          return (T) new CaptivePortalDetector();

          case 27: // androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> 
          return (T) AppModule_ProvideDataStoreFactory.provideDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 28: // com.bp22intel.edgesentinel.detection.geo.ThreatGeolocation 
          return (T) new ThreatGeolocation();

          case 29: // com.bp22intel.edgesentinel.detection.tower.TowerDatabaseManager 
          return (T) new TowerDatabaseManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.knownTowerDao());

          case 30: // com.bp22intel.edgesentinel.detection.wifi.WifiMonitor 
          return (T) new WifiMonitor(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 31: // com.bp22intel.edgesentinel.detection.wifi.WifiThreatDetector 
          return (T) new WifiThreatDetector();

          case 32: // com.bp22intel.edgesentinel.detection.wifi.WifiEnvironmentAnalyzer 
          return (T) new WifiEnvironmentAnalyzer();

          case 33: // com.bp22intel.edgesentinel.detection.wifi.WifiProbeProtector 
          return (T) new WifiProbeProtector(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 34: // com.bp22intel.edgesentinel.data.local.dao.TrustedNetworkDao 
          return (T) AppModule_ProvideTrustedNetworkDaoFactory.provideTrustedNetworkDao(singletonCImpl.provideDatabaseProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
