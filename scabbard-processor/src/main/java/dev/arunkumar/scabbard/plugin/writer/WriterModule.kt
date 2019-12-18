package dev.arunkumar.scabbard.plugin.writer

import dagger.Binds
import dagger.Module

@Module
interface WriterModule {
  @Binds
  fun bindWriter(defaultOutputWriter: DefaultOutputWriter): OutputWriter
}