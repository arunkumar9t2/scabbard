package dev.arunkumar.scabbard.plugin.di

import dagger.BindsInstance
import dagger.Subcomponent
import dagger.model.BindingGraph
import dagger.spi.DiagnosticReporter
import dev.arunkumar.scabbard.plugin.processor.BindingGraphProcessor
import dev.arunkumar.scabbard.plugin.processor.graphviz.GraphvizVisualizationModule

@VisitGraphScope
@Subcomponent(
  modules = [GraphvizVisualizationModule::class]
)
interface BindingGraphVisitorComponent {
  fun bindingGraphProcessors(): Set<BindingGraphProcessor>

  @Subcomponent.Factory
  interface Factory {
    fun create(
      @BindsInstance bindingGraph: BindingGraph,
      @BindsInstance diagnosticReporter: DiagnosticReporter
    ): BindingGraphVisitorComponent
  }
}
