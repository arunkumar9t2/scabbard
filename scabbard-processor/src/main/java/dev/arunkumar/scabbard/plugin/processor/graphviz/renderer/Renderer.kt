package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import dagger.model.Binding
import dagger.model.BindingGraph.Node
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.scabbard.plugin.processor.graphviz.RenderingContext

/**
 * Base definition for a class that takes a element of type `T` and renders the dot definition using the given
 * `DotGraphBuilder` in `Renderer.render`.
 *
 * The typical usage is to create a root `Renderer` with base graph attributes and then call many other renderers for
 * different types. The current renderer is responsible for passing `renderingContext` to other `renderer`s.
 */
interface Renderer<T> {
  /**
   * The rendering context for this render. The root renderer usually has the global context and other renderer typically
   * pass it to other renderers. Intermediate state between multiple renderers are store here.
   *
   * It is the responsibility of the current renderer to pass the correct context to other renderers.
   */
  val renderingContext: RenderingContext

  /**
   * Id of a given `Node` for the entire rendering flow. Usually there is very little need to customize id.
   */
  val Node.id get() = renderingContext.nodeId(this)

  val Node.label get() = renderingContext.nodeLabel(this)

  val Binding.isEntryPoint get() = renderingContext.isEntryPoint(this)

  val Binding.color get() = renderingContext.color(this)

  fun render(dotGraphBuilder: DotGraphBuilder, element: T) = dotGraphBuilder.build(element)

  /**
   * The actually rendering logic to implemented called with context of `DotGraphBuilder` passed to this renderer by the
   * consumer. Implement the rendering logic or delegate work to other renderers here.
   */
  fun DotGraphBuilder.build(renderingElement: T)
}