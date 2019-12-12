package dev.arunkumar.scabbard.plugin.graphviz

import dagger.Binds
import dagger.Module
import dev.arunkumar.scabbard.plugin.BindingGraphProcessor

@Module
interface GraphVizBindingGraphProcessorModule {
    @Binds
    fun bindingGraphProcessor(
        graphVizBindingGraphProcessor: GraphVizBindingGraphProcessor
    ): BindingGraphProcessor
}