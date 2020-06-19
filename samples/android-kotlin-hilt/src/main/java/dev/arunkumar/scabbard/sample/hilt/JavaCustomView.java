package dev.arunkumar.scabbard.sample.hilt;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.WithFragmentBindings;

import javax.inject.Inject;

@WithFragmentBindings
@AndroidEntryPoint
public final class JavaCustomView extends View {
  @Inject
  SimpleActivity.ActivityDependency activityDependency;

  public JavaCustomView(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
  }
}