package dev.arunkumar.scabbard.home.simple;

import dagger.Subcomponent;

@SimpleScope
@Subcomponent
public interface SimpleSubcomponent {
  @Subcomponent.Factory
  interface Factory {
    SimpleSubcomponent create();
  }
}
