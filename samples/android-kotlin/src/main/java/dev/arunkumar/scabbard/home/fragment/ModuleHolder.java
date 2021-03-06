package dev.arunkumar.scabbard.home.fragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dev.arunkumar.scabbard.di.scope.FragmentScope;

public class ModuleHolder {
  @Module
  public abstract class HomeFragmentBuilder {
    @FragmentScope
    @ContributesAndroidInjector()
    public abstract HomeFragment homeFragment();
  }
}