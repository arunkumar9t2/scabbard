package dev.arunkumar.scabbard.coffee;

import dagger.Binds;
import dagger.Module;

import javax.inject.Singleton;

@Module(includes = PumpModule.class)
abstract class DripCoffeeModule {
  @Binds
  @Singleton
  abstract Heater bindHeater(ElectricHeater heater);
}