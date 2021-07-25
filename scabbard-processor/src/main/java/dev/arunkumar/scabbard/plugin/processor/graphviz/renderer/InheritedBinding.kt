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
import dagger.model.BindingGraph
import dagger.model.Scope
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.scabbard.plugin.parser.NewLine
import dev.arunkumar.scabbard.plugin.parser.name

data class InheritedBinding(
  private val componentNode: BindingGraph.ComponentNode,
  private val bindings: List<Binding>
) {
  class GraphRenderer(
    override val renderingContext: RenderingContext
  ) : Renderer<List<InheritedBinding>> {
    override fun DotGraphBuilder.build(renderingElement: List<InheritedBinding>) {
      renderingElement.forEach { inheritedBinding ->
        val componentNode = inheritedBinding.componentNode
        val typeNameExtractor = renderingContext.typeNameExtractor

        val currentComponentPath = componentNode.componentPath()
        val componentType = currentComponentPath.currentComponent().asType()

        val componentName = buildString {
          append(typeNameExtractor.extractName(componentType))
          if (componentNode.scopes().isNotEmpty()) {
            append(NewLine)
            append(componentNode.scopes().map(Scope::name).last())
          }
        }
        cluster(typeNameExtractor.extractName(currentComponentPath)) {
          graphAttributes {
            "labeljust" `=` "c"
            "label" `=` "Inherited from $componentName"
            "style" `=` "dashed"
            "href" `=` renderingContext.href(componentNode)
            componentNode.scopes().map(Scope::name).forEach { scope ->
              "color" `=` renderingContext.scopeColor(scope)
            }
          }

          BindingsRenderer(renderingContext).render(this, inheritedBinding.bindings)
        }
      }
    }
  }
}
