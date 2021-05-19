package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import dagger.model.BindingGraph
import dagger.model.ComponentPath
import dev.arunkumar.dot.dsl.DotGraphBuilder

/**
 * Data structure for holding data about a given component
 */
data class DaggerComponent(
  val componentPath: ComponentPath,
  val entryPointBindings: List<BindingGraph.MaybeBinding>,
  val dependencyBindings: List<BindingGraph.MaybeBinding>,
  val subcomponents: List<BindingGraph.ComponentNode>,
  val inheritedBindings: List<InheritedBinding>,
  val edges: List<BindingGraph.Edge>
) {
  /**
   * Renders the given component's dependency graph by delegating for appropriate `Renderer` instances.
   */
  class GraphRenderer(
    override val renderingContext: RenderingContext
  ) : Renderer<DaggerComponent> {
    override fun DotGraphBuilder.build(renderElement: DaggerComponent) {
      cluster("Entry Points") {
        graphAttributes {
          "labeljust" `=` "l"
          "label" `=` "Entry Points"
        }
        nodeAttributes {
          "shape" `=` "component"
          "penwidth" `=` 2
        }
        BindingsRenderer(renderingContext).render(this, renderElement.entryPointBindings)
      }

      cluster("Dependency Graph") {
        graphAttributes {
          "labeljust" `=` "l"
          "label" `=` "Dependency Graph"
        }
        BindingsRenderer(renderingContext).render(this, renderElement.dependencyBindings)
      }

      SimpleSubComponentRenderer(renderingContext).render(this, renderElement.subcomponents)
      InheritedBinding.GraphRenderer(renderingContext).render(this, renderElement.inheritedBindings)
      EdgeRenderer(renderingContext).render(this, renderElement.edges)
    }
  }
}
