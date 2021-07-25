/*
 * Copyright 2021 Arunkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
