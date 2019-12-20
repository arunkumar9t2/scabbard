package dev.arunkumar.scabbard.plugin.output

import dagger.Binds
import dagger.Module

@Module
interface OutputModule {
  @Binds
  fun bindOutputManager(defaultOutputManager: DefaultOutputManager): OutputManager
}