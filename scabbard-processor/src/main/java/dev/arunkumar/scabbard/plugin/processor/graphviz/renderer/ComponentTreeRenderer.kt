package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import com.google.common.graph.EndpointPair
import com.google.common.graph.Graph
import dagger.model.BindingGraph
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.scabbard.plugin.parser.name
import dev.arunkumar.scabbard.plugin.processor.graphviz.RenderingContext
import dev.arunkumar.scabbard.plugin.util.component1
import dev.arunkumar.scabbard.plugin.util.component2

@Suppress("UnstableApiUsage")
class ComponentTreeRenderer(
  override val renderingContext: RenderingContext
) : Renderer<Graph<BindingGraph.ComponentNode>> {

  override fun DotGraphBuilder.build(renderingElement: Graph<BindingGraph.ComponentNode>) {
    renderingElement.nodes()
      .forEach { componentNode ->
        componentNode.id {
          "label" eq componentNode.label
          "href" eq renderingContext.href(componentNode)
          // TODO(arun) will multiple scopes be present? If yes, can it be visualized?
          componentNode.scopes().forEach { scope ->
            "color" eq renderingContext.scopeColor(scope.name)
          }
        }
      }
    renderingElement.edges()
      .forEach { edge: EndpointPair<BindingGraph.ComponentNode> ->
        val (source, target) = edge
        (source.id link target.id)
      }
  }
}
