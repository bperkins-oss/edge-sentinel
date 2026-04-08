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
import com.bp22intel.edgesentinel.data.local.EdgeSentinelDatabase;
import com.bp22intel.edgesentinel.data.local.dao.AlertDao;
import com.bp22intel.edgesentinel.data.local.dao.CellDao;
import com.bp22intel.edgesentinel.data.local.dao.ScanDao;
import com.bp22intel.edgesentinel.data.repository.AlertRepositoryImpl;
import com.bp22intel.edgesentinel.data.repository.CellRepositoryImpl;
import com.bp22intel.edgesentinel.data.repository.ScanRepositoryImpl;
import com.bp22intel.edgesentinel.data.sensor.CellInfoCollector;
import com.bp22intel.edgesentinel.data.sensor.NrMonitor;
import com.bp22intel.edgesentinel.data.sensor.TelephonyMonitor;
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
import com.bp22intel.edgesentinel.detection.scoring.ThreatScorer;
import com.bp22intel.edgesentinel.di.AppModule_ProvideAlertDaoFactory;
import com.bp22intel.edgesentinel.di.AppModule_ProvideCellDaoFactory;
import com.bp22intel.edgesentinel.di.AppModule_ProvideCellInfoCollectorFactory;
import com.bp22intel.edgesentinel.di.AppModule_ProvideDataStoreFactory;
import com.bp22intel.edgesentinel.di.AppModule_ProvideDatabaseFactory;
import com.bp22intel.edgesentinel.di.AppModule_ProvideScanDaoFactory;
import com.bp22intel.edgesentinel.diag.DiagBridge;
import com.bp22intel.edgesentinel.diag.DiagMessageParser;
import com.bp22intel.edgesentinel.diag.RootChecker;
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
import com.bp22intel.edgesentinel.ui.cellinfo.CellInfoViewModel;
import com.bp22intel.edgesentinel.ui.cellinfo.CellInfoViewModel_HiltModules;
import com.bp22intel.edgesentinel.ui.dashboard.DashboardViewModel;
import com.bp22intel.edgesentinel.ui.dashboard.DashboardViewModel_HiltModules;
import com.bp22intel.edgesentinel.ui.onboarding.OnboardingViewModel;
import com.bp22intel.edgesentinel.ui.onboarding.OnboardingViewModel_HiltModules;
import com.bp22intel.edgesentinel.ui.settings.SettingsViewModel;
import com.bp22intel.edgesentinel.ui.settings.SettingsViewModel_HiltModules;
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
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(7).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_alerts_AlertDetailViewModel, AlertDetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_alerts_AlertsViewModel, AlertsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_cellinfo_CellInfoViewModel, CellInfoViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_dashboard_DashboardViewModel, DashboardViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_mesh_MeshViewModel, MeshViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_onboarding_OnboardingViewModel, OnboardingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_settings_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).build());
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
      static String com_bp22intel_edgesentinel_mesh_MeshViewModel = "com.bp22intel.edgesentinel.mesh.MeshViewModel";

      static String com_bp22intel_edgesentinel_ui_alerts_AlertsViewModel = "com.bp22intel.edgesentinel.ui.alerts.AlertsViewModel";

      static String com_bp22intel_edgesentinel_ui_cellinfo_CellInfoViewModel = "com.bp22intel.edgesentinel.ui.cellinfo.CellInfoViewModel";

      static String com_bp22intel_edgesentinel_ui_dashboard_DashboardViewModel = "com.bp22intel.edgesentinel.ui.dashboard.DashboardViewModel";

      static String com_bp22intel_edgesentinel_ui_onboarding_OnboardingViewModel = "com.bp22intel.edgesentinel.ui.onboarding.OnboardingViewModel";

      static String com_bp22intel_edgesentinel_ui_alerts_AlertDetailViewModel = "com.bp22intel.edgesentinel.ui.alerts.AlertDetailViewModel";

      static String com_bp22intel_edgesentinel_ui_settings_SettingsViewModel = "com.bp22intel.edgesentinel.ui.settings.SettingsViewModel";

      @KeepFieldType
      MeshViewModel com_bp22intel_edgesentinel_mesh_MeshViewModel2;

      @KeepFieldType
      AlertsViewModel com_bp22intel_edgesentinel_ui_alerts_AlertsViewModel2;

      @KeepFieldType
      CellInfoViewModel com_bp22intel_edgesentinel_ui_cellinfo_CellInfoViewModel2;

      @KeepFieldType
      DashboardViewModel com_bp22intel_edgesentinel_ui_dashboard_DashboardViewModel2;

      @KeepFieldType
      OnboardingViewModel com_bp22intel_edgesentinel_ui_onboarding_OnboardingViewModel2;

      @KeepFieldType
      AlertDetailViewModel com_bp22intel_edgesentinel_ui_alerts_AlertDetailViewModel2;

      @KeepFieldType
      SettingsViewModel com_bp22intel_edgesentinel_ui_settings_SettingsViewModel2;
    }
  }

  private static final class ViewModelCImpl extends EdgeSentinelApp_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AlertDetailViewModel> alertDetailViewModelProvider;

    private Provider<AlertsViewModel> alertsViewModelProvider;

    private Provider<CellInfoViewModel> cellInfoViewModelProvider;

    private Provider<DashboardViewModel> dashboardViewModelProvider;

    private Provider<MeshViewModel> meshViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

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
      this.cellInfoViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.meshViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(7).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_alerts_AlertDetailViewModel, ((Provider) alertDetailViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_alerts_AlertsViewModel, ((Provider) alertsViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_cellinfo_CellInfoViewModel, ((Provider) cellInfoViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_dashboard_DashboardViewModel, ((Provider) dashboardViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_mesh_MeshViewModel, ((Provider) meshViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_onboarding_OnboardingViewModel, ((Provider) onboardingViewModelProvider)).put(LazyClassKeyProvider.com_bp22intel_edgesentinel_ui_settings_SettingsViewModel, ((Provider) settingsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_bp22intel_edgesentinel_ui_cellinfo_CellInfoViewModel = "com.bp22intel.edgesentinel.ui.cellinfo.CellInfoViewModel";

      static String com_bp22intel_edgesentinel_ui_alerts_AlertsViewModel = "com.bp22intel.edgesentinel.ui.alerts.AlertsViewModel";

      static String com_bp22intel_edgesentinel_ui_alerts_AlertDetailViewModel = "com.bp22intel.edgesentinel.ui.alerts.AlertDetailViewModel";

      static String com_bp22intel_edgesentinel_mesh_MeshViewModel = "com.bp22intel.edgesentinel.mesh.MeshViewModel";

      static String com_bp22intel_edgesentinel_ui_settings_SettingsViewModel = "com.bp22intel.edgesentinel.ui.settings.SettingsViewModel";

      static String com_bp22intel_edgesentinel_ui_dashboard_DashboardViewModel = "com.bp22intel.edgesentinel.ui.dashboard.DashboardViewModel";

      static String com_bp22intel_edgesentinel_ui_onboarding_OnboardingViewModel = "com.bp22intel.edgesentinel.ui.onboarding.OnboardingViewModel";

      @KeepFieldType
      CellInfoViewModel com_bp22intel_edgesentinel_ui_cellinfo_CellInfoViewModel2;

      @KeepFieldType
      AlertsViewModel com_bp22intel_edgesentinel_ui_alerts_AlertsViewModel2;

      @KeepFieldType
      AlertDetailViewModel com_bp22intel_edgesentinel_ui_alerts_AlertDetailViewModel2;

      @KeepFieldType
      MeshViewModel com_bp22intel_edgesentinel_mesh_MeshViewModel2;

      @KeepFieldType
      SettingsViewModel com_bp22intel_edgesentinel_ui_settings_SettingsViewModel2;

      @KeepFieldType
      DashboardViewModel com_bp22intel_edgesentinel_ui_dashboard_DashboardViewModel2;

      @KeepFieldType
      OnboardingViewModel com_bp22intel_edgesentinel_ui_onboarding_OnboardingViewModel2;
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

          case 2: // com.bp22intel.edgesentinel.ui.cellinfo.CellInfoViewModel 
          return (T) new CellInfoViewModel(singletonCImpl.cellRepositoryImplProvider.get(), singletonCImpl.provideCellInfoCollectorProvider.get());

          case 3: // com.bp22intel.edgesentinel.ui.dashboard.DashboardViewModel 
          return (T) new DashboardViewModel(singletonCImpl.alertRepositoryImplProvider.get(), singletonCImpl.cellRepositoryImplProvider.get(), singletonCImpl.scanRepositoryImplProvider.get(), new DemoDataGenerator(), singletonCImpl.provideCellInfoCollectorProvider.get());

          case 4: // com.bp22intel.edgesentinel.mesh.MeshViewModel 
          return (T) new MeshViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.bp22intel.edgesentinel.ui.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(singletonCImpl.provideDataStoreProvider.get());

          case 6: // com.bp22intel.edgesentinel.ui.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.provideDataStoreProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends EdgeSentinelApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
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

    private Provider<ThreatDetectionEngine> threatDetectionEngineProvider;

    private Provider<EdgeSentinelDatabase> provideDatabaseProvider;

    private Provider<CellRepositoryImpl> cellRepositoryImplProvider;

    private Provider<AlertRepositoryImpl> alertRepositoryImplProvider;

    private Provider<ScanRepositoryImpl> scanRepositoryImplProvider;

    private Provider<ScanWorker_AssistedFactory> scanWorker_AssistedFactoryProvider;

    private Provider<DataStore<Preferences>> provideDataStoreProvider;

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

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideCellInfoCollectorProvider = DoubleCheck.provider(new SwitchingProvider<CellInfoCollector>(singletonCImpl, 1));
      this.rootCheckerProvider = DoubleCheck.provider(new SwitchingProvider<RootChecker>(singletonCImpl, 4));
      this.diagBridgeProvider = DoubleCheck.provider(new SwitchingProvider<DiagBridge>(singletonCImpl, 3));
      this.diagMessageParserProvider = DoubleCheck.provider(new SwitchingProvider<DiagMessageParser>(singletonCImpl, 5));
      this.nrMonitorProvider = DoubleCheck.provider(new SwitchingProvider<NrMonitor>(singletonCImpl, 6));
      this.threatScorerProvider = DoubleCheck.provider(new SwitchingProvider<ThreatScorer>(singletonCImpl, 7));
      this.threatDetectionEngineProvider = DoubleCheck.provider(new SwitchingProvider<ThreatDetectionEngine>(singletonCImpl, 2));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<EdgeSentinelDatabase>(singletonCImpl, 9));
      this.cellRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<CellRepositoryImpl>(singletonCImpl, 8));
      this.alertRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<AlertRepositoryImpl>(singletonCImpl, 10));
      this.scanRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<ScanRepositoryImpl>(singletonCImpl, 11));
      this.scanWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<ScanWorker_AssistedFactory>(singletonCImpl, 0));
      this.provideDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<DataStore<Preferences>>(singletonCImpl, 12));
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
          return (T) new ThreatDetectionEngine(singletonCImpl.setOfThreatDetector(), singletonCImpl.threatScorerProvider.get());

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

          case 8: // com.bp22intel.edgesentinel.data.repository.CellRepositoryImpl 
          return (T) new CellRepositoryImpl(singletonCImpl.cellDao());

          case 9: // com.bp22intel.edgesentinel.data.local.EdgeSentinelDatabase 
          return (T) AppModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // com.bp22intel.edgesentinel.data.repository.AlertRepositoryImpl 
          return (T) new AlertRepositoryImpl(singletonCImpl.alertDao());

          case 11: // com.bp22intel.edgesentinel.data.repository.ScanRepositoryImpl 
          return (T) new ScanRepositoryImpl(singletonCImpl.scanDao());

          case 12: // androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> 
          return (T) AppModule_ProvideDataStoreFactory.provideDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
