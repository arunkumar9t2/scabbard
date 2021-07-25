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

import dagger.model.Binding
import dagger.model.BindingGraph.*
import dagger.model.BindingKind
import dev.arunkumar.dot.dsl.DotGraphBuilder
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

  override fun DotGraphBuilder.build(renderElement: List<Edge>) {
    renderElement.forEach { edge ->
      val (source, target) = renderingContext.rootBindingGraph.network().incidentNodes(edge)
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
              "style" `=` "dotted"
              "label" `=` "delegates"
            }
            // Handle missing binding
            if (source is MissingBinding || target is MissingBinding) {
              "style" `=` "dashed"
              "arrowType" `=` "empty"
              val labelLocation = if (source is MissingBinding) "taillabel" else "headlabel"
              labelLocation `=` "Missing binding"
            }
          }
        }
      }
      is ChildFactoryMethodEdge -> {
        (source.id link target.id) {
          "style" `=` "dashed"
          "taillabel" `=` edge.factoryMethod()
        }
      }
      is SubcomponentCreatorBindingEdge -> {
        (source.id link target.id) {
          "style" `=` "dashed"
          "label" `=` "subcomponent"
          "headport" `=` "w"
        }
      }
    }
  }
}
