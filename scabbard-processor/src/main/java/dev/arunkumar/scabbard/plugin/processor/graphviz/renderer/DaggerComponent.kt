package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import dagger.model.Binding
import dagger.model.BindingGraph
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.scabbard.plugin.processor.graphviz.RenderingContext

data class DaggerComponent(
  val entryPoints: List<Binding>,
  val dependencyNodes: List<BindingGraph.Node>,
  val subcomponents: List<BindingGraph.ComponentNode>,
  val edges: List<BindingGraph.Edge>
) {
  class GraphRenderer(
    override val renderingContext: RenderingContext
  ) : Renderer<DaggerComponent> {
    override fun DotGraphBuilder.build(renderingElement: DaggerComponent) {
      EntryPointsRenderer(renderingContext).render(this, renderingElement.entryPoints)
      DependenciesRenderer(renderingContext).render(this, renderingElement.dependencyNodes)
      SimpleSubComponentRenderer(renderingContext).render(this, renderingElement.subcomponents)
      EdgeRenderer(renderingContext).render(this, renderingElement.edges)
    }
  }
}