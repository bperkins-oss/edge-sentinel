package com.bp22intel.edgesentinel.mesh;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class MeshViewModel_Factory implements Factory<MeshViewModel> {
  private final Provider<Context> contextProvider;

  public MeshViewModel_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public MeshViewModel get() {
    return newInstance(contextProvider.get());
  }

  public static MeshViewModel_Factory create(Provider<Context> contextProvider) {
    return new MeshViewModel_Factory(contextProvider);
  }

  public static MeshViewModel newInstance(Context context) {
    return new MeshViewModel(context);
  }
}
