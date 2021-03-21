package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import dagger.model.BindingGraph
import dagger.model.ComponentPath
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.scabbard.plugin.processor.graphviz.RenderingContext

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
    override fun DotGraphBuilder.build(renderingElement: DaggerComponent) {
      cluster("Entry Points") {
        graphAttributes {
          "labeljust" eq "l"
          "label" eq "Entry Points"
        }
        nodeAttributes {
          "shape" eq "component"
          "penwidth" eq 2
        }
        BindingsRenderer(renderingContext).render(this, renderingElement.entryPointBindings)
      }

      cluster("Dependency Graph") {
        graphAttributes {
          "labeljust" eq "l"
          "label" eq "Dependency Graph"
        }
        BindingsRenderer(renderingContext).render(this, renderingElement.dependencyBindings)
      }

      SimpleSubComponentRenderer(renderingContext).render(this, renderingElement.subcomponents)
      InheritedBinding.GraphRenderer(renderingContext).render(this, renderingElement.inheritedBindings)
      EdgeRenderer(renderingContext).render(this, renderingElement.edges)
    }
  }
}
