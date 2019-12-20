package dev.arunkumar.scabbard.home.simple;

import dagger.Subcomponent;

@SimpleScope
@Subcomponent
public interface SimpleSubcomponent {

  SimpleDep simpleDep();

  @Subcomponent.Factory
  interface Factory {
    SimpleSubcomponent create();
  }
}
