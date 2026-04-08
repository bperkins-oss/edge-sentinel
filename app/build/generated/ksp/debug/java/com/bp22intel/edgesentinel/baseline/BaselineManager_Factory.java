package com.bp22intel.edgesentinel.baseline;

import com.bp22intel.edgesentinel.data.local.dao.BaselineDao;
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
public final class BaselineManager_Factory implements Factory<BaselineManager> {
  private final Provider<BaselineDao> baselineDaoProvider;

  private final Provider<BaselineScorer> scorerProvider;

  private final Provider<BaselineLearner> learnerProvider;

  public BaselineManager_Factory(Provider<BaselineDao> baselineDaoProvider,
      Provider<BaselineScorer> scorerProvider, Provider<BaselineLearner> learnerProvider) {
    this.baselineDaoProvider = baselineDaoProvider;
    this.scorerProvider = scorerProvider;
    this.learnerProvider = learnerProvider;
  }

  @Override
  public BaselineManager get() {
    return newInstance(baselineDaoProvider.get(), scorerProvider.get(), learnerProvider.get());
  }

  public static BaselineManager_Factory create(Provider<BaselineDao> baselineDaoProvider,
      Provider<BaselineScorer> scorerProvider, Provider<BaselineLearner> learnerProvider) {
    return new BaselineManager_Factory(baselineDaoProvider, scorerProvider, learnerProvider);
  }

  public static BaselineManager newInstance(BaselineDao baselineDao, BaselineScorer scorer,
      BaselineLearner learner) {
    return new BaselineManager(baselineDao, scorer, learner);
  }
}
