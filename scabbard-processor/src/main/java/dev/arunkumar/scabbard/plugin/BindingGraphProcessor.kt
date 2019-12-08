package dev.arunkumar.scabbard.plugin

import dagger.Binds
import dagger.Module

interface BindingGraphProcessor {
    fun process()
}

@Module
interface GraphVizBindingGraphProcessorModule {
    @Binds
    fun bindingGraphProcessor(
        graphVizBindingGraphProcessor: GraphVizBindingGraphProcessor
    ): BindingGraphProcessor
}