package com.bp22intel.edgesentinel.di;

import com.bp22intel.edgesentinel.analysis.ThreatAnalyst;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AnalysisModule_ProvideThreatAnalystFactory implements Factory<ThreatAnalyst> {
  @Override
  public ThreatAnalyst get() {
    return provideThreatAnalyst();
  }

  public static AnalysisModule_ProvideThreatAnalystFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ThreatAnalyst provideThreatAnalyst() {
    return Preconditions.checkNotNullFromProvides(AnalysisModule.INSTANCE.provideThreatAnalyst());
  }

  private static final class InstanceHolder {
    private static final AnalysisModule_ProvideThreatAnalystFactory INSTANCE = new AnalysisModule_ProvideThreatAnalystFactory();
  }
}
