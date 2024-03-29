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
