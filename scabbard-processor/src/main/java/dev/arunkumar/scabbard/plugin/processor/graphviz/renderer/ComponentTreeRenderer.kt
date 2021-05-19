package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import com.google.common.graph.EndpointPair
import com.google.common.graph.Graph
import dagger.model.BindingGraph
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.scabbard.plugin.parser.name
import dev.arunkumar.scabbard.plugin.util.component1
import dev.arunkumar.scabbard.plugin.util.component2

@Suppress("UnstableApiUsage")
class ComponentTreeRenderer(
  override val renderingContext: RenderingContext
) : Renderer<Graph<BindingGraph.ComponentNode>> {

  override fun DotGraphBuilder.build(renderElement: Graph<BindingGraph.ComponentNode>) {
    renderElement.nodes()
      .forEach { componentNode ->
        componentNode.id {
          "label" `=` componentNode.label
          "href" `=` renderingContext.href(componentNode)
          // TODO(arun) will multiple scopes be present? If yes, can it be visualized?
          componentNode.scopes().forEach { scope ->
            "color" `=` renderingContext.scopeColor(scope.name)
          }
        }
      }
    renderElement.edges()
      .forEach { edge: EndpointPair<BindingGraph.ComponentNode> ->
        val (source, target) = edge
        (source.id link target.id)
      }
  }
}
