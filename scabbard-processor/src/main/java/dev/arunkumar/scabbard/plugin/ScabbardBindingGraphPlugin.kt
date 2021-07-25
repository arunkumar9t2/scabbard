/*
 * Copyright 2021 Arunkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.arunkumar.scabbard.plugin

import com.google.auto.service.AutoService
import dagger.model.BindingGraph
import dagger.spi.BindingGraphPlugin
import dagger.spi.DiagnosticReporter
import dev.arunkumar.scabbard.plugin.di.DaggerScabbardComponent
import dev.arunkumar.scabbard.plugin.di.ProcessingEnvModule
import dev.arunkumar.scabbard.plugin.di.ScabbardComponent
import dev.arunkumar.scabbard.plugin.options.SUPPORTED_OPTIONS
import dev.arunkumar.scabbard.plugin.processor.BindingGraphProcessor
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

  private val scabbardComponent: ScabbardComponent by lazy {
    val processingEnvModule = ProcessingEnvModule(filer, types, elements, options)
    DaggerScabbardComponent.factory().create(processingEnvModule)
  }

  override fun visitGraph(bindingGraph: BindingGraph, diagnosticReporter: DiagnosticReporter) {
    scabbardComponent.bindingGraphVisitorComponent()
      .create(bindingGraph, diagnosticReporter)
      .bindingGraphProcessors()
      .forEach(BindingGraphProcessor::process)
  }
}
