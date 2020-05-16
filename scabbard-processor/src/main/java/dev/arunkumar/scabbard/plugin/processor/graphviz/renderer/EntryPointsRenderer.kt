package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import dagger.model.Binding
import dagger.model.BindingKind.MEMBERS_INJECTION
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.scabbard.plugin.processor.graphviz.RenderingContext

/**
 * Renders all entry point bindings in a separate cluster.
 */
class EntryPointsRenderer(
  override val renderingContext: RenderingContext
) : Renderer<List<Binding>> {
  override fun DotGraphBuilder.build(renderingElement: List<Binding>) {
    cluster("Entry Points") {
      graphAttributes {
        "labeljust" eq "l"
        "label" eq "Entry Points"
      }
      renderingElement.forEach { node ->
        val label = when (node.kind()) {
          MEMBERS_INJECTION -> "inject (${node.label})"
          else -> node.label
        }
        node.id {
          "shape" eq "component"
          "label" eq label
          "penwidth" eq 2
        }
      }
    }
  }
}