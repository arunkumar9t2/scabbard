package dev.arunkumar.scabbard.plugin.processor.graphviz

import dagger.Binds
import dagger.Module
import dev.arunkumar.scabbard.plugin.BindingGraphProcessor
import dev.arunkumar.scabbard.plugin.processor.graphviz.GraphVizBindingGraphProcessor

@Module
interface GraphVizBindingGraphProcessorModule {
  @Binds
  fun bindingGraphProcessor(
    graphVizBindingGraphProcessor: GraphVizBindingGraphProcessor
  ): BindingGraphProcessor
}