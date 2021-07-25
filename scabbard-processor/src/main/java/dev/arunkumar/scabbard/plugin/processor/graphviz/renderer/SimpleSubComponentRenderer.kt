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

import dagger.model.BindingGraph
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.scabbard.plugin.parser.name

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
