package dev.arunkumar.scabbard.coffee;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module(includes = PumpModule.class)
abstract class DripCoffeeModule {
  @Binds
  @Singleton
  abstract Heater bindHeater(ElectricHeater heater);
}