package dev.arunkumar.scabbard.plugin.di

import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.model.BindingGraph
import dagger.spi.DiagnosticReporter
import dev.arunkumar.scabbard.plugin.processor.BindingGraphProcessor
import dev.arunkumar.scabbard.plugin.options.ScabbardOptions
import dev.arunkumar.scabbard.plugin.options.parseOptions
import dev.arunkumar.scabbard.plugin.output.OutputModule
import dev.arunkumar.scabbard.plugin.parser.TypeNameExtractorModule
import dev.arunkumar.scabbard.plugin.processor.graphviz.GraphvizVisualizationModule
import javax.annotation.processing.Filer
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

@ProcessorScope
@Component(
  modules = [
    OutputModule::class,
    ProcessingEnvModule::class,
    TypeNameExtractorModule::class,
    GraphvizVisualizationModule::class
  ]
)
interface ScabbardComponent {

  fun bindingGraphProcessors(): Set<BindingGraphProcessor>

  @Component.Factory
  interface Factory {
    fun create(
      processingEnvModule: ProcessingEnvModule,
      @BindsInstance bindingGraph: BindingGraph,
    ): ScabbardComponent
  }
}

@Module
class ProcessingEnvModule(
  private val filer: Filer,
  private val types: Types,
  private val elements: Elements,
  private val options: Map<String, String>,
  private val diagnosticReporter: DiagnosticReporter
) {
  @ProcessorScope
  @Provides
  fun filer(): Filer = filer

  @ProcessorScope
  @Provides
  fun types(): Types = types

  @ProcessorScope
  @Provides
  fun elements(): Elements = elements

  @ProcessorScope
  @Provides
  fun scabbardOptions(): ScabbardOptions = parseOptions(options)

  @ProcessorScope
  @Provides
  fun diagnosticReporter(): DiagnosticReporter = diagnosticReporter
}
