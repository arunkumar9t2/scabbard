package dev.arunkumar.scabbard.plugin.output

import dagger.Binds
import dagger.Module

@Module(includes = [OutputWriterModule::class])
interface OutputModule {
  @Binds
  fun DefaultOutputManager.bindOutputManager(): OutputManager
}
