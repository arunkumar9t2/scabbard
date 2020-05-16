package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import dagger.model.BindingGraph
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.scabbard.plugin.parser.name
import dev.arunkumar.scabbard.plugin.processor.graphviz.RenderingContext

class SimpleSubComponentRenderer(
  override val renderingContext: RenderingContext
) : Renderer<List<BindingGraph.ComponentNode>> {

  override fun DotGraphBuilder.build(renderingElement: List<BindingGraph.ComponentNode>) {
    cluster("Subcomponents") {
      graphAttributes {
        "labeljust" eq "l"
        "shape" eq "folder"
        "label" eq "Subcomponents"
      }
      renderingElement.forEach { subcomponent ->
        subcomponent.id {
          "label" eq subcomponent.label
          "href" eq renderingContext.href(subcomponent)
          // TODO(arun) will multiple scopes be present? If yes, can it be visualized?
          subcomponent.scopes().forEach { scope ->
            "color" eq renderingContext.scopeColor(scope.name)
          }
        }
      }
    }
  }
}