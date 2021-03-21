package dev.arunkumar.scabbard.plugin.processor.graphviz

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import dev.arunkumar.scabbard.plugin.BindingGraphProcessor

@Module
interface BindingGraphProcessorModule {
  @Binds
  @IntoSet
  fun bindingGraphProcessor(
    defaultBindingGraphProcessor: DefaultBindingGraphProcessor
  ): BindingGraphProcessor

  @Binds
  @IntoSet
  fun componentTreeProcessor(componentTreeProcessor: ComponentTreeProcessor): BindingGraphProcessor
}
