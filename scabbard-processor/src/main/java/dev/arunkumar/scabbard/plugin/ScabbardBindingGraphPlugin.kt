package dev.arunkumar.scabbard.plugin

import com.google.auto.service.AutoService
import dagger.model.BindingGraph
import dagger.spi.BindingGraphPlugin
import dagger.spi.DiagnosticReporter
import dev.arunkumar.scabbard.plugin.di.DaggerScabbardComponent
import dev.arunkumar.scabbard.plugin.di.ProcessingEnvModule
import dev.arunkumar.scabbard.plugin.options.SUPPORTED_OPTIONS
import javax.annotation.processing.Filer
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

@AutoService(BindingGraphPlugin::class)
class ScabbardBindingGraphPlugin : BindingGraphPlugin {
  private lateinit var filer: Filer
  private lateinit var types: Types
  private lateinit var elements: Elements
  private lateinit var options: Map<String, String>

  override fun pluginName() = "Scabbard Dagger Plugin"

  override fun supportedOptions() = SUPPORTED_OPTIONS

  override fun initFiler(filer: Filer) {
    this.filer = filer
  }

  override fun initTypes(types: Types) {
    this.types = types
  }

  override fun initElements(elements: Elements) {
    this.elements = elements
  }

  override fun initOptions(options: Map<String, String>) {
    this.options = options
  }

  // TODO(arun) Establish  Singleton -> VisitGraph scope
  override fun visitGraph(bindingGraph: BindingGraph, diagnosticReporter: DiagnosticReporter) {
    val processingEnvModule = ProcessingEnvModule(filer, types, elements, options)
    DaggerScabbardComponent.factory()
      .create(processingEnvModule, bindingGraph, diagnosticReporter)
      .bindingGraphProcessors()
      .forEach(BindingGraphProcessor::process)
  }
}
