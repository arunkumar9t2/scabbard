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

package dev.arunkumar.scabbard.plugin.output

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.flatMap
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import dev.arunkumar.scabbard.plugin.di.ProcessorScope
import dev.arunkumar.scabbard.plugin.options.ScabbardOptions
import dev.arunkumar.scabbard.plugin.output.OutputManager.Format.*
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import javax.inject.Inject
import javax.inject.Provider
import javax.lang.model.element.TypeElement

interface OutputWriter {
  fun write(
    dotString: String,
    component: TypeElement,
    isFull: Boolean,
    isComponentTree: Boolean = false
  ): Result<Boolean, Exception>
}

@Module
object OutputWriterModule {
  @ProcessorScope
  @Provides
  @ElementsIntoSet
  fun writers(
    scabbardOptions: ScabbardOptions,
    dotProvider: Provider<DotWriter>,
    pngProvider: Provider<PngWriter>,
    svgProvider: Provider<SvgWriter>
  ): Set<OutputWriter> {
    val imageWriterProvider = if (scabbardOptions.outputImageFormat == SVG) {
      svgProvider
    } else {
      pngProvider
    }
    return linkedSetOf<OutputWriter>(dotProvider.get(), imageWriterProvider.get())
  }
}

@ProcessorScope
class DotWriter
@Inject
constructor(private val outputManager: OutputManager) : OutputWriter {

  override fun write(
    dotString: String,
    component: TypeElement,
    isFull: Boolean,
    isComponentTree: Boolean
  ) = outputManager.createOutputFiles(DOT, component, isFull, isComponentTree)
    .flatMap { fileObject ->
      Result.of<Boolean, Exception> {
        fileObject.openOutputStream().use { it.write(dotString.toByteArray()) }
        true
      }
    }
}

@ProcessorScope
class PngWriter
@Inject
constructor(private val outputManager: OutputManager) : OutputWriter {

  override fun write(
    dotString: String,
    component: TypeElement,
    isFull: Boolean,
    isComponentTree: Boolean
  ) = outputManager.createOutputFiles(PNG, component, isFull, isComponentTree)
    .flatMap { fileObject ->
      Result.of<Boolean, Exception> {
        fileObject.openOutputStream().use { stream ->
          Graphviz.fromString(dotString)
            .render(Format.PNG)
            .toOutputStream(stream)
        }
        true
      }
    }
}

@ProcessorScope
class SvgWriter
@Inject
constructor(private val outputManager: OutputManager) : OutputWriter {
  override fun write(
    dotString: String,
    component: TypeElement,
    isFull: Boolean,
    isComponentTree: Boolean
  ) = outputManager.createOutputFiles(SVG, component, isFull, isComponentTree)
    .flatMap { fileObject ->
      Result.of<Boolean, Exception> {
        fileObject.openOutputStream().use { stream ->
          Graphviz.fromString(dotString)
            .render(Format.SVG)
            .toOutputStream(stream)
        }
        true
      }
    }
}
