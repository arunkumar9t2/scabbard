package dev.arunkumar.scabbard.plugin

import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.model.BindingGraph
import dagger.spi.DiagnosticReporter
import javax.annotation.processing.Filer
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

@ProcessorScope
@Component(
    modules = [
        ProcessingEnvModule::class,
        GraphVizBindingGraphProcessorModule::class
    ]
)
interface ScabbardComponent {

    fun bindingGraphProcessor(): BindingGraphProcessor

    @Component.Factory
    interface Factory {
        fun create(
            processingEnvModule: ProcessingEnvModule,
            @BindsInstance bindingGraph: BindingGraph,
            @BindsInstance diagnosticReporter: DiagnosticReporter
        ): ScabbardComponent
    }
}

@Module
class ProcessingEnvModule(
    private val filer: Filer,
    private val types: Types,
    private val elements: Elements
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
}

