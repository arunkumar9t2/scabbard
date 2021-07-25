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
import dagger.model.ComponentPath
import dev.arunkumar.dot.dsl.DotGraphBuilder

/**
 * Data structure for holding data about a given component
 */
data class DaggerComponent(
  val componentPath: ComponentPath,
  val entryPointBindings: List<BindingGraph.MaybeBinding>,
  val dependencyBindings: List<BindingGraph.MaybeBinding>,
  val subcomponents: List<BindingGraph.ComponentNode>,
  val inheritedBindings: List<InheritedBinding>,
  val edges: List<BindingGraph.Edge>
) {
  /**
   * Renders the given component's dependency graph by delegating for appropriate `Renderer` instances.
   */
  class GraphRenderer(
    override val renderingContext: RenderingContext
  ) : Renderer<DaggerComponent> {
    override fun DotGraphBuilder.build(renderElement: DaggerComponent) {
      cluster("Entry Points") {
        graphAttributes {
          "labeljust" `=` "l"
          "label" `=` "Entry Points"
        }
        nodeAttributes {
          "shape" `=` "component"
          "penwidth" `=` 2
        }
        BindingsRenderer(renderingContext).render(this, renderElement.entryPointBindings)
      }

      cluster("Dependency Graph") {
        graphAttributes {
          "labeljust" `=` "l"
          "label" `=` "Dependency Graph"
        }
        BindingsRenderer(renderingContext).render(this, renderElement.dependencyBindings)
      }

      SimpleSubComponentRenderer(renderingContext).render(this, renderElement.subcomponents)
      InheritedBinding.GraphRenderer(renderingContext).render(this, renderElement.inheritedBindings)
      EdgeRenderer(renderingContext).render(this, renderElement.edges)
    }
  }
}
