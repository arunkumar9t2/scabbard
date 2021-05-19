package dev.arunkumar.scabbard.plugin.processor.graphviz

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import dev.arunkumar.scabbard.plugin.processor.BindingGraphProcessor
import dev.arunkumar.scabbard.plugin.processor.graphviz.renderer.GraphVizRenderingModule

@Module(
  includes = [GraphVizRenderingModule::class]
)
interface GraphvizVisualizationModule {
  @Binds
  @IntoSet
  fun ComponentVisualizationProcessor.bindingGraphProcessor(): BindingGraphProcessor

  @Binds
  @IntoSet
  fun ComponentTreeVisualizationProcessor.componentTreeProcessor(): BindingGraphProcessor
}
