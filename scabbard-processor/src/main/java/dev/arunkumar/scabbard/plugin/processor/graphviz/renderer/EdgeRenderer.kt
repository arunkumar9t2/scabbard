package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import dagger.model.Binding
import dagger.model.BindingGraph.*
import dagger.model.BindingKind
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.scabbard.plugin.processor.graphviz.RenderingContext
import dev.arunkumar.scabbard.plugin.util.component1
import dev.arunkumar.scabbard.plugin.util.component2

/**
 * A renderer that renders edges for valid nodes in current rendering scope. For example, it only renders the given edge
 * if both the source and target node of the edge was already rendered in the current `RenderingContext`.
 */
@Suppress("UnstableApiUsage")
class EdgeRenderer(
  override val renderingContext: RenderingContext
) : Renderer<List<Edge>> {

  override fun DotGraphBuilder.build(renderingElement: List<Edge>) {
    renderingElement.forEach { edge ->
      val (source, target) = renderingContext.bindingGraph.network().incidentNodes(edge)
      if (renderingContext.validInContext(source, target)) {
        renderEdge(edge, source, target)
      }
    }
  }

  private fun DotGraphBuilder.renderEdge(edge: Edge, source: Node, target: Node) {
    when (edge) {
      is DependencyEdge -> {
        if (!edge.isEntryPoint) {
          (source.id link target.id) {
            if ((source as? Binding)?.kind() == BindingKind.DELEGATE) {
              // Delegate edges i.e usually using @Binds
              "style" eq "dotted"
              "label" eq "delegates"
            }
            // Handle missing binding
            if (source is MissingBinding || target is MissingBinding) {
              "style" eq "dashed"
              "arrowType" eq "empty"
              val labelLocation = if (source is MissingBinding) "taillabel" else "headlabel"
              labelLocation eq "Missing binding"
            }
          }
        }
      }
      is ChildFactoryMethodEdge -> {
        (source.id link target.id) {
          "style" eq "dashed"
          "taillabel" eq edge.factoryMethod()
        }
      }
      is SubcomponentCreatorBindingEdge -> {
        (source.id link target.id) {
          "style" eq "dashed"
          "label" eq "subcomponent"
          "headport" eq "w"
        }
      }
    }
  }
}