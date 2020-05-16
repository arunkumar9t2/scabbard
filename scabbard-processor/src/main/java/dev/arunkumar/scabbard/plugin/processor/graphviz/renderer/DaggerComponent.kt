package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import dagger.model.Binding
import dagger.model.BindingGraph
import dagger.model.ComponentPath
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.scabbard.plugin.processor.graphviz.RenderingContext

/**
 * Data structure for holding data about a given component
 */
data class DaggerComponent(
  val componentPath: ComponentPath,
  val entryPoints: List<Binding>,
  val dependencyBindings: List<BindingGraph.MaybeBinding>,
  val subcomponents: List<BindingGraph.ComponentNode>,
  val edges: List<BindingGraph.Edge>
) {
  /**
   * Renders the given component's dependency graph by delegating for appropriate `Renderer` instances.
   */
  class GraphRenderer(
    override val renderingContext: RenderingContext
  ) : Renderer<DaggerComponent> {
    override fun DotGraphBuilder.build(renderingElement: DaggerComponent) {
      EntryPointsRenderer(renderingContext).render(this, renderingElement.entryPoints)
      DependenciesRenderer(renderingContext).render(this, renderingElement.dependencyBindings)
      SimpleSubComponentRenderer(renderingContext).render(this, renderingElement.subcomponents)
      EdgeRenderer(renderingContext).render(this, renderingElement.edges)
    }
  }
}