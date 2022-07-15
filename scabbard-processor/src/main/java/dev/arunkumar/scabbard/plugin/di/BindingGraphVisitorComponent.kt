/*
 * Copyright 2022 Arunkumar
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

package dev.arunkumar.scabbard.plugin.di

import dagger.BindsInstance
import dagger.Subcomponent
import dagger.model.BindingGraph
import dagger.spi.DiagnosticReporter
import dev.arunkumar.scabbard.plugin.processor.BindingGraphProcessor
import dev.arunkumar.scabbard.plugin.processor.graphviz.GraphvizVisualizationModule

@VisitGraphScope
@Subcomponent(
  modules = [
    GraphvizVisualizationModule::class,
  ]
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
