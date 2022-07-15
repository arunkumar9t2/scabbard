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

package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import dagger.model.Binding
import dagger.model.BindingGraph.MaybeBinding
import dagger.model.BindingGraph.Node
import dev.arunkumar.dot.dsl.DotGraphBuilder

/**
 * Base definition for a class that takes a element of type `T` and
 * renders the dot definition using the given `DotGraphBuilder` in
 * `Renderer.render`.
 *
 * The typical usage is to create a root `Renderer` with base graph
 * attributes and then call many other renderers for different types.
 * The current renderer is responsible for passing `renderingContext` to
 * other `renderer`s.
 */
interface Renderer<T> {
  /**
   * The rendering context for this render. The root renderer usually
   * has the global context and other renderers typically pass it to
   * other renderers. Intermediate state between multiple renderers can
   * be stored here.
   *
   * It is the responsibility of the current renderer to pass the
   * correct context to other renderers.
   */
  val renderingContext: RenderingContext

  /**
   * Id of a given `Node` for the entire rendering flow. Usually there
   * is very little need to customize id.
   */
  val Node.id get() = renderingContext.nodeId(this)

  val Node.label get() = renderingContext.nodeLabel(this)

  val MaybeBinding.isEntryPoint get() = renderingContext.isEntryPoint(this)

  val Binding.color get() = renderingContext.color(this)

  fun render(dotGraphBuilder: DotGraphBuilder, element: T) = dotGraphBuilder.build(element)

  /**
   * Implementations should render the given [renderElement] onto
   * [DotGraphBuilder]. A typical implementation simply renders the
   * element directly or delegates to other renderers as needed.
   * [renderingContext] should be passed to other renderers and can be
   * customized if needed.
   */
  fun DotGraphBuilder.build(renderElement: T)
}
