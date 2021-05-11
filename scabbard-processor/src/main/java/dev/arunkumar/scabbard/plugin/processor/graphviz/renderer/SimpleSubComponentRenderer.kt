package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import dagger.model.BindingGraph
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.scabbard.plugin.parser.name
import dev.arunkumar.scabbard.plugin.processor.graphviz.RenderingContext

/**
 * A renderer for all subcomponents that simply adds a Node with customized scope colors. This renderer does not expand
 * the subcomponent i.e does not render any nodes present inside the subcomponent.
 */
class SimpleSubComponentRenderer(
  override val renderingContext: RenderingContext
) : Renderer<List<BindingGraph.ComponentNode>> {

  override fun DotGraphBuilder.build(renderElement: List<BindingGraph.ComponentNode>) {
    cluster("Subcomponents") {
      graphAttributes {
        "labeljust" `=` "l"
        "shape" `=` "folder"
        "label" `=` "Subcomponents"
      }
      renderElement.forEach { subcomponent ->
        subcomponent.id {
          "label" `=` subcomponent.label
          "href" `=` renderingContext.href(subcomponent)
          // TODO(arun) will multiple scopes be present? If yes, can it be visualized?
          subcomponent.scopes().forEach { scope ->
            "color" `=` renderingContext.scopeColor(scope.name)
          }
        }
      }
    }
  }
}
