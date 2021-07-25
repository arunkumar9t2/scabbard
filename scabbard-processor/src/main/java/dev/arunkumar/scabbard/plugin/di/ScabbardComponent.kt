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
