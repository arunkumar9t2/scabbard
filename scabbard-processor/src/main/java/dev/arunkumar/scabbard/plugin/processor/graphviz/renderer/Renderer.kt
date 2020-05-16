package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import dagger.model.Binding
import dagger.model.BindingGraph.Node
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.scabbard.plugin.processor.graphviz.RenderingContext

interface Renderer<T> {
  val renderingContext: RenderingContext

  val Node.id get() = renderingContext.nodeId(this)

  val Node.label get() = renderingContext.nodeLabel(this)

  val Binding.isEntryPoint get() = renderingContext.isEntryPoint(this)

  val Binding.color get() = renderingContext.color(this)

  fun render(dotGraphBuilder: DotGraphBuilder, element: T) = dotGraphBuilder.build(element)

  fun DotGraphBuilder.build(renderingElement: T)
}