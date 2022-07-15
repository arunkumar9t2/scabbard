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
import dagger.model.BindingGraph.MaybeBinding
import dagger.model.BindingGraph.MissingBinding
import dagger.model.BindingKind.BOUND_INSTANCE
import dagger.model.BindingKind.MEMBERS_INJECTION
import dev.arunkumar.dot.dsl.DotGraphBuilder

/**
 * Renders the actual dependency graph nodes in the current context
 * accounting for missing nodes, multibindings and entry points.
 */
class BindingsRenderer(
  override val renderingContext: RenderingContext
) : Renderer<List<MaybeBinding>> {

  private fun DotGraphBuilder.missingBinding(missingBinding: MissingBinding) {
    missingBinding.id {
      "label" `=` missingBinding.key()
        .toString() // TODO(arun) Update label calculation for MissingBinding
      "color" `=` "firebrick1"
    }
  }

  private fun DotGraphBuilder.binding(binding: Binding) {
    binding.id {
      "label" `=` binding.label
      "color" `=` binding.color
      when {
        binding.kind().isMultibinding -> {
          "shape" `=` "tab"
        }
        binding.kind() == BOUND_INSTANCE -> {
          "shape" `=` "parallelogram"
        }
        binding.isEntryPoint -> {
          "shape" `=` "component"
          if (binding.kind() == MEMBERS_INJECTION) {
            "label" `=` "inject (${binding.label})"
          }
        }
      }
    }
  }

  private fun DotGraphBuilder.addMultiBindings(currentComponentNodes: Sequence<MaybeBinding>) {
    currentComponentNodes
      .filterIsInstance<Binding>()
      .filter { it.kind().isMultibinding }
      .forEach { multiBinding ->
        val name = renderingContext.typeNameExtractor.extractName(multiBinding.key().type())
        cluster(name) {
          graphAttributes {
            "label" `=` name
            "labeljust" `=` "c"
            "style" `=` "rounded"
            "color" `=` "black"
          }

          binding(multiBinding)

          if (!multiBinding.isEntryPoint) {
            // If multbinding node was present as an entry point does not make sense to render its' content in
            // entry point cluster.
            renderingContext.rootBindingGraph
              .requestedBindings(multiBinding)
              .forEach { binding -> binding(binding) }
          }
        }
      }
  }

  override fun DotGraphBuilder.build(renderElement: List<MaybeBinding>) {
    renderElement.forEach { maybeBinding ->
      when (maybeBinding) {
        is Binding -> {
          if (!maybeBinding.kind().isMultibinding) {
            // Multibindings are handled in another cluster
            binding(maybeBinding)
          }
        }
        is MissingBinding -> missingBinding(maybeBinding)
      }
    }
    // Add multi bindings
    addMultiBindings(renderElement.asSequence())
  }
}
