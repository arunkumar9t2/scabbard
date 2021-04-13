package dev.arunkumar.scabbard.plugin.di

import dagger.Component
import dagger.Module
import dagger.Provides
import dev.arunkumar.scabbard.plugin.options.ScabbardOptions
import dev.arunkumar.scabbard.plugin.options.parseOptions
import dev.arunkumar.scabbard.plugin.output.OutputModule
import dev.arunkumar.scabbard.plugin.parser.TypeNameExtractorModule
import javax.annotation.processing.Filer
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

@ProcessorScope
@Component(
  modules = [
    OutputModule::class,
    ProcessingEnvModule::class,
    TypeNameExtractorModule::class,
  ]
)
interface ScabbardComponent {

  fun bindingGraphVisitorComponent(): BindingGraphVisitorComponent.Factory

  @Component.Factory
  interface Factory {
    fun create(
      processingEnvModule: ProcessingEnvModule,
    ): ScabbardComponent
  }
}

@Module
class ProcessingEnvModule(
  private val filer: Filer,
  private val types: Types,
  private val elements: Elements,
  private val options: Map<String, String>
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
}
